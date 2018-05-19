package com.bittle.colorpicker.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Bittle on 12/31/16.
 */

public class ColorUtil {
    private ClosestColor closestColor;

    public ColorUtil() {
        closestColor = new ClosestColor();
    }

    public String colorToHex(int color) {
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

    public String RGBToHex(int r, int g, int b) {
        return colorToHex(Color.rgb(r, g, b));
    }

    static boolean isSmali(String s){
        return s.startsWith("-0x");
    }

    public int hexToColor(String hex) {
        if(isSmali(hex)){
            hex = smaliCodeToHex(hex);
        }
        if (!hex.contains("#")) {
            hex = "#" + hex;
        }
        if (hex.length() >= 6) {
            return Color.parseColor(hex);
        } else {
            Log.e("ERROR", "hexToColor, corrupt hex");
            try {
                return Color.parseColor(hex);
            } catch (Exception ig) {
                return Color.BLACK;
            }
        }
    }

    public boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114
                * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    public int lightenColor(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        red = lighten(red, fraction);
        green = lighten(green, fraction);
        blue = lighten(blue, fraction);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, red, green, blue);
    }

    public int darkenColor(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        red = darken(red, fraction);
        green = darken(green, fraction);
        blue = darken(blue, fraction);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, red, green, blue);
    }

    private static int darken(int color, double fraction) {
        return (int) Math.max(color - (color * fraction), 0);
    }

    private static int lighten(int color, double fraction) {
        return (int) Math.min(color + (color * fraction), 255);
    }

    static boolean validHex(String hex){
        boolean flag = true;
        if(hex.startsWith("#")){
            return true;
        }
        else{
            for(int x=0; x<6; x++){
                char c = hex.toLowerCase().charAt(x);

                if(!((c >= '0' && c <= '9') || (c>='a' && c<='f'))){
                    flag = false;
                }
            }
        }
        return flag;
    }

    public int invertColor(int color) {
        return (0xFFFFFF - color) | 0xFF000000;
    }

    public int getDominantColor(Bitmap bitmap) {
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

    public int getAverageColor(Bitmap bitmap) {
        Bitmap b = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = b.getPixel(0, 0);
        b.recycle();
        return color;
    }

    private static float[] colorToHsl(int color){
        float[] f = new float[3];
        android.support.v4.graphics.ColorUtils.colorToHSL(color, f);
        return f;
    }

    public static float[] rgbToHsl(int r, int g, int b) {
        return colorToHsl(Color.rgb(r,g,b));
    }

    private static double[] colorToLAB(int color){
        double[] f = new double[3];
        android.support.v4.graphics.ColorUtils.colorToLAB(color, f);
        return f;
    }

    static double[] rgbToLAB(int r, int g, int b){
        return colorToLAB(Color.rgb(r,g,b));
    }

    public String hexToSmaliCode(String hex) {
        if (hex.length() >= 6) {
            hex = hex.toUpperCase();
            hex = hex.replaceAll("0", "f").replaceAll("1", "e").replaceAll("2", "d")
                    .replaceAll("3", "c").replaceAll("4", "b").replaceAll("5", "a")
                    .replaceAll("6", "9").replaceAll("7", "8").replaceAll("8", "7")
                    .replaceAll("9", "6").replaceAll("A", "5").replaceAll("B", "4")
                    .replaceAll("C", "3").replaceAll("D", "2").replaceAll("E", "1")
                    .replaceAll("F", "0");
            return hex;
        } else {
            Log.e("ERROR", "hexToSmali, corrupt hex");
            return "";
        }
    }

    public String intColorToSmaliCode(int color) {
        return hexToSmaliCode(colorToHex(color));
    }

    static String smaliCodeToHex(String smali) {
        smali = smali.replace("-0x", "");
        if (smali.length() >= 6) {
            smali = smali.toLowerCase();
            smali = smali.replaceAll("f", "0").replaceAll("e", "1").replaceAll("d", "2")
                    .replaceAll("c", "3").replaceAll("b", "4").replaceAll("a", "5")
                    .replaceAll("9", "6").replaceAll("8", "7").replaceAll("7", "8")
                    .replaceAll("6", "9").replaceAll("5", "A").replaceAll("4", "B")
                    .replaceAll("3", "C").replaceAll("2", "D").replaceAll("1", "E")
                    .replaceAll("0", "F");
            return smali.trim();
        } else {
            Log.e("ERROR", "smaliToHex, corrupt smali");
            return "";
        }
    }

    private int[] colorToRGB(int color) {

        return new int[]{
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        };
    }

    public int[] hexToRGB(String hex) {
        return colorToRGB(hexToColor(hex));
    }

    public int[] colorToARGB(int color) {
        return new int[]{
                Color.alpha(color),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        };
    }

    public String getClosestColor(int color) {
        return closestColor.getColor(color);
    }

    public class ClosestColor {

        private ArrayList<ColorName> colorList = new ArrayList<ColorName>();

        private void initColorList() {
            colorList.add(new ColorName("AliceBlue", 0xF0, 0xF8, 0xFF));
            colorList.add(new ColorName("AntiqueWhite", 0xFA, 0xEB, 0xD7));
            colorList.add(new ColorName("Aqua", 0x00, 0xFF, 0xFF));
            colorList.add(new ColorName("Aquamarine", 0x7F, 0xFF, 0xD4));
            colorList.add(new ColorName("Azure", 0xF0, 0xFF, 0xFF));
            colorList.add(new ColorName("Beige", 0xF5, 0xF5, 0xDC));
            colorList.add(new ColorName("Bisque", 0xFF, 0xE4, 0xC4));
            colorList.add(new ColorName("Black", 0x00, 0x00, 0x00));
            colorList.add(new ColorName("BlanchedAlmond", 0xFF, 0xEB, 0xCD));
            colorList.add(new ColorName("Blue", 0x00, 0x00, 0xFF));
            colorList.add(new ColorName("BlueViolet", 0x8A, 0x2B, 0xE2));
            colorList.add(new ColorName("Brown", 0xA5, 0x2A, 0x2A));
            colorList.add(new ColorName("BurlyWood", 0xDE, 0xB8, 0x87));
            colorList.add(new ColorName("CadetBlue", 0x5F, 0x9E, 0xA0));
            colorList.add(new ColorName("Chartreuse", 0x7F, 0xFF, 0x00));
            colorList.add(new ColorName("Chocolate", 0xD2, 0x69, 0x1E));
            colorList.add(new ColorName("Coral", 0xFF, 0x7F, 0x50));
            colorList.add(new ColorName("CornflowerBlue", 0x64, 0x95, 0xED));
            colorList.add(new ColorName("Cornsilk", 0xFF, 0xF8, 0xDC));
            colorList.add(new ColorName("Crimson", 0xDC, 0x14, 0x3C));
            colorList.add(new ColorName("Cyan", 0x00, 0xFF, 0xFF));
            colorList.add(new ColorName("DarkBlue", 0x00, 0x00, 0x8B));
            colorList.add(new ColorName("DarkCyan", 0x00, 0x8B, 0x8B));
            colorList.add(new ColorName("DarkGoldenRod", 0xB8, 0x86, 0x0B));
            colorList.add(new ColorName("DarkGray", 0xA9, 0xA9, 0xA9));
            colorList.add(new ColorName("DarkGreen", 0x00, 0x64, 0x00));
            colorList.add(new ColorName("DarkKhaki", 0xBD, 0xB7, 0x6B));
            colorList.add(new ColorName("DarkMagenta", 0x8B, 0x00, 0x8B));
            colorList.add(new ColorName("DarkOliveGreen", 0x55, 0x6B, 0x2F));
            colorList.add(new ColorName("DarkOrange", 0xFF, 0x8C, 0x00));
            colorList.add(new ColorName("DarkOrchid", 0x99, 0x32, 0xCC));
            colorList.add(new ColorName("DarkRed", 0x8B, 0x00, 0x00));
            colorList.add(new ColorName("DarkSalmon", 0xE9, 0x96, 0x7A));
            colorList.add(new ColorName("DarkSeaGreen", 0x8F, 0xBC, 0x8F));
            colorList.add(new ColorName("DarkSlateBlue", 0x48, 0x3D, 0x8B));
            colorList.add(new ColorName("DarkSlateGray", 0x2F, 0x4F, 0x4F));
            colorList.add(new ColorName("DarkTurquoise", 0x00, 0xCE, 0xD1));
            colorList.add(new ColorName("DarkViolet", 0x94, 0x00, 0xD3));
            colorList.add(new ColorName("DeepPink", 0xFF, 0x14, 0x93));
            colorList.add(new ColorName("DeepSkyBlue", 0x00, 0xBF, 0xFF));
            colorList.add(new ColorName("DimGray", 0x69, 0x69, 0x69));
            colorList.add(new ColorName("DodgerBlue", 0x1E, 0x90, 0xFF));
            colorList.add(new ColorName("FireBrick", 0xB2, 0x22, 0x22));
            colorList.add(new ColorName("FloralWhite", 0xFF, 0xFA, 0xF0));
            colorList.add(new ColorName("ForestGreen", 0x22, 0x8B, 0x22));
            colorList.add(new ColorName("Fuchsia", 0xFF, 0x00, 0x80));
            colorList.add(new ColorName("Gainsboro", 0xDC, 0xDC, 0xDC));
            colorList.add(new ColorName("GhostWhite", 0xF8, 0xF8, 0xFF));
            colorList.add(new ColorName("Gold", 0xFF, 0xD7, 0x00));
            colorList.add(new ColorName("GoldenRod", 0xDA, 0xA5, 0x20));
            colorList.add(new ColorName("Gray", 0x80, 0x80, 0x80));
            colorList.add(new ColorName("Green", 0x00, 0x80, 0x00));
            colorList.add(new ColorName("GreenYellow", 0xAD, 0xFF, 0x2F));
            colorList.add(new ColorName("HoneyDew", 0xF0, 0xFF, 0xF0));
            colorList.add(new ColorName("HotPink", 0xFF, 0x69, 0xB4));
            colorList.add(new ColorName("IndianRed", 0xCD, 0x5C, 0x5C));
            colorList.add(new ColorName("Indigo", 0x4B, 0x00, 0x82));
            colorList.add(new ColorName("Ivory", 0xFF, 0xFF, 0xF0));
            colorList.add(new ColorName("Khaki", 0xF0, 0xE6, 0x8C));
            colorList.add(new ColorName("Lavender", 0xE6, 0xE6, 0xFA));
            colorList.add(new ColorName("LavenderBlush", 0xFF, 0xF0, 0xF5));
            colorList.add(new ColorName("LawnGreen", 0x7C, 0xFC, 0x00));
            colorList.add(new ColorName("LemonChiffon", 0xFF, 0xFA, 0xCD));
            colorList.add(new ColorName("LightBlue", 0xAD, 0xD8, 0xE6));
            colorList.add(new ColorName("LightCoral", 0xF0, 0x80, 0x80));
            colorList.add(new ColorName("LightCyan", 0xE0, 0xFF, 0xFF));
            colorList.add(new ColorName("LightGoldenRodYellow", 0xFA, 0xFA, 0xD2));
            colorList.add(new ColorName("LightGray", 0xD3, 0xD3, 0xD3));
            colorList.add(new ColorName("LightGreen", 0x90, 0xEE, 0x90));
            colorList.add(new ColorName("LightPink", 0xFF, 0xB6, 0xC1));
            colorList.add(new ColorName("LightSalmon", 0xFF, 0xA0, 0x7A));
            colorList.add(new ColorName("LightSeaGreen", 0x20, 0xB2, 0xAA));
            colorList.add(new ColorName("LightSkyBlue", 0x87, 0xCE, 0xFA));
            colorList.add(new ColorName("LightSlateGray", 0x77, 0x88, 0x99));
            colorList.add(new ColorName("LightSteelBlue", 0xB0, 0xC4, 0xDE));
            colorList.add(new ColorName("LightYellow", 0xFF, 0xFF, 0xE0));
            colorList.add(new ColorName("Lime", 0x00, 0xFF, 0x00));
            colorList.add(new ColorName("LimeGreen", 0x32, 0xCD, 0x32));
            colorList.add(new ColorName("Linen", 0xFA, 0xF0, 0xE6));
            colorList.add(new ColorName("Magenta", 0xFF, 0x00, 0xFF));
            colorList.add(new ColorName("Maroon", 0x80, 0x00, 0x00));
            colorList.add(new ColorName("MediumAquaMarine", 0x66, 0xCD, 0xAA));
            colorList.add(new ColorName("MediumBlue", 0x00, 0x00, 0xCD));
            colorList.add(new ColorName("MediumOrchid", 0xBA, 0x55, 0xD3));
            colorList.add(new ColorName("MediumPurple", 0x93, 0x70, 0xDB));
            colorList.add(new ColorName("MediumSeaGreen", 0x3C, 0xB3, 0x71));
            colorList.add(new ColorName("MediumSlateBlue", 0x7B, 0x68, 0xEE));
            colorList.add(new ColorName("MediumSpringGreen", 0x00, 0xFA, 0x9A));
            colorList.add(new ColorName("MediumTurquoise", 0x48, 0xD1, 0xCC));
            colorList.add(new ColorName("MediumVioletRed", 0xC7, 0x15, 0x85));
            colorList.add(new ColorName("MidnightBlue", 0x19, 0x19, 0x70));
            colorList.add(new ColorName("MintCream", 0xF5, 0xFF, 0xFA));
            colorList.add(new ColorName("MistyRose", 0xFF, 0xE4, 0xE1));
            colorList.add(new ColorName("Moccasin", 0xFF, 0xE4, 0xB5));
            colorList.add(new ColorName("NavajoWhite", 0xFF, 0xDE, 0xAD));
            colorList.add(new ColorName("Navy", 0x00, 0x00, 0x80));
            colorList.add(new ColorName("OldLace", 0xFD, 0xF5, 0xE6));
            colorList.add(new ColorName("Olive", 0x80, 0x80, 0x00));
            colorList.add(new ColorName("OliveDrab", 0x6B, 0x8E, 0x23));
            colorList.add(new ColorName("Orange", 0xFF, 0xA5, 0x00));
            colorList.add(new ColorName("OrangeRed", 0xFF, 0x45, 0x00));
            colorList.add(new ColorName("Orchid", 0xDA, 0x70, 0xD6));
            colorList.add(new ColorName("PaleGoldenRod", 0xEE, 0xE8, 0xAA));
            colorList.add(new ColorName("PaleGreen", 0x98, 0xFB, 0x98));
            colorList.add(new ColorName("PaleTurquoise", 0xAF, 0xEE, 0xEE));
            colorList.add(new ColorName("PaleVioletRed", 0xDB, 0x70, 0x93));
            colorList.add(new ColorName("PapayaWhip", 0xFF, 0xEF, 0xD5));
            colorList.add(new ColorName("PeachPuff", 0xFF, 0xDA, 0xB9));
            colorList.add(new ColorName("Peru", 0xCD, 0x85, 0x3F));
            colorList.add(new ColorName("Pink", 0xFF, 0xC0, 0xCB));
            colorList.add(new ColorName("Plum", 0xDD, 0xA0, 0xDD));
            colorList.add(new ColorName("PowderBlue", 0xB0, 0xE0, 0xE6));
            colorList.add(new ColorName("Purple", 0x80, 0x00, 0x80));
            colorList.add(new ColorName("Red", 0xFF, 0x00, 0x00));
            colorList.add(new ColorName("RosyBrown", 0xBC, 0x8F, 0x8F));
            colorList.add(new ColorName("RoyalBlue", 0x41, 0x69, 0xE1));
            colorList.add(new ColorName("SaddleBrown", 0x8B, 0x45, 0x13));
            colorList.add(new ColorName("Salmon", 0xFA, 0x80, 0x72));
            colorList.add(new ColorName("SandyBrown", 0xF4, 0xA4, 0x60));
            colorList.add(new ColorName("SeaGreen", 0x2E, 0x8B, 0x57));
            colorList.add(new ColorName("SeaShell", 0xFF, 0xF5, 0xEE));
            colorList.add(new ColorName("Sienna", 0xA0, 0x52, 0x2D));
            colorList.add(new ColorName("Silver", 0xC0, 0xC0, 0xC0));
            colorList.add(new ColorName("SkyBlue", 0x87, 0xCE, 0xEB));
            colorList.add(new ColorName("SlateBlue", 0x6A, 0x5A, 0xCD));
            colorList.add(new ColorName("SlateGray", 0x70, 0x80, 0x90));
            colorList.add(new ColorName("Snow", 0xFF, 0xFA, 0xFA));
            colorList.add(new ColorName("SpringGreen", 0x00, 0xFF, 0x7F));
            colorList.add(new ColorName("SteelBlue", 0x46, 0x82, 0xB4));
            colorList.add(new ColorName("Tan", 0xD2, 0xB4, 0x8C));
            colorList.add(new ColorName("Teal", 0x00, 0x80, 0x80));
            colorList.add(new ColorName("Thistle", 0xD8, 0xBF, 0xD8));
            colorList.add(new ColorName("Tomato", 0xFF, 0x63, 0x47));
            colorList.add(new ColorName("Turquoise", 0x40, 0xE0, 0xD0));
            colorList.add(new ColorName("Violet", 0xEE, 0x82, 0xEE));
            colorList.add(new ColorName("Wheat", 0xF5, 0xDE, 0xB3));
            colorList.add(new ColorName("White", 0xFF, 0xFF, 0xFF));
            colorList.add(new ColorName("WhiteSmoke", 0xF5, 0xF5, 0xF5));
            colorList.add(new ColorName("Yellow", 0xFF, 0xFF, 0x00));
            colorList.add(new ColorName("YellowGreen", 0x9A, 0xCD, 0x32));
        }

        ClosestColor() {
            initColorList();
        }

        String getColorNameFromRgb(int r, int g, int b) {

            ColorName closestMatch = null;
            double minMSE = Integer.MAX_VALUE;
            for (ColorName c : colorList) {
                double t = c.computeMSE(r, g, b);
                if (t < minMSE) {
                    minMSE = t;
                    closestMatch = c;
                }
            }

            //Log.d("FINAL RESULT", Color.rgb(r, g, b)+" == "+
            //        Color.rgb(closestMatch.r, closestMatch.g, closestMatch.b));

            if (closestMatch != null) {
                return closestMatch.getName();
            } else {
                return "No matched color name.";
            }
        }

        String getColor(int color) {
            return getColorNameFromRgb(Color.red(color), Color.green(color), Color.blue(color));
        }


    }

    /**
     * Created by Bittle on 1/29/17.
     */

    public static class ColorName implements Comparable<ColorName> {

        int r, g, b;
        public String name;
        public String hex;

        public ColorName(String name, int r, int g, int b2) {
            this.r = r;
            this.g = g;
            this.b = b2;
            this.name = name;
            hex = RGBToHex(r, g, b);
        }

        public ColorName(ColorName colorName) {
            this(colorName.name, colorName.r, colorName.g, colorName.b);
        }

        public ColorName(String name, int color) {
            this(name, Color.red(color), Color.green(color), Color.blue(color));
        }


        @Override
        public int compareTo(@NonNull ColorName colorName) {
            int pixel1 = (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);

            int pixel2 = (colorName.r & 0xFF) << 16 | (colorName.g & 0xFF) << 8 | (colorName.b & 0xFF);

            return Integer.compare(pixel1, pixel2);

        }

        private String colorToHex(int color) {
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

        public String RGBToHex(int r, int g, int b) {
            return colorToHex(Color.rgb(r, g, b));
        }


        public int getR() {
            return r;
        }

        public int getG() {
            return g;
        }

        public int getB() {
            return b;
        }

        public int colorNum() {
            return Color.rgb(r, g, b);
        }

        public int getColor() {
            return Color.rgb(r, g, b);
        }

        private double difference(double a, double b) {
            if (a > b) {
                return a - b;
            } else {
                return b - a;
            }
        }

        private double getChangeOfE_ab(double[] f1, double[] f2) {
            return Math.sqrt(Math.pow(f1[0] - f2[0], 2) + Math.pow(f1[1] - f2[1], 2) +
                    Math.pow(f1[2] - f2[2], 2));
        }


        private double getChangeOfH_ab(double changeE, double changeL, double changeC_ab) {
            return Math.sqrt((Math.pow(changeE, 2)) - (Math.pow(changeL, 2)) - (Math.pow(changeC_ab, 2)));
        }

        private double computeMSE(int r2, int g2, int b2) {
            //return computeClosestColor_CIE94(r2, g2, b2);
            return computeClosestColor_CIEDE2000(r2, g2, b2);
        }

        private double computeClosestColor_CIEDE2000(int r2, int g2, int b3) {
            double[] f1 = rgbToLAB(r, g, b);
            double[] f2 = rgbToLAB(r2, g2, b3);

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

        public String getName() {
            return name;
        }
    }
}