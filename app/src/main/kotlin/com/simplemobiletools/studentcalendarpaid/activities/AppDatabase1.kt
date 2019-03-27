package com.simplemobiletools.studentcalendarpaid.activities

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Grade::class], version = GradeDBContract.DATABASE_VERSION)
abstract class AppDatabase1 : RoomDatabase() {
    abstract fun gradeDAO(): GradeDAO
}
