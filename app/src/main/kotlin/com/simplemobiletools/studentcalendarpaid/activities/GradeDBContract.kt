package com.simplemobiletools.studentcalendarpaid.activities

import android.provider.BaseColumns

object GradeDBContract {
    const val DATABASE_VERSION = 2
    const val DATABASE_NAME = "grade_db"

    class GradeItem: BaseColumns {
        companion object {
            const val TABLE_NAME = "grade"
            const val COLUMN_NAME_TASK = "value"
            const val COLUMN_NOTE = "note"
            const val COLUMN_NAME_DEADLINE = "task_deadline"
            const val COLUMN_NAME_COMPLETED = "task_completed"


        }
    }
}
