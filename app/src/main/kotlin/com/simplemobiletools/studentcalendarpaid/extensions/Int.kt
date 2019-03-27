package com.simplemobiletools.studentcalendarpaid.extensions

import com.simplemobiletools.studentcalendarpaid.helpers.Formatter
import com.simplemobiletools.studentcalendarpaid.helpers.MONTH
import com.simplemobiletools.studentcalendarpaid.helpers.WEEK
import com.simplemobiletools.studentcalendarpaid.helpers.YEAR
import com.simplemobiletools.studentcalendarpaid.models.Event

fun Int.isTsOnProperDay(event: Event): Boolean {
    val dateTime = Formatter.getDateTimeFromTS(this)
    val power = Math.pow(2.0, (dateTime.dayOfWeek - 1).toDouble()).toInt()
    return event.repeatRule and power != 0
}

fun Int.isXWeeklyRepetition() = this != 0 && this % WEEK == 0

fun Int.isXMonthlyRepetition() = this != 0 && this % MONTH == 0

fun Int.isXYearlyRepetition() = this != 0 && this % YEAR == 0
