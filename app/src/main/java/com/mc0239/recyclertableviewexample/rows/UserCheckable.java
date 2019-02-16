package com.mc0239.recyclertableviewexample.rows;

import com.mc0239.recyclertableview.annotation.RecyclerTableColumn;
import com.mc0239.recyclertableview.annotation.RecyclerTableRow;
import com.mc0239.recyclertableviewexample.R;

@RecyclerTableRow(value = R.layout.table_row_sample_checkbox, checkboxViewId = R.id.checkBoxSelected)
public class UserCheckable {

    @RecyclerTableColumn(R.id.checkBoxSelected)
    public boolean checked;

    @RecyclerTableColumn(R.id.textViewID)
    public int id;

    @RecyclerTableColumn(R.id.textViewUsername)
    public String username;

    @RecyclerTableColumn(R.id.textViewName)
    public String name;

    @RecyclerTableColumn(R.id.textViewSurname)
    public String surname;

}
