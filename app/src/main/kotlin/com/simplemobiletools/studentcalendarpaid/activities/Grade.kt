package com.simplemobiletools.studentcalendarpaid.activities

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = GradeDBContract.GradeItem.TABLE_NAME)


class Grade() {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumns._ID)
    var gradeId: Long? = null

    @ColumnInfo(name = GradeDBContract.GradeItem.COLUMN_NAME_TASK)
    var gradeDetails: String? = null


    @ColumnInfo(name = GradeDBContract.GradeItem.COLUMN_NOTE)
    var gradeValue: String? = null


    @ColumnInfo(name = GradeDBContract.GradeItem.COLUMN_NAME_DEADLINE)
    var gradeDeadline: String? = null

    @ColumnInfo(name = GradeDBContract.GradeItem.COLUMN_NAME_COMPLETED)
    var completed: Boolean? = false

    @Ignore
    constructor(gradeDetails: String?, gradeValue: String?, gradeDeadline: String?): this() {
        this.gradeDetails = gradeDetails
        this.gradeValue = gradeValue
        this.gradeDeadline = gradeDeadline
    }

    constructor(gradeId:Long, gradeDetails: String?,gradeValue: String?, gradeDeadline: String?, completed: Boolean) : this(gradeDetails, gradeValue, gradeDeadline) {
        this.gradeId = gradeId
        this.gradeValue = gradeValue
        this.completed = completed
    }

}
