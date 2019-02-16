package com.mc0239.recyclertableview;

import android.view.View;

public interface OnRowLongClickListener {
    boolean onLongClicked(View rowView, Object rowData, int position);
}
