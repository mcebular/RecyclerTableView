package com.mc0239.recyclertableviewexample.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.mc0239.recyclertableview.annotation.RecyclerTableColumn;
import com.mc0239.recyclertableview.annotation.RecyclerTableRow;
import com.mc0239.recyclertableviewexample.R;

@Entity
@RecyclerTableRow(value = R.layout.table_row_sample_usage)
public class User {

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

    /*
     * You can define *InView() method for a field to override the field's default string value.
     */
    public String surnameInView() {
        return surname + " " + id;
    }

}
