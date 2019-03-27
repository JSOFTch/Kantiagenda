package com.simplemobiletools.studentcalendarpaid.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simplemobiletools.studentcalendarpaid.extensions.recheckCalDAVCalendars
import com.simplemobiletools.studentcalendarpaid.extensions.updateWidgets

class CalDAVSyncReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.recheckCalDAVCalendars {
            context.updateWidgets()
        }
    }
}
