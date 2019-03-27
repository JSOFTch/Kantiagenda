package com.simplemobiletools.studentcalendarpaid.helpers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.simplemobiletools.studentcalendarpaid.extensions.config
import com.simplemobiletools.studentcalendarpaid.extensions.launchNewEventIntent
import com.simplemobiletools.studentcalendarpaid.services.WidgetService
import com.simplemobiletools.commons.extensions.*
import org.joda.time.DateTime

class MyWidgetListProvider : AppWidgetProvider() {
    private val NEW_EVENT = "new_event"
    private val LAUNCH_CAL = "launch_cal"

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        performUpdate(context)
    }

    private fun performUpdate(context: Context) {
        val fontSize = context.config.getFontSize()
        val textColor = context.config.widgetTextColor

        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.getAppWidgetIds(getComponentName(context)).forEach {
            val views = RemoteViews(context.packageName, com.simplemobiletools.studentcalendarpaid.R.layout.widget_event_list).apply {
                setBackgroundColor(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list_holder, context.config.widgetBgColor)
                setTextColor(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list_empty, textColor)
                setTextSize(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list_empty, fontSize)

                setTextColor(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list_today, textColor)
                setTextSize(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list_today, fontSize)
            }

            val todayText = Formatter.getLongestDate(getNowSeconds())
            views.setText(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list_today, todayText)

            views.setImageViewBitmap(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_new_event, context.resources.getColoredBitmap(com.simplemobiletools.studentcalendarpaid.R.drawable.ic_plus, textColor))
            setupIntent(context, views, NEW_EVENT, com.simplemobiletools.studentcalendarpaid.R.id.widget_event_new_event)
            setupIntent(context, views, LAUNCH_CAL, com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list_today)

            Intent(context, WidgetService::class.java).apply {
                data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
                views.setRemoteAdapter(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list, this)
            }

            val startActivityIntent = context.getLaunchIntent() ?: Intent(context, com.simplemobiletools.studentcalendarpaid.activities.SplashActivity::class.java)
            val startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list, startActivityPendingIntent)
            views.setEmptyView(com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list, com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list_empty)

            appWidgetManager.updateAppWidget(it, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(it, com.simplemobiletools.studentcalendarpaid.R.id.widget_event_list)
        }
    }

    private fun getComponentName(context: Context) = ComponentName(context, MyWidgetListProvider::class.java)

    private fun setupIntent(context: Context, views: RemoteViews, action: String, id: Int) {
        Intent(context, MyWidgetListProvider::class.java).apply {
            this.action = action
            val pendingIntent = PendingIntent.getBroadcast(context, 0, this, 0)
            views.setOnClickPendingIntent(id, pendingIntent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NEW_EVENT -> context.launchNewEventIntent()
            LAUNCH_CAL -> launchCalenderInDefaultView(context)
            else -> super.onReceive(context, intent)
        }
    }

    private fun launchCalenderInDefaultView(context: Context) {
        (context.getLaunchIntent() ?: Intent(context, com.simplemobiletools.studentcalendarpaid.activities.SplashActivity::class.java)).apply {
            putExtra(DAY_CODE, Formatter.getDayCodeFromDateTime(DateTime()))
            putExtra(VIEW_TO_OPEN, context.config.listWidgetViewToOpen)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }
}
