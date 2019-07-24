package com.mc0239.recyclertableviewexample.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user u ORDER BY u.id DESC")
    LiveData<List<User>> getAll();

    //

    @Insert
    void insert(List<User> users);

    //

    @Query("DELETE FROM user")
    void truncate();
}
