package com.mc0239.recyclertableviewexample.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mc0239.recyclertableview.annotation.RecyclerTableColumn
import com.mc0239.recyclertableview.annotation.RecyclerTableRow
import com.mc0239.recyclertableviewexample.R

@Entity
@RecyclerTableRow(value = R.layout.table_row_sample_usage)
data class User(
    @PrimaryKey @RecyclerTableColumn(R.id.textViewID) val id: Int,
    @RecyclerTableColumn(R.id.textViewUsername) val username: String?,
    @RecyclerTableColumn(R.id.textViewName) val name: String?,
    @RecyclerTableColumn(R.id.textViewSurname) val surname: String?,
    @RecyclerTableColumn(R.id.editTextNote) val note: String?,
) {
    fun surnameInView(): String {
        return "$surname $id"
    }
}
