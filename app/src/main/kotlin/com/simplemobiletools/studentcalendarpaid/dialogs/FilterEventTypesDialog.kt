package com.simplemobiletools.studentcalendarpaid.dialogs

import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.studentcalendarpaid.adapters.FilterEventTypeAdapter
import com.simplemobiletools.studentcalendarpaid.extensions.config
import com.simplemobiletools.studentcalendarpaid.extensions.dbHelper
import com.simplemobiletools.commons.extensions.setupDialogStuff
import kotlinx.android.synthetic.main.dialog_filter_event_types.view.*

class FilterEventTypesDialog(val activity: com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity, val callback: () -> Unit) {
    var dialog: AlertDialog
    val view = activity.layoutInflater.inflate(com.simplemobiletools.studentcalendarpaid.R.layout.dialog_filter_event_types, null)

    init {
        val eventTypes = activity.dbHelper.getEventTypesSync()
        val displayEventTypes = activity.config.displayEventTypes
        view.filter_event_types_list.adapter = FilterEventTypeAdapter(activity, eventTypes, displayEventTypes)

        dialog = AlertDialog.Builder(activity)
                .setPositiveButton(com.simplemobiletools.studentcalendarpaid.R.string.ok) { dialogInterface, i -> confirmEventTypes() }
                .setNegativeButton(com.simplemobiletools.studentcalendarpaid.R.string.cancel, null)
                .create().apply {
                    activity.setupDialogStuff(view, this, com.simplemobiletools.studentcalendarpaid.R.string.filter_events_by_type)
                }
    }

    private fun confirmEventTypes() {
        val selectedItems = (view.filter_event_types_list.adapter as FilterEventTypeAdapter).getSelectedItemsSet()
        if (activity.config.displayEventTypes != selectedItems) {
            activity.config.displayEventTypes = selectedItems
            callback()
        }
        dialog.dismiss()
    }
}
