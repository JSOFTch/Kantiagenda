package com.simplemobiletools.studentcalendarpaid.activities

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import com.simplemobiletools.studentcalendarpaid.adapters.EventListAdapter
import com.simplemobiletools.studentcalendarpaid.extensions.config
import com.simplemobiletools.studentcalendarpaid.extensions.seconds
import com.simplemobiletools.studentcalendarpaid.helpers.Formatter
import com.simplemobiletools.studentcalendarpaid.helpers.MyWidgetListProvider
import com.simplemobiletools.studentcalendarpaid.models.ListEvent
import com.simplemobiletools.studentcalendarpaid.models.ListItem
import com.simplemobiletools.studentcalendarpaid.models.ListSection
import com.simplemobiletools.commons.dialogs.ColorPickerDialog
import com.simplemobiletools.commons.extensions.adjustAlpha
import com.simplemobiletools.commons.extensions.getAdjustedPrimaryColor
import com.simplemobiletools.commons.extensions.setFillWithStroke
import com.simplemobiletools.commons.helpers.IS_CUSTOMIZING_COLORS
import kotlinx.android.synthetic.main.widget_config_list.*
import org.joda.time.DateTime
import java.util.*

class WidgetListConfigureActivity : com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity() {
    private var mBgAlpha = 0f
    private var bgColor = Color.parseColor("#311b92")

    private var mWidgetId = 0
    private var mBgColorWithoutTransparency = 0
    private var mBgColor = 0
    private var mTextColorWithoutTransparency = 0
    private var mTextColor = 0

    private var mEventsAdapter: EventListAdapter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        useDynamicTheme = false
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        setContentView(com.simplemobiletools.studentcalendarpaid.R.layout.widget_config_list)
        initVariables()

        val isCustomizingColors = intent.extras?.getBoolean(IS_CUSTOMIZING_COLORS) ?: false
        mWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (mWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID && !isCustomizingColors) {
            finish()
        }

        mEventsAdapter = EventListAdapter(this, getListItems(), false, null, config_events_list) {}
        mEventsAdapter!!.updateTextColor(mTextColor)
        config_events_list.adapter = mEventsAdapter

        config_save.setOnClickListener { saveConfig() }
        config_bg_color.setOnClickListener { pickBackgroundColor() }
        config_text_color.setOnClickListener { pickTextColor() }

        val primaryColor = getAdjustedPrimaryColor()
        config_bg_seekbar.setColors(mTextColor, primaryColor, primaryColor)
    }

    override fun onResume() {
        super.onResume()
        window.decorView.setBackgroundColor(0)
    }

    private fun initVariables() {
        mTextColorWithoutTransparency = config.widgetTextColor
        updateColors()

        mBgColor = config.widgetBgColor
        if (mBgColor == 1) {
            mBgColor = Color.BLACK
            mBgAlpha = .2f
        } else {
            mBgAlpha = Color.alpha(mBgColor) / 255.toFloat()
        }

        mBgColorWithoutTransparency = Color.rgb(Color.red(mBgColor), Color.green(mBgColor), Color.blue(mBgColor))
        config_bg_seekbar.setOnSeekBarChangeListener(bgSeekbarChangeListener)
        config_bg_seekbar.progress = (mBgAlpha * 100).toInt()
        updateBgColor()
    }

    private fun saveConfig() {
        storeWidgetColors()
        requestWidgetUpdate()

        Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    private fun storeWidgetColors() {
        config.apply {
            widgetBgColor = mBgColor
            widgetTextColor = mTextColorWithoutTransparency
        }
    }

    private fun pickBackgroundColor() {
        ColorPickerDialog(this, mBgColorWithoutTransparency) { wasPositivePressed, color ->
            if (wasPositivePressed) {
                mBgColorWithoutTransparency = color
                updateBgColor()
            }
        }
    }

    private fun pickTextColor() {
        ColorPickerDialog(this, mTextColor) { wasPositivePressed, color ->
            if (wasPositivePressed) {
                mTextColorWithoutTransparency = color
                updateColors()
            }
        }
    }

    private fun requestWidgetUpdate() {
        Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, MyWidgetListProvider::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(mWidgetId))
            sendBroadcast(this)
        }
    }

    private fun updateColors() {
        mTextColor = mTextColorWithoutTransparency
        mEventsAdapter?.updateTextColor(mTextColor)
        config_text_color.setFillWithStroke(mTextColor, Color.BLACK)
        config_save.setTextColor(mTextColor)
    }

    private fun updateBgColor() {
        mBgColor = mBgColorWithoutTransparency.adjustAlpha(mBgAlpha)
        config_events_list.setBackgroundColor(mBgColor)
        config_bg_color.setFillWithStroke(mBgColor, Color.BLACK)
        config_save.setBackgroundColor(mBgColor)
    }

    private fun getListItems(): ArrayList<ListItem> {
        val listItems = ArrayList<ListItem>(10)
        var dateTime = DateTime.now().withTime(0, 0, 0, 0).plusDays(1)
        var code = Formatter.getDayCodeFromTS(dateTime.seconds())
        var day = Formatter.getDayTitle(this, code)
        listItems.add(ListSection(day, code, false, false))

        var time = dateTime.withHourOfDay(7)
        listItems.add(ListEvent(1, time.seconds(), time.plusMinutes(30).seconds(), getString(com.simplemobiletools.studentcalendarpaid.R.string.sample_title_1), getString(com.simplemobiletools.studentcalendarpaid.R.string.sample_description_1), false, getAdjustedPrimaryColor()))
        time = dateTime.withHourOfDay(8)
        listItems.add(ListEvent(2, time.seconds(), time.plusHours(1).seconds(), getString(com.simplemobiletools.studentcalendarpaid.R.string.sample_title_2), getString(com.simplemobiletools.studentcalendarpaid.R.string.sample_description_2), false, getAdjustedPrimaryColor()))

        dateTime = dateTime.plusDays(1)
        code = Formatter.getDayCodeFromTS(dateTime.seconds())
        day = Formatter.getDayTitle(this, code)
        listItems.add(ListSection(day, code, false, false))

        time = dateTime.withHourOfDay(8)
        listItems.add(ListEvent(3, time.seconds(), time.plusHours(1).seconds(), getString(com.simplemobiletools.studentcalendarpaid.R.string.sample_title_3), "", false, getAdjustedPrimaryColor()))
        time = dateTime.withHourOfDay(13)
        listItems.add(ListEvent(4, time.seconds(), time.plusHours(1).seconds(), getString(com.simplemobiletools.studentcalendarpaid.R.string.sample_title_4), getString(com.simplemobiletools.studentcalendarpaid.R.string.sample_description_4), false, getAdjustedPrimaryColor()))
        time = dateTime.withHourOfDay(18)
        listItems.add(ListEvent(5, time.seconds(), time.plusMinutes(10).seconds(), getString(com.simplemobiletools.studentcalendarpaid.R.string.sample_title_5), "", false, getAdjustedPrimaryColor()))

        return listItems
    }

    private val bgSeekbarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            mBgAlpha = progress.toFloat() / 100.toFloat()
            updateBgColor()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {

        }
    }
}
