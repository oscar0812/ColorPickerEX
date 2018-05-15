package com.bittle.colorpicker.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Bittle on 12/29/16.
 */

public class Toaster {
    public static Context context;
    private static Handler mUiHandler = new Handler(Looper.getMainLooper());

    public static void toast(final String line) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, line, Toast.LENGTH_LONG).show();
            }
        });
    }
}
