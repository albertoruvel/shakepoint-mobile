package com.shakepoint.mobile.watcher;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

/**
 * Created by jose.rubalcaba on 02/27/2018.
 */

public class BankCardFormatWatcher implements TextWatcher {

    private static final char space = ' ';

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        //remove spacing char
        if (editable.length() > 0 && (editable.length() % 5) == 0) {
            final char c = editable.charAt(editable.length() - 1);
            if (space == c) {
                editable.delete(editable.length() - 1, editable.length());
            }
        }
        //insert char where needed
        if (editable.length() > 0 && (editable.length() % 5) == 0) {
            char c = editable.charAt(editable.length() - 1);
            //only if its a digit where there should be a space, we insert a space
            if (Character.isDigit(c) && TextUtils.split(editable.toString(), String.valueOf(space)).length <= 3) {
                editable.insert(editable.length() - 1, String.valueOf(space));
            }
        }
    }
}
