package com.mc0239.recyclertableviewexample.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM user u ORDER BY u.id DESC")
    fun getAll(): LiveData<List<User>>

    @Insert
    fun insert(users: List<User>)

    @Query("DELETE FROM user")
    fun truncate()

}