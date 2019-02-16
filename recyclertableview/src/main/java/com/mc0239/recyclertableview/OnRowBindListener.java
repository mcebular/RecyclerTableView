package com.mc0239.recyclertableview;

import android.view.View;

public interface OnRowBindListener {
    void onRowBound(View rowView, Object rowData, int position);
    void onRowRecycled(View rowView);
}
