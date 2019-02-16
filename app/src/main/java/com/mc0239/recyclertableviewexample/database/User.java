package com.mc0239.recyclertableviewexample.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.mc0239.recyclertableview.annotation.RecyclerTableColumn;
import com.mc0239.recyclertableview.annotation.RecyclerTableRow;
import com.mc0239.recyclertableviewexample.R;

@Entity
@RecyclerTableRow(value = R.layout.table_row_sample_usage)
public class User {

    @RecyclerTableColumn(value = R.id.checkBoxSelected)
    public boolean checked;

    @PrimaryKey
    @RecyclerTableColumn(R.id.textViewID)
    public int id;

    @RecyclerTableColumn(R.id.textViewUsername)
    public String username;

    @RecyclerTableColumn(R.id.textViewName)
    public String name;

    @RecyclerTableColumn(R.id.textViewSurname)
    public String surname;

    @RecyclerTableColumn(R.id.editTextNote)
    public String note;

}