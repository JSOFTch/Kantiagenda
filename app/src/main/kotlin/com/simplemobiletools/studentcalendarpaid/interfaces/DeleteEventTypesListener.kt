package com.simplemobiletools.studentcalendarpaid.interfaces

import com.simplemobiletools.studentcalendarpaid.models.EventType
import java.util.*

interface DeleteEventTypesListener {
    fun deleteEventTypes(eventTypes: ArrayList<EventType>, deleteEvents: Boolean): Boolean
}
