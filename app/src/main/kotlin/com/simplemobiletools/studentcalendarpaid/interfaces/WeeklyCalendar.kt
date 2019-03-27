package com.simplemobiletools.studentcalendarpaid.interfaces

import com.simplemobiletools.studentcalendarpaid.models.Event

interface WeeklyCalendar {
    fun updateWeeklyCalendar(events: ArrayList<Event>)
}
