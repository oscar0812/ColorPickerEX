package com.bittle.colorpicker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Bittle on 2/1/17.
 */

// this lags activities since it reads too much from space, lets memoize this
public class PrefUtil {

    private static final String PREFS_NAME = "HISTORY_FILE";
    private static final String CURRENT_INT = "CURRENT_INT";
    private static final int NUM_OF_ENTRIES_TO_SAVE = 40;
    public static final int MIN_NUM_OF_ENTRIES = NUM_OF_ENTRIES_TO_SAVE / 2;

    public static void write(String str, final Context c) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context

        if (!isAlreadyWritten(str, c)) {
            SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            int num = getNumberOfEntries(c);
            editor.putString("history" + num, str);
            incrementCurrentNumber(c, num);
            editor.apply();
        } else {

            bringRepeatToTop(str, c);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (getNumberOfEntries(c) >= NUM_OF_ENTRIES_TO_SAVE) {
                    reduceTo(MIN_NUM_OF_ENTRIES, c);
                    Toaster.toast("REDUCED to " + getNumberOfEntries(c), c);
                }
            }
        }).start();
    }

    private static void reduceTo(int num, Context c) {
        String[] strings = new String[num];
        try {
            int y = 0;
            for (int x = getNumberOfEntries(c); x >= num; x--) {
                strings[y] = get(y, c);
                y++;
            }
        } catch (Exception e) {
            Log.e("ERR = Pref -> reduceTo", e.toString());
        }
        clearAll(c);
        for (String string : strings) {
            if (!string.equals(""))
                write(string, c);
            else
                break;
        }
    }

    private static void bringRepeatToTop(String str, Context c) {
        //Log.e("STR",str+" - ");
        String[] hex = new String[getNumberOfEntries(c)];
        for (int y = 0; y < hex.length; y++) {
            hex[y] = get(y, c);
        }
        clearAll(c);
        for (String aHex : hex) {
            if (!aHex.equals(str)) {
                write(aHex, c);
            }
        }
        write(str, c);
    }

    public static String get(int n, Context c) {
        // Restore preferences
        SharedPreferences prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String restoredText = prefs.getString("history" + n, null);
        String str = "";
        if (restoredText != null) {
            str = prefs.getString("history" + n, "no history");//"No name defined" is the default value.
        }
        return str;
    }

    private static int getCurrentNumber(Context c) {
        SharedPreferences prefs = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int checkNumber = prefs.getInt(CURRENT_INT, -1);
        int num = 0;
        if (checkNumber != -1) {
            num = prefs.getInt(CURRENT_INT, 0);
        }
        return num;
    }

    private static void incrementCurrentNumber(Context c, int current) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(CURRENT_INT, current + 1);
        editor.apply();
    }

    private static void resetCurrentNumber(Context c) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(CURRENT_INT, 0);
        editor.apply();
    }

    public static String getAll(Context context) {
        StringBuilder str = new StringBuilder();
        int x = 0;
        while (true) {
            String s = PrefUtil.get(x++, context);
            if (s.equals("")) {
                break;
            } else {
                str.append(s);
                str.append("\n");
            }
        }
        return str.toString().trim();
    }

    public static void clearAll(Context c) {
        c.getSharedPreferences(PREFS_NAME, 0).edit().clear().apply();
        resetCurrentNumber(c);
    }

    public static int getNumberOfEntries(Context context) {
        int x = 0;
        while (true) {
            String s = PrefUtil.get(x++, context);
            if (s.equals("")) {
                break;
            }
        }
        return x - 1;
    }

    private static boolean isAlreadyWritten(String str, Context context) {
        if (ColorUtil.isSmali(str)) {
            str = ColorUtil.smaliCodeToHex(str);
        }
        int x = 0;
        boolean flag = false;
        while (true) {
            String s = PrefUtil.get(x++, context);
            if (s.equals("")) {
                break;
            }
            if (s.equals(str)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}