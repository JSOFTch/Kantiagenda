package com.simplemobiletools.studentcalendarpaid.activities

import androidx.room.*

@Dao
interface GradeDAO {
    @Query("SELECT * FROM " + GradeDBContract.GradeItem.TABLE_NAME)
    fun retrieveGradeList(): List<Grade>

    @Insert
    fun addNewGrade(grade: Grade): Long

    @Insert
    fun addNewValue(grade: Grade): Long



    @Update
    fun updateGrade(grade: Grade)

    @Delete
    fun deleteGrade(grade: Grade)
}
