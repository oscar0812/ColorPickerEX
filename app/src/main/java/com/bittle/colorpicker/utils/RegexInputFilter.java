package com.bittle.colorpicker.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scribbled by oscartorres on 5/30/18.
 */
public class RegexInputFilter implements InputFilter {
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

    public static class HexInputFilter extends RegexInputFilter{
        public HexInputFilter() {
            // empty is also considered valid hex
            super("[A-Fa-f0-9]*");
        }
    }
}
