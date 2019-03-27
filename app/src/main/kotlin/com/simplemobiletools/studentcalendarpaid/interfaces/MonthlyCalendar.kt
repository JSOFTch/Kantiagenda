package com.simplemobiletools.studentcalendarpaid.interfaces

import android.content.Context
import com.simplemobiletools.studentcalendarpaid.models.DayMonthly
import org.joda.time.DateTime

interface MonthlyCalendar {
    fun updateMonthlyCalendar(context: Context, month: String, days: ArrayList<DayMonthly>, checkedEvents: Boolean, currTargetDate: DateTime)
}
