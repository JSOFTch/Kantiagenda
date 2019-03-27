package com.simplemobiletools.studentcalendarpaid.interfaces

import android.util.SparseArray
import com.simplemobiletools.studentcalendarpaid.models.DayYearly
import java.util.*

interface YearlyCalendar {
    fun updateYearlyCalendar(events: SparseArray<ArrayList<DayYearly>>, hashCode: Int)
}
