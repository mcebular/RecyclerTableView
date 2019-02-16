package com.mc0239.recyclertableviewexample.rows;

import com.mc0239.recyclertableview.annotation.RecyclerTableColumn;
import com.mc0239.recyclertableview.annotation.RecyclerTableRow;
import com.mc0239.recyclertableviewexample.R;


@RecyclerTableRow(value = R.layout.table_row_sample_edittext, edittextViewId = R.id.editTextNote)
public class UserEditable {

    @RecyclerTableColumn(R.id.textViewID)
    public int id;

    @RecyclerTableColumn(R.id.textViewUsername)
    public String username;

    @RecyclerTableColumn(R.id.textViewName)
    public String name;

    @RecyclerTableColumn(R.id.textViewSurname)
    public String surname;

    @RecyclerTableColumn(value = R.id.editTextNote)
    public String note;

}
