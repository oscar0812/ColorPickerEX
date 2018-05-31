package com.bittle.colorpicker.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scribbled by oscartorres on 5/30/18.
 */
public class RegexInputFilter implements InputFilter {

    public static final InputFilter[] HexInputFilter = new InputFilter[] {
            // Only accept Hexidecimal values with a max length of 6,
            // it will also capitalize all input
            new RegexInputFilter("[A-Fa-f0-9]*"),
            new InputFilter.LengthFilter(6),
            new InputFilter.AllCaps()
    };

    private Pattern pattern;

    RegexInputFilter(String inputPattern) {
        this(Pattern.compile(inputPattern));
    }

    private RegexInputFilter(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("RegexInputFilter requires a regex.");
        }
        this.pattern = pattern;
    }

    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        Matcher matcher = this.pattern.matcher(charSequence);
        if (!matcher.matches()) return "";
        return null;
    }
}
