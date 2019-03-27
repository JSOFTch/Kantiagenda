package com.simplemobiletools.studentcalendarpaid.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simplemobiletools.studentcalendarpaid.extensions.notifyRunningEvents
import com.simplemobiletools.studentcalendarpaid.extensions.recheckCalDAVCalendars
import com.simplemobiletools.studentcalendarpaid.extensions.scheduleAllEvents

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Thread {
            context.apply {
                scheduleAllEvents()
                notifyRunningEvents()
                recheckCalDAVCalendars {}
            }
        }.start()
    }
}
