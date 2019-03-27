package com.simplemobiletools.studentcalendarpaid.interfaces

import org.joda.time.DateTime

interface NavigationListener {
    fun goLeft()

    fun goRight()

    fun goToDateTime(dateTime: DateTime)
}
