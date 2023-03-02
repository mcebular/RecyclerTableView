package com.mc0239.recyclertableviewexample.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private var instance: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "sample-db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }

            return instance!!
        }
    }

    fun generateSampleData() {
        userDao().truncate()

        val users: MutableList<User> = mutableListOf()
        for (i in 0 until 30) {
            val u = User(i, "johnd", "John", "Doe", null)
            users.add(u)
        }
        userDao().insert(users)
    }

}