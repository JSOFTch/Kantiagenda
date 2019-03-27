package com.simplemobiletools.studentcalendarpaid.services

import android.content.Intent
import android.widget.RemoteViewsService
import com.simplemobiletools.studentcalendarpaid.adapters.EventListWidgetAdapter

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent) = EventListWidgetAdapter(applicationContext)
}
