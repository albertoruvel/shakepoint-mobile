package com.shakepoint.mobile.filter;

import android.text.InputFilter;
import android.text.Spanned;

public class MinMaxInputFilter implements InputFilter{
    private int min;
    private int max;

    public MinMaxInputFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try{
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input)) {
                return null;
            }
        }catch(Exception ex) {

        }

        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
