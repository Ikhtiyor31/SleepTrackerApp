package com.example.apparchitecturepersistence.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase_1 : RoomDatabase() {

    abstract val sleepDatabaseDao: SleepDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: SleepDatabase_1? = null
        fun getInstance(context: Context): SleepDatabase_1 {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase_1::class.java,
                        "sleep_history_database"
                    )
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}