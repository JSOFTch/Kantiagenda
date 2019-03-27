package com.simplemobiletools.studentcalendarpaid.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.simplemobiletools.studentcalendarpaid.extensions.dbHelper
import com.simplemobiletools.studentcalendarpaid.extensions.notifyEvent
import com.simplemobiletools.studentcalendarpaid.extensions.scheduleNextEventReminder
import com.simplemobiletools.studentcalendarpaid.extensions.updateListWidget
import com.simplemobiletools.studentcalendarpaid.helpers.EVENT_ID
import com.simplemobiletools.studentcalendarpaid.helpers.Formatter

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Simple Calendar")
        wakelock.acquire(3000)

        Thread {
            handleIntent(context, intent)
        }.start()
    }

    private fun handleIntent(context: Context, intent: Intent) {
        val id = intent.getIntExtra(EVENT_ID, -1)
        if (id == -1) {
            return
        }

        context.updateListWidget()
        val event = context.dbHelper.getEventWithId(id)
        if (event == null || event.getReminders().isEmpty()) {
            return
        }

        if (!event.ignoreEventOccurrences.contains(Formatter.getDayCodeFromTS(event.startTS).toInt())) {
            context.notifyEvent(event)
        }
        context.scheduleNextEventReminder(event, context.dbHelper)
    }
}
