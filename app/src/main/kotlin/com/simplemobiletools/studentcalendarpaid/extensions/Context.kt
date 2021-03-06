package com.simplemobiletools.studentcalendarpaid.extensions

import android.accounts.Account
import android.annotation.SuppressLint
import android.app.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.ContentObserver
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import com.simplemobiletools.studentcalendarpaid.helpers.*
import com.simplemobiletools.studentcalendarpaid.helpers.Formatter
import com.simplemobiletools.studentcalendarpaid.models.*
import com.simplemobiletools.studentcalendarpaid.receivers.CalDAVSyncReceiver
import com.simplemobiletools.studentcalendarpaid.receivers.NotificationReceiver
import com.simplemobiletools.studentcalendarpaid.services.SnoozeService
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.SILENT
import com.simplemobiletools.commons.helpers.WEEK_SECONDS
import com.simplemobiletools.commons.helpers.YEAR_SECONDS
import com.simplemobiletools.commons.helpers.isOreoPlus
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*

val Context.config: Config get() = Config.newInstance(applicationContext)

val Context.dbHelper: DBHelper get() = DBHelper.newInstance(applicationContext)

fun Context.updateWidgets() {
    val widgetIDs = AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(ComponentName(applicationContext, MyWidgetMonthlyProvider::class.java))
    if (widgetIDs.isNotEmpty()) {
        Intent(applicationContext, MyWidgetMonthlyProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs)
            sendBroadcast(this)
        }
    }

    updateListWidget()
}

fun Context.updateListWidget() {
    val widgetIDs = AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(ComponentName(applicationContext, MyWidgetListProvider::class.java))
    if (widgetIDs.isNotEmpty()) {
        Intent(applicationContext, MyWidgetListProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs)
            sendBroadcast(this)
        }
    }
}

fun Context.scheduleAllEvents() {
    val events = dbHelper.getEventsAtReboot()
    events.forEach {
        scheduleNextEventReminder(it, dbHelper)
    }
}

fun Context.scheduleNextEventReminder(event: Event, dbHelper: DBHelper, activity: com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity? = null) {
    if (event.getReminders().isEmpty()) {
        activity?.toast(com.simplemobiletools.studentcalendarpaid.R.string.saving)
        return
    }

    val now = getNowSeconds()
    val reminderSeconds = event.getReminders().reversed().map { it * 60 }
    dbHelper.getEvents(now, now + YEAR, event.id, false) {
        if (it.isNotEmpty()) {
            for (curEvent in it) {
                for (curReminder in reminderSeconds) {
                    if (curEvent.getEventStartTS() - curReminder > now) {
                        scheduleEventIn((curEvent.getEventStartTS() - curReminder) * 1000L, curEvent, activity)
                        return@getEvents
                    }
                }
            }
        }

        activity?.toast(com.simplemobiletools.studentcalendarpaid.R.string.saving)
    }
}

fun Context.scheduleEventIn(notifTS: Long, event: Event, activity: com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity? = null) {
    if (notifTS < System.currentTimeMillis()) {
        activity?.toast(com.simplemobiletools.studentcalendarpaid.R.string.saving)
        return
    }

    val newNotifTS = notifTS + 1000
    if (activity != null) {
        val secondsTillNotification = (newNotifTS - System.currentTimeMillis()) / 1000
        val msg = String.format(getString(com.simplemobiletools.studentcalendarpaid.R.string.reminder_triggers_in), formatSecondsToTimeString(secondsTillNotification.toInt()))
        activity.toast(msg)
    }

    val pendingIntent = getNotificationIntent(applicationContext, event)
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, newNotifTS, pendingIntent)
}

fun Context.cancelNotification(id: Int) {
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(id)
}

private fun getNotificationIntent(context: Context, event: Event): PendingIntent {
    val intent = Intent(context, NotificationReceiver::class.java)
    intent.putExtra(EVENT_ID, event.id)
    intent.putExtra(EVENT_OCCURRENCE_TS, event.startTS)
    return PendingIntent.getBroadcast(context, event.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.getRepetitionText(seconds: Int) = when (seconds) {
    0 -> getString(com.simplemobiletools.studentcalendarpaid.R.string.no_repetition)
    DAY -> getString(com.simplemobiletools.studentcalendarpaid.R.string.daily)
    WEEK -> getString(com.simplemobiletools.studentcalendarpaid.R.string.weekly)
    MONTH -> getString(com.simplemobiletools.studentcalendarpaid.R.string.monthly)
    YEAR -> getString(com.simplemobiletools.studentcalendarpaid.R.string.yearly)
    else -> {
        when {
            seconds % YEAR == 0 -> resources.getQuantityString(com.simplemobiletools.studentcalendarpaid.R.plurals.years, seconds / YEAR, seconds / YEAR)
            seconds % MONTH == 0 -> resources.getQuantityString(com.simplemobiletools.studentcalendarpaid.R.plurals.months, seconds / MONTH, seconds / MONTH)
            seconds % WEEK == 0 -> resources.getQuantityString(com.simplemobiletools.studentcalendarpaid.R.plurals.weeks, seconds / WEEK, seconds / WEEK)
            else -> resources.getQuantityString(com.simplemobiletools.studentcalendarpaid.R.plurals.days, seconds / DAY, seconds / DAY)
        }
    }
}

fun Context.notifyRunningEvents() {
    dbHelper.getRunningEvents().filter { it.getReminders().isNotEmpty() }.forEach {
        notifyEvent(it)
    }
}

fun Context.notifyEvent(originalEvent: Event) {
    var event = originalEvent.copy()
    val currentSeconds = getNowSeconds()

    var eventStartTS = if (event.getIsAllDay()) Formatter.getDayStartTS(Formatter.getDayCodeFromTS(event.startTS)) else event.startTS
    // make sure refer to the proper repeatable event instance with "Tomorrow", or the specific date
    if (event.repeatInterval != 0 && eventStartTS - event.reminder1Minutes * 60 < currentSeconds) {
        val events = dbHelper.getRepeatableEventsFor(currentSeconds - WEEK_SECONDS, currentSeconds + YEAR_SECONDS, event.id)
        for (currEvent in events) {
            eventStartTS = if (currEvent.getIsAllDay()) Formatter.getDayStartTS(Formatter.getDayCodeFromTS(currEvent.startTS)) else currEvent.startTS
            if (eventStartTS - currEvent.reminder1Minutes * 60 > currentSeconds) {
                break
            }

            event = currEvent
        }
    }

    val pendingIntent = getPendingIntent(applicationContext, event)
    val startTime = Formatter.getTimeFromTS(applicationContext, event.startTS)
    val endTime = Formatter.getTimeFromTS(applicationContext, event.endTS)
    val startDate = Formatter.getDateFromTS(event.startTS)

    val displayedStartDate = when (startDate) {
        LocalDate.now() -> ""
        LocalDate.now().plusDays(1) -> getString(com.simplemobiletools.studentcalendarpaid.R.string.tomorrow)
        else -> "${Formatter.getDateFromCode(this, Formatter.getDayCodeFromTS(event.startTS))},"
    }

    val timeRange = if (event.getIsAllDay()) getString(com.simplemobiletools.studentcalendarpaid.R.string.all_day) else getFormattedEventTime(startTime, endTime)
    val descriptionOrLocation = if (config.replaceDescription) event.location else event.description
    val content = "$displayedStartDate $timeRange $descriptionOrLocation".trim()
    val notification = getNotification(pendingIntent, event, content)
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(event.id, notification)
}

@SuppressLint("NewApi")
fun Context.getNotification(pendingIntent: PendingIntent, event: Event, content: String, publicVersion: Boolean = false): Notification {
    var soundUri = config.reminderSoundUri
    if (soundUri == SILENT) {
        soundUri = ""
    } else {
        grantReadUriPermission(soundUri)
    }

    // create a new channel for every new sound uri as the new Android Oreo notification system is fundamentally broken
    if (soundUri != config.lastSoundUri) {
        config.lastReminderChannel = System.currentTimeMillis()
        config.lastSoundUri = soundUri
    }

    val channelId = "simple_calendar_${config.lastReminderChannel}_${config.reminderAudioStream}"
    if (isOreoPlus()) {
        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setLegacyStreamType(config.reminderAudioStream)
                .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = resources.getString(com.simplemobiletools.studentcalendarpaid.R.string.event_reminders)
        val importance = NotificationManager.IMPORTANCE_HIGH
        NotificationChannel(channelId, name, importance).apply {
            setBypassDnd(true)
            enableLights(true)
            lightColor = event.color
            enableVibration(false)
            setSound(Uri.parse(soundUri), audioAttributes)
            notificationManager.createNotificationChannel(this)
        }
    }

    val contentTitle = if (publicVersion) resources.getString(com.simplemobiletools.studentcalendarpaid.R.string.app_name) else event.title
    val contentText = if (publicVersion) resources.getString(com.simplemobiletools.studentcalendarpaid.R.string.public_event_notification_text) else content

    val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(com.simplemobiletools.studentcalendarpaid.R.drawable.ic_calendar)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_LIGHTS)
            .setAutoCancel(true)
            .setSound(Uri.parse(soundUri), config.reminderAudioStream)
            .setChannelId(channelId)
            .addAction(com.simplemobiletools.studentcalendarpaid.R.drawable.ic_snooze, getString(com.simplemobiletools.studentcalendarpaid.R.string.snooze), getSnoozePendingIntent(this, event))

    if (config.vibrateOnReminder) {
        val vibrateArray = LongArray(2) { 500 }
        builder.setVibrate(vibrateArray)
    }

    if (!publicVersion) {
        builder.setPublicVersion(getNotification(pendingIntent, event, content, true))
    }

    val notification = builder.build()
    if (config.loopReminders) {
        notification.flags = notification.flags or Notification.FLAG_INSISTENT
    }
    return notification
}

private fun getFormattedEventTime(startTime: String, endTime: String) = if (startTime == endTime) startTime else "$startTime \u2013 $endTime"

private fun getPendingIntent(context: Context, event: Event): PendingIntent {
    val intent = Intent(context, com.simplemobiletools.studentcalendarpaid.activities.EventActivity::class.java)
    intent.putExtra(EVENT_ID, event.id)
    intent.putExtra(EVENT_OCCURRENCE_TS, event.startTS)
    return PendingIntent.getActivity(context, event.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

private fun getSnoozePendingIntent(context: Context, event: Event): PendingIntent {
    val snoozeClass = if (context.config.useSameSnooze) SnoozeService::class.java else com.simplemobiletools.studentcalendarpaid.activities.SnoozeReminderActivity::class.java
    val intent = Intent(context, snoozeClass).setAction("Snooze")
    intent.putExtra(EVENT_ID, event.id)
    return if (context.config.useSameSnooze) {
        PendingIntent.getService(context, event.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    } else {
        PendingIntent.getActivity(context, event.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

fun Context.rescheduleReminder(event: Event?, minutes: Int) {
    if (event != null) {
        applicationContext.scheduleEventIn(System.currentTimeMillis() + minutes * 60000, event)
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(event.id)
    }
}

fun Context.launchNewEventIntent(dayCode: String = Formatter.getTodayCode()) {
    Intent(applicationContext, com.simplemobiletools.studentcalendarpaid.activities.EventActivity::class.java).apply {
        putExtra(NEW_EVENT_START_TS, getNewEventTimestampFromCode(dayCode))
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(this)
    }
}

fun Context.getNewEventTimestampFromCode(dayCode: String): Int {
    val currHour = DateTime(System.currentTimeMillis(), DateTimeZone.getDefault()).hourOfDay
    val dateTime = Formatter.getLocalDateTimeFromCode(dayCode).withHourOfDay(currHour)
    val newDateTime = dateTime.plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
    // make sure the date doesn't change
    return newDateTime.withDate(dateTime.year, dateTime.monthOfYear, dateTime.dayOfMonth).seconds()
}

fun Context.getCurrentOffset() = SimpleDateFormat("Z", Locale.getDefault()).format(Date())

fun Context.getSyncedCalDAVCalendars() = CalDAVHandler(applicationContext).getCalDAVCalendars(null, config.caldavSyncedCalendarIDs)

fun Context.recheckCalDAVCalendars(callback: () -> Unit) {
    if (config.caldavSync) {
        Thread {
            CalDAVHandler(applicationContext).refreshCalendars(null, callback)
            updateWidgets()
        }.start()
    }
}

fun Context.scheduleCalDAVSync(activate: Boolean) {
    val syncIntent = Intent(applicationContext, CalDAVSyncReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, syncIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (activate) {
        val syncCheckInterval = 2 * AlarmManager.INTERVAL_HOUR
        try {
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + syncCheckInterval, syncCheckInterval, pendingIntent)
        } catch (ignored: SecurityException) {
        }
    } else {
        alarm.cancel(pendingIntent)
    }
}

fun Context.syncCalDAVCalendars(activity: com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity?, calDAVSyncObserver: ContentObserver) {
    Thread {
        val uri = CalendarContract.Calendars.CONTENT_URI
        contentResolver.unregisterContentObserver(calDAVSyncObserver)
        contentResolver.registerContentObserver(uri, false, calDAVSyncObserver)
        refreshCalDAVCalendars(activity, config.caldavSyncedCalendarIDs)
    }.start()
}

fun Context.refreshCalDAVCalendars(activity: com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity?, ids: String) {
    val uri = CalendarContract.Calendars.CONTENT_URI
    val accounts = HashSet<Account>()
    val calendars = CalDAVHandler(applicationContext).getCalDAVCalendars(activity, ids)
    calendars.forEach {
        accounts.add(Account(it.accountName, it.accountType))
    }

    Bundle().apply {
        putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        accounts.forEach {
            ContentResolver.requestSync(it, uri.authority, this)
        }
    }
}

fun Context.addDayNumber(rawTextColor: Int, day: DayMonthly, linearLayout: LinearLayout, dayLabelHeight: Int, callback: (Int) -> Unit) {
    var textColor = rawTextColor
    if (!day.isThisMonth)
        textColor = textColor.adjustAlpha(LOW_ALPHA)

    (View.inflate(applicationContext, com.simplemobiletools.studentcalendarpaid.R.layout.day_monthly_number_view, null) as TextView).apply {
        setTextColor(textColor)
        text = day.value.toString()
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        linearLayout.addView(this)

        if (day.isToday) {
            val bgColor = Color.parseColor("#311b92")

            val primaryColor = bgColor
            setTextColor(primaryColor.getContrastColor())
            if (dayLabelHeight == 0) {
                onGlobalLayout {
                    val height = this@apply.height
                    if (height > 0) {
                        callback(height)
                        addTodaysBackground(this, resources, height, bgColor)
                    }
                }
            } else {
                addTodaysBackground(this, resources, dayLabelHeight, bgColor)
            }
        }
    }
}

private fun addTodaysBackground(textView: TextView, res: Resources, dayLabelHeight: Int, primaryColor: Int) =
        textView.addResizedBackgroundDrawable(res, dayLabelHeight, primaryColor, com.simplemobiletools.studentcalendarpaid.R.drawable.ic_circle_filled)

fun Context.addDayEvents(day: DayMonthly, linearLayout: LinearLayout, res: Resources, dividerMargin: Int) {
    val eventLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    day.dayEvents.sortedWith(compareBy({ it.startTS }, { it.endTS }, { it.title })).forEach {
        val backgroundDrawable = res.getDrawable(com.simplemobiletools.studentcalendarpaid.R.drawable.day_monthly_event_background)
        backgroundDrawable.applyColorFilter(it.color)
        eventLayoutParams.setMargins(dividerMargin, 0, dividerMargin, dividerMargin)

        var textColor = it.color.getContrastColor()
        if (!day.isThisMonth) {
            backgroundDrawable.alpha = 64
            textColor = textColor.adjustAlpha(0.25f)
        }

        (View.inflate(applicationContext, com.simplemobiletools.studentcalendarpaid.R.layout.day_monthly_event_view, null) as TextView).apply {
            setTextColor(textColor)
            text = it.title.replace(" ", "\u00A0")  // allow word break by char
            background = backgroundDrawable
            layoutParams = eventLayoutParams
            linearLayout.addView(this)
        }
    }
}

fun Context.getEventListItems(events: List<Event>): ArrayList<ListItem> {
    val listItems = ArrayList<ListItem>(events.size)
    val replaceDescription = config.replaceDescription
    val sorted = events.sortedWith(compareBy({ it.startTS }, { it.endTS }, { it.title }, { if (replaceDescription) it.location else it.description }))
    var prevCode = ""
    val now = getNowSeconds()
    val today = Formatter.getDayTitle(this, Formatter.getDayCodeFromTS(now))

    sorted.forEach {
        val code = Formatter.getDayCodeFromTS(it.startTS)
        if (code != prevCode) {
            val day = Formatter.getDayTitle(this, code)
            val isToday = day == today
            val listSection = ListSection(day, code, isToday, !isToday && it.startTS < now)
            listItems.add(listSection)
            prevCode = code
        }
        val listEvent = ListEvent(it.id, it.startTS, it.endTS, it.title, it.description, it.getIsAllDay(), it.color, it.location, it.isPastEvent, it.repeatInterval > 0)
        listItems.add(listEvent)
    }
    return listItems
}

fun Context.handleEventDeleting(eventIds: List<Int>, timestamps: List<Int>, action: Int) {
    when (action) {
        DELETE_SELECTED_OCCURRENCE -> {
            eventIds.forEachIndexed { index, value ->
                dbHelper.addEventRepeatException(value, timestamps[index], true)
            }
        }
        DELETE_FUTURE_OCCURRENCES -> {
            eventIds.forEachIndexed { index, value ->
                dbHelper.addEventRepeatLimit(value, timestamps[index])
            }
        }
        DELETE_ALL_OCCURRENCES -> {
            val eventIDs = Array(eventIds.size) { i -> (eventIds[i].toString()) }
            dbHelper.deleteEvents(eventIDs, true)
        }
    }
}
