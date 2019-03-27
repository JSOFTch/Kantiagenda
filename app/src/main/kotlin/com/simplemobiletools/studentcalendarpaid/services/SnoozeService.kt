package com.simplemobiletools.studentcalendarpaid.services

import android.app.IntentService
import android.content.Intent
import com.simplemobiletools.studentcalendarpaid.extensions.config
import com.simplemobiletools.studentcalendarpaid.extensions.dbHelper
import com.simplemobiletools.studentcalendarpaid.extensions.rescheduleReminder
import com.simplemobiletools.studentcalendarpaid.helpers.EVENT_ID

class SnoozeService : IntentService("Snooze") {
    override fun onHandleIntent(intent: Intent) {
        val eventId = intent.getIntExtra(EVENT_ID, 0)
        val event = dbHelper.getEventWithId(eventId)
        rescheduleReminder(event, config.snoozeTime)
    }
}
