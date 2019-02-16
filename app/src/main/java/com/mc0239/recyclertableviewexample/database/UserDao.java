package com.mc0239.recyclertableviewexample.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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
