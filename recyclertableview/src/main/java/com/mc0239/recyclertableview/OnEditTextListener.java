package com.mc0239.recyclertableview;

import android.widget.EditText;

public interface OnEditTextListener {
    void onEditTextFocus(EditText editText, Object rowData);
    void onEditTextChanged(EditText editText, String text);
    boolean onEnterPressed(EditText editText, Object rowData);
}