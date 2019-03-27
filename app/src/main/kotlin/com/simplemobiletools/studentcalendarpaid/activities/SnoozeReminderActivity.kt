package com.simplemobiletools.studentcalendarpaid.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simplemobiletools.studentcalendarpaid.extensions.config
import com.simplemobiletools.studentcalendarpaid.extensions.dbHelper
import com.simplemobiletools.studentcalendarpaid.extensions.rescheduleReminder
import com.simplemobiletools.studentcalendarpaid.helpers.EVENT_ID
import com.simplemobiletools.commons.extensions.showPickSecondsDialogHelper

class SnoozeReminderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showPickSecondsDialogHelper(config.snoozeTime, true, cancelCallback = { dialogCancelled() }) {
            val eventId = intent.getIntExtra(EVENT_ID, 0)
            val event = dbHelper.getEventWithId(eventId)
            config.snoozeTime = it / 60
            rescheduleReminder(event, it / 60)
            finishActivity()
        }
    }

    private fun dialogCancelled() {
        finishActivity()
    }

    private fun finishActivity() {
        finish()
        overridePendingTransition(0, 0)
    }
}
