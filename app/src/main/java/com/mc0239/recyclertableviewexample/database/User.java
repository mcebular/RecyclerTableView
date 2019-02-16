package com.mc0239.recyclertableviewexample.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    public int id;

    public String username;

    public String name;

    public String surname;
}
