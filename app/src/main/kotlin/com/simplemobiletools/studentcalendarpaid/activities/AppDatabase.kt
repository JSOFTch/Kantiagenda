package com.simplemobiletools.studentcalendarpaid.activities

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Task::class], version = TodoListDBContract.DATABASE_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDAO
}

