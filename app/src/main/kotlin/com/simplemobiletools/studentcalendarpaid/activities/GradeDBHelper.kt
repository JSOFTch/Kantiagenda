package com.simplemobiletools.studentcalendarpaid.activities

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class GradeDBHelper(context: Context): SQLiteOpenHelper(context, GradeDBContract.DATABASE_NAME, null, GradeDBContract.DATABASE_VERSION) {


    private val SQL_CREATE_ENTRIES = "CREATE TABLE " + GradeDBContract.GradeItem.TABLE_NAME + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            GradeDBContract.GradeItem.COLUMN_NAME_TASK + " TEXT, " +
            GradeDBContract.GradeItem.COLUMN_NOTE + " TEXT, " +
            GradeDBContract.GradeItem.COLUMN_NAME_DEADLINE + " TEXT, " +
            GradeDBContract.GradeItem.COLUMN_NAME_COMPLETED + " INTEGER)"


    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + GradeDBContract.GradeItem.TABLE_NAME

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun addNewGrade(grade: Grade): Grade {
        // Gets the data repository in write mode
        val db = this.writableDatabase

// Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(GradeDBContract.GradeItem.COLUMN_NAME_TASK, grade.gradeDetails)
        values.put(GradeDBContract.GradeItem.COLUMN_NOTE, grade.gradeValue)
        values.put(GradeDBContract.GradeItem.COLUMN_NAME_DEADLINE, grade.gradeDeadline)
        values.put(GradeDBContract.GradeItem.COLUMN_NAME_COMPLETED, grade.completed)

// Insert the new row, returning the primary key value of the new row
        val gradeId = db.insert(GradeDBContract.GradeItem.TABLE_NAME, null, values);
        grade.gradeId = gradeId



        return grade
    }

    fun updtadeGrade(grade: Grade) {
        val db = this.writableDatabase

// New value for one column
        val values = ContentValues()
        values.put(GradeDBContract.GradeItem.COLUMN_NAME_TASK, grade.gradeDetails)
        values.put(GradeDBContract.GradeItem.COLUMN_NOTE, grade.gradeValue)
        values.put(GradeDBContract.GradeItem.COLUMN_NAME_DEADLINE, grade.gradeDeadline)
        values.put(GradeDBContract.GradeItem.COLUMN_NAME_COMPLETED, grade.completed)

// Which row to update, based on the title
        val selection = BaseColumns._ID + " = ?"
        val selectionArgs = arrayOf(grade.gradeId.toString())

        db.update(TodoListDBContract.TodoListItem.TABLE_NAME, values, selection, selectionArgs)

    }

    fun deleteGrade(grade: Grade) {
        val db = this.writableDatabase
        // Define 'where' part of query.
        val selection = BaseColumns._ID + " = ?"
// Specify arguments in placeholder order.
        val selectionArgs = arrayOf(grade.gradeId.toString())
// Issue SQL statement.
        db.delete(GradeDBContract.GradeItem.TABLE_NAME, selection, selectionArgs)
    }

    fun retrieveGradeList(): ArrayList<Grade> {
        val db = this.readableDatabase

        val projection = arrayOf<String>(BaseColumns._ID,
                GradeDBContract.GradeItem.COLUMN_NAME_TASK,
                GradeDBContract.GradeItem.COLUMN_NOTE,
                GradeDBContract.GradeItem.COLUMN_NAME_DEADLINE,
                GradeDBContract.GradeItem.COLUMN_NAME_COMPLETED)

        val cursor = db.query(GradeDBContract.GradeItem.TABLE_NAME, projection,
                null, null, null, null, null)

        val gradeList = ArrayList<Grade>()
        while (cursor.moveToNext()) {
            val grade = Grade(cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(GradeDBContract.GradeItem.COLUMN_NAME_TASK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(GradeDBContract.GradeItem.COLUMN_NOTE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(GradeDBContract.GradeItem.COLUMN_NAME_DEADLINE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(GradeDBContract.GradeItem.COLUMN_NAME_COMPLETED)) == 1)
            gradeList.add(grade)
        }
        cursor.close()

        return gradeList
    }

}
