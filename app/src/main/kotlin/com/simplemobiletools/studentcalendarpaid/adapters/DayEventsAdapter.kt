package com.simplemobiletools.studentcalendarpaid.adapters

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.simplemobiletools.studentcalendarpaid.dialogs.DeleteEventDialog
import com.simplemobiletools.studentcalendarpaid.extensions.config
import com.simplemobiletools.studentcalendarpaid.extensions.dbHelper
import com.simplemobiletools.studentcalendarpaid.extensions.handleEventDeleting
import com.simplemobiletools.studentcalendarpaid.extensions.shareEvents
import com.simplemobiletools.studentcalendarpaid.helpers.Formatter
import com.simplemobiletools.studentcalendarpaid.helpers.ITEM_EVENT
import com.simplemobiletools.studentcalendarpaid.helpers.ITEM_EVENT_SIMPLE
import com.simplemobiletools.studentcalendarpaid.helpers.LOW_ALPHA
import com.simplemobiletools.studentcalendarpaid.models.Event
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.extensions.adjustAlpha
import com.simplemobiletools.commons.extensions.applyColorFilter
import com.simplemobiletools.commons.extensions.beInvisible
import com.simplemobiletools.commons.extensions.beInvisibleIf
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.event_item_day_view.view.*

class DayEventsAdapter(activity: com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity, val events: ArrayList<Event>, recyclerView: MyRecyclerView, itemClick: (Any) -> Unit)
    : MyRecyclerViewAdapter(activity, recyclerView, null, itemClick) {

    private val allDayString = resources.getString(com.simplemobiletools.studentcalendarpaid.R.string.all_day)
    private val replaceDescriptionWithLocation = activity.config.replaceDescription
    private val dimPastEvents = activity.config.dimPastEvents

    init {
        setupDragListener(true)
    }

    override fun getActionMenuId() = com.simplemobiletools.studentcalendarpaid.R.menu.cab_day

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {
        when (id) {
            com.simplemobiletools.studentcalendarpaid.R.id.cab_share -> shareEvents()
            com.simplemobiletools.studentcalendarpaid.R.id.cab_delete -> askConfirmDelete()
        }
    }

    override fun getSelectableItemCount() = events.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = events.getOrNull(position)?.id

    override fun getItemKeyPosition(key: Int) = events.indexOfFirst { it.id == key }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewAdapter.ViewHolder {
        val layoutId = when (viewType) {
            ITEM_EVENT -> com.simplemobiletools.studentcalendarpaid.R.layout.event_item_day_view
            else -> com.simplemobiletools.studentcalendarpaid.R.layout.event_item_day_view_simple
        }
        return createViewHolder(layoutId, parent)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val event = events[position]
        holder.bindView(event, true, true) { itemView, layoutPosition ->
            setupView(itemView, event)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = events.size

    override fun getItemViewType(position: Int): Int {
        val event = events[position]
        val detailField = if (replaceDescriptionWithLocation) event.location else event.description
        return if (detailField.isNotEmpty()) {
            ITEM_EVENT
        } else if (event.startTS == event.endTS) {
            ITEM_EVENT_SIMPLE
        } else if (event.getIsAllDay()) {
            val startCode = Formatter.getDayCodeFromTS(event.startTS)
            val endCode = Formatter.getDayCodeFromTS(event.endTS)
            if (startCode == endCode) {
                ITEM_EVENT_SIMPLE
            } else {
                ITEM_EVENT
            }
        } else {
            ITEM_EVENT
        }
    }

    private fun setupView(view: View, event: Event) {
        view.apply {
            event_item_frame.isSelected = selectedKeys.contains(event.id)
            event_item_title.text = event.title
            event_item_description?.text = if (replaceDescriptionWithLocation) event.location else event.description
            event_item_start.text = if (event.getIsAllDay()) allDayString else Formatter.getTimeFromTS(context, event.startTS)
            event_item_end?.beInvisibleIf(event.startTS == event.endTS)
            event_item_color_bar.background.applyColorFilter(event.color)

            if (event.startTS != event.endTS) {
                val startCode = Formatter.getDayCodeFromTS(event.startTS)
                val endCode = Formatter.getDayCodeFromTS(event.endTS)

                event_item_end?.apply {
                    text = Formatter.getTimeFromTS(context, event.endTS)
                    if (startCode != endCode) {
                        if (event.getIsAllDay()) {
                            text = Formatter.getDateFromCode(context, endCode, true)
                        } else {
                            append(" (${Formatter.getDateFromCode(context, endCode, true)})")
                        }
                    } else if (event.getIsAllDay()) {
                        beInvisible()
                    }
                }
            }

            var newTextColor = textColor
            if (dimPastEvents && event.isPastEvent) {
                newTextColor = newTextColor.adjustAlpha(LOW_ALPHA)
            }

            event_item_start.setTextColor(newTextColor)
            event_item_end?.setTextColor(newTextColor)
            event_item_title.setTextColor(newTextColor)
            event_item_description?.setTextColor(newTextColor)
        }
    }

    private fun shareEvents() = activity.shareEvents(selectedKeys.distinct())

    private fun askConfirmDelete() {
        val eventIds = selectedKeys.toMutableList()
        val eventsToDelete = events.filter { selectedKeys.contains(it.id) }
        val timestamps = eventsToDelete.map { it.startTS }
        val positions = getSelectedItemPositions()

        val hasRepeatableEvent = eventsToDelete.any { it.repeatInterval > 0 }
        DeleteEventDialog(activity, eventIds, hasRepeatableEvent) { it ->
            events.removeAll(eventsToDelete)

            val nonRepeatingEventIDs = eventsToDelete.asSequence().filter { it.repeatInterval == 0 }.map { it.id.toString() }.toList().toTypedArray()
            activity.dbHelper.deleteEvents(nonRepeatingEventIDs, true)

            val repeatingEventIDs = eventsToDelete.asSequence().filter { it.repeatInterval != 0 }.map { it.id }.toList()
            activity.handleEventDeleting(repeatingEventIDs, timestamps, it)
            removeSelectedItems(positions)
        }
    }
}
