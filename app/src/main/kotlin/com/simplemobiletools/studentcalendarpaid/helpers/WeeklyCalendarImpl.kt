package com.simplemobiletools.studentcalendarpaid.helpers

import android.content.Context
import com.simplemobiletools.studentcalendarpaid.extensions.dbHelper
import com.simplemobiletools.studentcalendarpaid.interfaces.WeeklyCalendar
import com.simplemobiletools.studentcalendarpaid.models.Event
import com.simplemobiletools.commons.helpers.WEEK_SECONDS
import java.util.*

class WeeklyCalendarImpl(val callback: WeeklyCalendar, val context: Context) {
    var mEvents = ArrayList<Event>()

    fun updateWeeklyCalendar(weekStartTS: Int) {
        val endTS = weekStartTS + WEEK_SECONDS
        context.dbHelper.getEvents(weekStartTS, endTS, applyTypeFilter = true) {
            mEvents = it
            callback.updateWeeklyCalendar(it)
        }
    }
}
