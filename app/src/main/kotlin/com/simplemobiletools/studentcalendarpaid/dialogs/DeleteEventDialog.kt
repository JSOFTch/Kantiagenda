package com.simplemobiletools.studentcalendarpaid.dialogs

import android.app.Activity
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.studentcalendarpaid.helpers.DELETE_ALL_OCCURRENCES
import com.simplemobiletools.studentcalendarpaid.helpers.DELETE_FUTURE_OCCURRENCES
import com.simplemobiletools.studentcalendarpaid.helpers.DELETE_SELECTED_OCCURRENCE
import com.simplemobiletools.commons.extensions.setupDialogStuff
import kotlinx.android.synthetic.main.dialog_delete_event.view.*

class DeleteEventDialog(val activity: Activity, eventIds: List<Int>, hasRepeatableEvent: Boolean, val callback: (deleteRule: Int) -> Unit) {
    val dialog: AlertDialog?

    init {
        val view = activity.layoutInflater.inflate(com.simplemobiletools.studentcalendarpaid.R.layout.dialog_delete_event, null).apply {
            delete_event_repeat_description.beVisibleIf(hasRepeatableEvent)
            delete_event_radio_view.beVisibleIf(hasRepeatableEvent)
            if (!hasRepeatableEvent) {
                delete_event_radio_view.check(com.simplemobiletools.studentcalendarpaid.R.id.delete_event_all)
            }

            if (eventIds.size > 1) {
                delete_event_repeat_description.text = resources.getString(com.simplemobiletools.studentcalendarpaid.R.string.selection_contains_repetition)
            }
        }

        dialog = AlertDialog.Builder(activity)
                .setPositiveButton(com.simplemobiletools.studentcalendarpaid.R.string.yes) { dialog, which -> dialogConfirmed(view as ViewGroup) }
                .setNegativeButton(com.simplemobiletools.studentcalendarpaid.R.string.no, null)
                .create().apply {
                    activity.setupDialogStuff(view, this)
                }
    }

    private fun dialogConfirmed(view: ViewGroup) {
        val deleteRule = when (view.delete_event_radio_view.checkedRadioButtonId) {
            com.simplemobiletools.studentcalendarpaid.R.id.delete_event_all -> DELETE_ALL_OCCURRENCES
            com.simplemobiletools.studentcalendarpaid.R.id.delete_event_future -> DELETE_FUTURE_OCCURRENCES
            else -> DELETE_SELECTED_OCCURRENCE
        }
        dialog?.dismiss()
        callback(deleteRule)
    }
}
