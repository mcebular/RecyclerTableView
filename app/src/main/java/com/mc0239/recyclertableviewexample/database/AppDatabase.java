package com.mc0239.recyclertableviewexample.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.util.ArrayList;


@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "sample-db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public void generateSampleData() {
        userDao().truncate();

        // Prepare some data for the table
        ArrayList<User> users = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            User u = new User();

            u.id = i;
            u.username = "johnd";
            u.name = "John";
            u.surname = "Doe ";

            users.add(u);
        }
        userDao().insert(users);
    }
}
