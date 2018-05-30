package com.bittle.colorpicker.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.bittle.colorpicker.realm.ColorModel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Bittle on 12/31/16.
 */

public class ColorUtil {

    private ColorUtil() {
        // dont instantiate
    }

    private static ArrayList<ColorModel> colorList = new ArrayList<>();

    public static String colorToHex(int color) {
        try {
            if (color == Integer.MAX_VALUE) {
                return "";
            } else {
                String str = String.format("#%X", color);
                return str.substring(3);
            }
        } catch (Exception e) {
            try {
                return String.format("#%X", color);
            } catch (Exception er) {
                Log.d("ERROR", "ColorUtil -> colorToHex: " + er.toString());
                return "" + color;
            }
        }
    }

    public static String rgbToHex(int r, int g, int b) {
        return colorToHex(Color.rgb(r, g, b));
    }

    private static boolean isSmali(String s) {
        return s.contains("0x");
    }

    public static int hexToColor(String hex) {
        if (isSmali(hex)) {
            hex = smaliToHex(hex);
        }
        if (!hex.startsWith("#")) {
            hex = "#" + hex;
        }
        if (isValidHex(hex)) {
            return Color.parseColor(hex);
        } else {
            return Color.BLACK;
        }
    }

    public static boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114
                * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    public static int lightenColor(int color, double fraction) {
        if (fraction > 1)
            fraction = 1;

        int red = lighten(Color.red(color), fraction);
        int green = lighten(Color.green(color), fraction);
        int blue = lighten(Color.blue(color), fraction);

        return Color.rgb(red, green, blue);
    }

    private static int lighten(int color, double fraction) {
        return (int) ((color * (1 - fraction) / 255 + fraction) * 255);
    }

    public static int darkenColor(int color, double fraction) {
        if (fraction > 1)
            fraction = 1;

        int red = darken(Color.red(color), fraction);
        int green = darken(Color.green(color), fraction);
        int blue = darken(Color.blue(color), fraction);

        return Color.rgb(red, green, blue);
    }

    private static int darken(int color, double fraction) {
        return (int) ((color * (1 - fraction) / 255) * 255);
    }

    public static boolean isValidHex(String hex) {
        return Pattern.compile("([A-Fa-f0-9]{6})$").matcher(hex).matches();
    }

    public static int invertColor(int color) {
        return (0xFFFFFF - color) | 0xFF000000;
    }

    public static int getDominantColor(Bitmap bitmap) {
        List<Palette.Swatch> swatchesTemp = Palette.from(bitmap).generate().getSwatches();
        List<Palette.Swatch> swatches = new ArrayList<>(swatchesTemp);

        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch t1, Palette.Swatch t2) {
                return t2.getPopulation() - t1.getPopulation();
            }
        });
        return swatches.size() > 0 ? swatches.get(0).getRgb() : Color.BLACK;
    }

    public static int getAverageColor(Bitmap bitmap) {
        Bitmap b = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = b.getPixel(0, 0);
        b.recycle();
        return color;
    }

    private static double[] colorToLAB(int color) {
        double[] f = new double[3];
        android.support.v4.graphics.ColorUtils.colorToLAB(color, f);
        return f;
    }

    private static double[] hexToLAB(String hex) {
        return colorToLAB(hexToColor(hex));
    }

    /*
     If the value is positive, then there's no need to do anything.
     If the value is negative, simply add 0x1000000 to the
     value. e.g. -0x111112 + 0x1000000 = 0xeeeeee, so a value
     of -0x111112 corresponds to a hex color code of #eeeeee.
     */
    public static String[] hexToSmaliCode(String hex) {
        if (hex.startsWith("#")) {
            // remove leading #
            hex = hex.substring(1);
        }
        String[] smali = new String[2];

        if (isValidHex(hex)) {
            smali[0] = "0x" + hex.toLowerCase();

            try {
                BigInteger h = new BigInteger(hex, 16);
                BigInteger shift = new BigInteger("1000000", 16);

                String product = h.subtract(shift).toString(16);
                // product => -111112, need -0x111112
                smali[1] = "-0x" + product.substring(1);
            } catch (Exception e) {
                Log.e("ERROR", "hexToSmali -> corrupt hex");
            }

        } else {
            Log.e("ERROR", "hexToSmali => corrupt hex");
        }
        return smali;
    }

    public static String smaliToHex(String smali) {
        if (smali.contains("0x")) {
            // remove 0x
            smali = smali.replace("0x", "");
        }

        try {
            if (smali.startsWith("-")) {
                // negative smali (add 0x1000000)
                BigInteger h = new BigInteger(smali, 16);
                BigInteger shift = new BigInteger("1000000", 16);

                return h.add(shift).toString(16);
            }
            // positive (smali = hex)
            return smali;
        } catch (Exception e) {
            Log.e("ERROR", "smaliToHex");
            return "";
        }
    }

    private static int[] colorToRGB(int color) {

        return new int[]{
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        };
    }

    public static int[] hexToRGB(String hex) {
        return colorToRGB(hexToColor(hex));
    }

    public static String getClosestColor(int color) {
        return getClosestColor(new ColorModel(colorToHex(color)));
    }

    public static String getClosestColor(ColorModel current) {
        ColorModel closestMatch = null;
        double minMSE = Integer.MAX_VALUE;
        for (ColorModel c : getColorList()) {
            double t = computeClosestColor_CIEDE2000(current, c);
            if (t < minMSE) {
                minMSE = t;
                closestMatch = c;
            }
        }

        if (closestMatch != null) {
            return closestMatch.getName();
        } else {
            return "No matched color name.";
        }
    }

    private static double computeClosestColor_CIEDE2000(ColorModel current, ColorModel incoming) {
        double[] f1 = hexToLAB(current.getHex());
        double[] f2 = hexToLAB(incoming.getHex());

        final double L1 = f1[0], a1 = f1[1], a2 = f2[1], L2 = f2[0], b1 = f1[2], b2 = f2[2];

        double Lmean = (L1 + L2) / 2.0; //ok
        double C1 = Math.sqrt(a1 * a1 + b1 * b1); //ok
        double C2 = Math.sqrt(a2 * a2 + b2 * b2); //ok
        double Cmean = (C1 + C2) / 2.0; //ok

        double G = (1 - Math.sqrt(Math.pow(Cmean, 7) / (Math.pow(Cmean, 7) + Math.pow(25, 7)))) / 2; //ok
        double a1prime = a1 * (1 + G); //ok
        double a2prime = a2 * (1 + G); //ok

        double C1prime = Math.sqrt(a1prime * a1prime + b1 * b1); //ok
        double C2prime = Math.sqrt(a2prime * a2prime + b2 * b2); //ok
        double Cmeanprime = (C1prime + C2prime) / 2; //ok

        double h1prime = Math.atan2(b1, a1prime) +
                2 * Math.PI * (Math.atan2(b1, a1prime) < 0 ? 1 : 0);
        double h2prime = Math.atan2(b2, a2prime) +
                2 * Math.PI * (Math.atan2(b2, a2prime) < 0 ? 1 : 0);
        double Hmeanprime = ((Math.abs(h1prime - h2prime) > Math.PI)
                ? (h1prime + h2prime + 2 * Math.PI) / 2 : (h1prime + h2prime) / 2); //ok

        double T = 1.0 - 0.17 * Math.cos(Hmeanprime - Math.PI / 6.0) + 0.24
                * Math.cos(2 * Hmeanprime) + 0.32 * Math.cos(3 * Hmeanprime + Math.PI / 30)
                - 0.2 * Math.cos(4 * Hmeanprime - 21 * Math.PI / 60); //ok

        double deltahprime = ((Math.abs(h1prime - h2prime) <= Math.PI)
                ? h2prime - h1prime : (h2prime <= h1prime) ? h2prime - h1prime +
                2 * Math.PI : h2prime - h1prime - 2 * Math.PI); //ok

        double deltaLprime = L2 - L1; //ok
        double deltaCprime = C2prime - C1prime; //ok
        double deltaHprime = 2.0 * Math.sqrt(C1prime * C2prime) *
                Math.sin(deltahprime / 2.0); //ok
        double SL = 1.0 + ((0.015 * (Lmean - 50) * (Lmean - 50)) /
                (Math.sqrt(20 + (Lmean - 50) * (Lmean - 50)))); //ok
        double SC = 1.0 + 0.045 * Cmeanprime; //ok
        double SH = 1.0 + 0.015 * Cmeanprime * T; //ok

        double deltaTheta = (30 * Math.PI / 180) *
                Math.exp(-((180 / Math.PI * Hmeanprime - 275) / 25) *
                        ((180 / Math.PI * Hmeanprime - 275) / 25));
        double RC = (2 * Math.sqrt(Math.pow(Cmeanprime, 7) /
                (Math.pow(Cmeanprime, 7) + Math.pow(25, 7))));
        double RT = (-RC * Math.sin(2 * deltaTheta));

        double KL = 1;
        double KC = 1;
        double KH = 1;

        return Math.sqrt(
                ((deltaLprime / (KL * SL)) * (deltaLprime / (KL * SL))) +
                        ((deltaCprime / (KC * SC)) * (deltaCprime / (KC * SC))) +
                        ((deltaHprime / (KH * SH)) * (deltaHprime / (KH * SH))) +
                        (RT * (deltaCprime / (KC * SC)) * (deltaHprime / (KH * SH)))
        );
    }

    private static void initColorList() {
        colorList.add(new ColorModel("AliceBlue", 0xF0, 0xF8, 0xFF));
        colorList.add(new ColorModel("AntiqueWhite", 0xFA, 0xEB, 0xD7));
        colorList.add(new ColorModel("Aqua", 0x00, 0xFF, 0xFF));
        colorList.add(new ColorModel("Aquamarine", 0x7F, 0xFF, 0xD4));
        colorList.add(new ColorModel("Azure", 0xF0, 0xFF, 0xFF));
        colorList.add(new ColorModel("Beige", 0xF5, 0xF5, 0xDC));
        colorList.add(new ColorModel("Bisque", 0xFF, 0xE4, 0xC4));
        colorList.add(new ColorModel("Black", 0x00, 0x00, 0x00));
        colorList.add(new ColorModel("BlanchedAlmond", 0xFF, 0xEB, 0xCD));
        colorList.add(new ColorModel("Blue", 0x00, 0x00, 0xFF));
        colorList.add(new ColorModel("BlueViolet", 0x8A, 0x2B, 0xE2));
        colorList.add(new ColorModel("Brown", 0xA5, 0x2A, 0x2A));
        colorList.add(new ColorModel("BurlyWood", 0xDE, 0xB8, 0x87));
        colorList.add(new ColorModel("CadetBlue", 0x5F, 0x9E, 0xA0));
        colorList.add(new ColorModel("Chartreuse", 0x7F, 0xFF, 0x00));
        colorList.add(new ColorModel("Chocolate", 0xD2, 0x69, 0x1E));
        colorList.add(new ColorModel("Coral", 0xFF, 0x7F, 0x50));
        colorList.add(new ColorModel("CornflowerBlue", 0x64, 0x95, 0xED));
        colorList.add(new ColorModel("Cornsilk", 0xFF, 0xF8, 0xDC));
        colorList.add(new ColorModel("Crimson", 0xDC, 0x14, 0x3C));
        colorList.add(new ColorModel("Cyan", 0x00, 0xFF, 0xFF));
        colorList.add(new ColorModel("DarkBlue", 0x00, 0x00, 0x8B));
        colorList.add(new ColorModel("DarkCyan", 0x00, 0x8B, 0x8B));
        colorList.add(new ColorModel("DarkGoldenRod", 0xB8, 0x86, 0x0B));
        colorList.add(new ColorModel("DarkGray", 0xA9, 0xA9, 0xA9));
        colorList.add(new ColorModel("DarkGreen", 0x00, 0x64, 0x00));
        colorList.add(new ColorModel("DarkKhaki", 0xBD, 0xB7, 0x6B));
        colorList.add(new ColorModel("DarkMagenta", 0x8B, 0x00, 0x8B));
        colorList.add(new ColorModel("DarkOliveGreen", 0x55, 0x6B, 0x2F));
        colorList.add(new ColorModel("DarkOrange", 0xFF, 0x8C, 0x00));
        colorList.add(new ColorModel("DarkOrchid", 0x99, 0x32, 0xCC));
        colorList.add(new ColorModel("DarkRed", 0x8B, 0x00, 0x00));
        colorList.add(new ColorModel("DarkSalmon", 0xE9, 0x96, 0x7A));
        colorList.add(new ColorModel("DarkSeaGreen", 0x8F, 0xBC, 0x8F));
        colorList.add(new ColorModel("DarkSlateBlue", 0x48, 0x3D, 0x8B));
        colorList.add(new ColorModel("DarkSlateGray", 0x2F, 0x4F, 0x4F));
        colorList.add(new ColorModel("DarkTurquoise", 0x00, 0xCE, 0xD1));
        colorList.add(new ColorModel("DarkViolet", 0x94, 0x00, 0xD3));
        colorList.add(new ColorModel("DeepPink", 0xFF, 0x14, 0x93));
        colorList.add(new ColorModel("DeepSkyBlue", 0x00, 0xBF, 0xFF));
        colorList.add(new ColorModel("DimGray", 0x69, 0x69, 0x69));
        colorList.add(new ColorModel("DodgerBlue", 0x1E, 0x90, 0xFF));
        colorList.add(new ColorModel("FireBrick", 0xB2, 0x22, 0x22));
        colorList.add(new ColorModel("FloralWhite", 0xFF, 0xFA, 0xF0));
        colorList.add(new ColorModel("ForestGreen", 0x22, 0x8B, 0x22));
        colorList.add(new ColorModel("Fuchsia", 0xFF, 0x00, 0x80));
        colorList.add(new ColorModel("Gainsboro", 0xDC, 0xDC, 0xDC));
        colorList.add(new ColorModel("GhostWhite", 0xF8, 0xF8, 0xFF));
        colorList.add(new ColorModel("Gold", 0xFF, 0xD7, 0x00));
        colorList.add(new ColorModel("GoldenRod", 0xDA, 0xA5, 0x20));
        colorList.add(new ColorModel("Gray", 0x80, 0x80, 0x80));
        colorList.add(new ColorModel("Green", 0x00, 0x80, 0x00));
        colorList.add(new ColorModel("GreenYellow", 0xAD, 0xFF, 0x2F));
        colorList.add(new ColorModel("HoneyDew", 0xF0, 0xFF, 0xF0));
        colorList.add(new ColorModel("HotPink", 0xFF, 0x69, 0xB4));
        colorList.add(new ColorModel("IndianRed", 0xCD, 0x5C, 0x5C));
        colorList.add(new ColorModel("Indigo", 0x4B, 0x00, 0x82));
        colorList.add(new ColorModel("Ivory", 0xFF, 0xFF, 0xF0));
        colorList.add(new ColorModel("Khaki", 0xF0, 0xE6, 0x8C));
        colorList.add(new ColorModel("Lavender", 0xE6, 0xE6, 0xFA));
        colorList.add(new ColorModel("LavenderBlush", 0xFF, 0xF0, 0xF5));
        colorList.add(new ColorModel("LawnGreen", 0x7C, 0xFC, 0x00));
        colorList.add(new ColorModel("LemonChiffon", 0xFF, 0xFA, 0xCD));
        colorList.add(new ColorModel("LightBlue", 0xAD, 0xD8, 0xE6));
        colorList.add(new ColorModel("LightCoral", 0xF0, 0x80, 0x80));
        colorList.add(new ColorModel("LightCyan", 0xE0, 0xFF, 0xFF));
        colorList.add(new ColorModel("LightGoldenRodYellow", 0xFA, 0xFA, 0xD2));
        colorList.add(new ColorModel("LightGray", 0xD3, 0xD3, 0xD3));
        colorList.add(new ColorModel("LightGreen", 0x90, 0xEE, 0x90));
        colorList.add(new ColorModel("LightPink", 0xFF, 0xB6, 0xC1));
        colorList.add(new ColorModel("LightSalmon", 0xFF, 0xA0, 0x7A));
        colorList.add(new ColorModel("LightSeaGreen", 0x20, 0xB2, 0xAA));
        colorList.add(new ColorModel("LightSkyBlue", 0x87, 0xCE, 0xFA));
        colorList.add(new ColorModel("LightSlateGray", 0x77, 0x88, 0x99));
        colorList.add(new ColorModel("LightSteelBlue", 0xB0, 0xC4, 0xDE));
        colorList.add(new ColorModel("LightYellow", 0xFF, 0xFF, 0xE0));
        colorList.add(new ColorModel("Lime", 0x00, 0xFF, 0x00));
        colorList.add(new ColorModel("LimeGreen", 0x32, 0xCD, 0x32));
        colorList.add(new ColorModel("Linen", 0xFA, 0xF0, 0xE6));
        colorList.add(new ColorModel("Magenta", 0xFF, 0x00, 0xFF));
        colorList.add(new ColorModel("Maroon", 0x80, 0x00, 0x00));
        colorList.add(new ColorModel("MediumAquaMarine", 0x66, 0xCD, 0xAA));
        colorList.add(new ColorModel("MediumBlue", 0x00, 0x00, 0xCD));
        colorList.add(new ColorModel("MediumOrchid", 0xBA, 0x55, 0xD3));
        colorList.add(new ColorModel("MediumPurple", 0x93, 0x70, 0xDB));
        colorList.add(new ColorModel("MediumSeaGreen", 0x3C, 0xB3, 0x71));
        colorList.add(new ColorModel("MediumSlateBlue", 0x7B, 0x68, 0xEE));
        colorList.add(new ColorModel("MediumSpringGreen", 0x00, 0xFA, 0x9A));
        colorList.add(new ColorModel("MediumTurquoise", 0x48, 0xD1, 0xCC));
        colorList.add(new ColorModel("MediumVioletRed", 0xC7, 0x15, 0x85));
        colorList.add(new ColorModel("MidnightBlue", 0x19, 0x19, 0x70));
        colorList.add(new ColorModel("MintCream", 0xF5, 0xFF, 0xFA));
        colorList.add(new ColorModel("MistyRose", 0xFF, 0xE4, 0xE1));
        colorList.add(new ColorModel("Moccasin", 0xFF, 0xE4, 0xB5));
        colorList.add(new ColorModel("NavajoWhite", 0xFF, 0xDE, 0xAD));
        colorList.add(new ColorModel("Navy", 0x00, 0x00, 0x80));
        colorList.add(new ColorModel("OldLace", 0xFD, 0xF5, 0xE6));
        colorList.add(new ColorModel("Olive", 0x80, 0x80, 0x00));
        colorList.add(new ColorModel("OliveDrab", 0x6B, 0x8E, 0x23));
        colorList.add(new ColorModel("Orange", 0xFF, 0xA5, 0x00));
        colorList.add(new ColorModel("OrangeRed", 0xFF, 0x45, 0x00));
        colorList.add(new ColorModel("Orchid", 0xDA, 0x70, 0xD6));
        colorList.add(new ColorModel("PaleGoldenRod", 0xEE, 0xE8, 0xAA));
        colorList.add(new ColorModel("PaleGreen", 0x98, 0xFB, 0x98));
        colorList.add(new ColorModel("PaleTurquoise", 0xAF, 0xEE, 0xEE));
        colorList.add(new ColorModel("PaleVioletRed", 0xDB, 0x70, 0x93));
        colorList.add(new ColorModel("PapayaWhip", 0xFF, 0xEF, 0xD5));
        colorList.add(new ColorModel("PeachPuff", 0xFF, 0xDA, 0xB9));
        colorList.add(new ColorModel("Peru", 0xCD, 0x85, 0x3F));
        colorList.add(new ColorModel("Pink", 0xFF, 0xC0, 0xCB));
        colorList.add(new ColorModel("Plum", 0xDD, 0xA0, 0xDD));
        colorList.add(new ColorModel("PowderBlue", 0xB0, 0xE0, 0xE6));
        colorList.add(new ColorModel("Purple", 0x80, 0x00, 0x80));
        colorList.add(new ColorModel("Red", 0xFF, 0x00, 0x00));
        colorList.add(new ColorModel("RosyBrown", 0xBC, 0x8F, 0x8F));
        colorList.add(new ColorModel("RoyalBlue", 0x41, 0x69, 0xE1));
        colorList.add(new ColorModel("SaddleBrown", 0x8B, 0x45, 0x13));
        colorList.add(new ColorModel("Salmon", 0xFA, 0x80, 0x72));
        colorList.add(new ColorModel("SandyBrown", 0xF4, 0xA4, 0x60));
        colorList.add(new ColorModel("SeaGreen", 0x2E, 0x8B, 0x57));
        colorList.add(new ColorModel("SeaShell", 0xFF, 0xF5, 0xEE));
        colorList.add(new ColorModel("Sienna", 0xA0, 0x52, 0x2D));
        colorList.add(new ColorModel("Silver", 0xC0, 0xC0, 0xC0));
        colorList.add(new ColorModel("SkyBlue", 0x87, 0xCE, 0xEB));
        colorList.add(new ColorModel("SlateBlue", 0x6A, 0x5A, 0xCD));
        colorList.add(new ColorModel("SlateGray", 0x70, 0x80, 0x90));
        colorList.add(new ColorModel("Snow", 0xFF, 0xFA, 0xFA));
        colorList.add(new ColorModel("SpringGreen", 0x00, 0xFF, 0x7F));
        colorList.add(new ColorModel("SteelBlue", 0x46, 0x82, 0xB4));
        colorList.add(new ColorModel("Tan", 0xD2, 0xB4, 0x8C));
        colorList.add(new ColorModel("Teal", 0x00, 0x80, 0x80));
        colorList.add(new ColorModel("Thistle", 0xD8, 0xBF, 0xD8));
        colorList.add(new ColorModel("Tomato", 0xFF, 0x63, 0x47));
        colorList.add(new ColorModel("Turquoise", 0x40, 0xE0, 0xD0));
        colorList.add(new ColorModel("Violet", 0xEE, 0x82, 0xEE));
        colorList.add(new ColorModel("Wheat", 0xF5, 0xDE, 0xB3));
        colorList.add(new ColorModel("White", 0xFF, 0xFF, 0xFF));
        colorList.add(new ColorModel("WhiteSmoke", 0xF5, 0xF5, 0xF5));
        colorList.add(new ColorModel("Yellow", 0xFF, 0xFF, 0x00));
        colorList.add(new ColorModel("YellowGreen", 0x9A, 0xCD, 0x32));
    }

    public static ArrayList<ColorModel> getColorList() {
        if (colorList.isEmpty())
            initColorList();
        return colorList;
    }
}