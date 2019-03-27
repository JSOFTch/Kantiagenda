package com.simplemobiletools.studentcalendarpaid.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.simplemobiletools.studentcalendarpaid.adapters.ManageEventTypesAdapter
import com.simplemobiletools.studentcalendarpaid.dialogs.EditEventTypeDialog
import com.simplemobiletools.studentcalendarpaid.extensions.dbHelper
import com.simplemobiletools.studentcalendarpaid.interfaces.DeleteEventTypesListener
import com.simplemobiletools.studentcalendarpaid.models.EventType
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.updateTextColors
import kotlinx.android.synthetic.main.activity_manage_event_types.*
import java.util.*

class ManageEventTypesActivity : com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity(), DeleteEventTypesListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.simplemobiletools.studentcalendarpaid.R.layout.activity_manage_event_types)

        getEventTypes()
        updateTextColors(manage_event_types_list)
    }

    private fun showEventTypeDialog(eventType: EventType? = null) {
        EditEventTypeDialog(this, eventType?.copy()) {
            getEventTypes()
        }
    }

    private fun getEventTypes() {
        dbHelper.getEventTypes {
            runOnUiThread {
                val adapter = ManageEventTypesAdapter(this, it, this, manage_event_types_list) {
                    showEventTypeDialog(it as EventType)
                }
                manage_event_types_list.adapter = adapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.simplemobiletools.studentcalendarpaid.R.menu.menu_event_types, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.simplemobiletools.studentcalendarpaid.R.id.add_event_type -> showEventTypeDialog()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun deleteEventTypes(eventTypes: ArrayList<EventType>, deleteEvents: Boolean): Boolean {
        if (eventTypes.any { it.caldavCalendarId != 0 }) {
            toast(com.simplemobiletools.studentcalendarpaid.R.string.unsync_caldav_calendar)
            if (eventTypes.size == 1) {
                return false
            }
        }

        Thread {
            dbHelper.deleteEventTypes(eventTypes, deleteEvents) {
                if (it == 0) {
                    toast(com.simplemobiletools.studentcalendarpaid.R.string.unknown_error_occurred)
                }
            }
        }.start()
        return true
    }
}
