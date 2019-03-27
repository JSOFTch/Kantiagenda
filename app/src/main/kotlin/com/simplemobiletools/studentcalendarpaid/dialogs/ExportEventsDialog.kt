package com.simplemobiletools.studentcalendarpaid.dialogs

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.studentcalendarpaid.adapters.FilterEventTypeAdapter
import com.simplemobiletools.studentcalendarpaid.extensions.dbHelper
import com.simplemobiletools.commons.extensions.*
import kotlinx.android.synthetic.main.dialog_export_events.view.*
import java.io.File
import java.util.*

class ExportEventsDialog(val activity: com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity, val path: String, val callback: (exportPastEvents: Boolean, file: File, eventTypes: HashSet<String>) -> Unit) {

    init {
        val view = (activity.layoutInflater.inflate(com.simplemobiletools.studentcalendarpaid.R.layout.dialog_export_events, null) as ViewGroup).apply {
            export_events_folder.text = activity.humanizePath(path)
            export_events_filename.setText("${activity.getString(com.simplemobiletools.studentcalendarpaid.R.string.events)}_${activity.getCurrentFormattedDateTime()}")

            activity.dbHelper.getEventTypes {
                val eventTypes = HashSet<String>()
                it.mapTo(eventTypes) { it.id.toString() }

                activity.runOnUiThread {
                    export_events_types_list.adapter = FilterEventTypeAdapter(activity, it, eventTypes)
                    if (it.size > 1) {
                        export_events_pick_types.beVisible()

                        val margin = activity.resources.getDimension(com.simplemobiletools.studentcalendarpaid.R.dimen.normal_margin).toInt()
                        (export_events_checkbox.layoutParams as LinearLayout.LayoutParams).leftMargin = margin
                    }
                }
            }
        }

        AlertDialog.Builder(activity)
                .setPositiveButton(com.simplemobiletools.studentcalendarpaid.R.string.ok, null)
                .setNegativeButton(com.simplemobiletools.studentcalendarpaid.R.string.cancel, null)
                .create().apply {
                    activity.setupDialogStuff(view, this, com.simplemobiletools.studentcalendarpaid.R.string.export_events) {
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            val filename = view.export_events_filename.value
                            when {
                                filename.isEmpty() -> activity.toast(com.simplemobiletools.studentcalendarpaid.R.string.empty_name)
                                filename.isAValidFilename() -> {
                                    val file = File(path, "$filename.ics")
                                    if (file.exists()) {
                                        activity.toast(com.simplemobiletools.studentcalendarpaid.R.string.name_taken)
                                        return@setOnClickListener
                                    }

                                    val eventTypes = (view.export_events_types_list.adapter as FilterEventTypeAdapter).getSelectedItemsSet()
                                    callback(view.export_events_checkbox.isChecked, file, eventTypes)
                                    dismiss()
                                }
                                else -> activity.toast(com.simplemobiletools.studentcalendarpaid.R.string.invalid_name)
                            }
                        }
                    }
                }
    }
}
