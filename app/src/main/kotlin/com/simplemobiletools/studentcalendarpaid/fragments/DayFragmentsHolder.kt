package com.simplemobiletools.studentcalendarpaid.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.simplemobiletools.studentcalendarpaid.adapters.MyDayPagerAdapter
import com.simplemobiletools.studentcalendarpaid.extensions.config
import com.simplemobiletools.studentcalendarpaid.helpers.DAY_CODE
import com.simplemobiletools.studentcalendarpaid.helpers.Formatter
import com.simplemobiletools.studentcalendarpaid.interfaces.NavigationListener
import com.simplemobiletools.commons.extensions.updateActionBarTitle
import com.simplemobiletools.commons.views.MyViewPager
import kotlinx.android.synthetic.main.fragment_days_holder.view.*
import org.joda.time.DateTime
import java.util.*

class DayFragmentsHolder : MyFragmentHolder(), NavigationListener {
    private val PREFILLED_DAYS = 251

    private var viewPager: MyViewPager? = null
    private var defaultDailyPage = 0
    private var todayDayCode = ""
    private var currentDayCode = ""
    private var isGoToTodayVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDayCode = arguments?.getString(DAY_CODE) ?: ""
        todayDayCode = Formatter.getTodayCode()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.simplemobiletools.studentcalendarpaid.R.layout.fragment_days_holder, container, false)
        view.background = ColorDrawable(context!!.config.backgroundColor)
        viewPager = view.fragment_days_viewpager
        viewPager!!.id = (System.currentTimeMillis() % 100000).toInt()
        setupFragment()
        return view
    }

    private fun setupFragment() {
        val codes = getDays(currentDayCode)
        val dailyAdapter = MyDayPagerAdapter(activity!!.supportFragmentManager, codes, this)
        defaultDailyPage = codes.size / 2


        viewPager!!.apply {
            adapter = dailyAdapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    currentDayCode = codes[position]
                    val shouldGoToTodayBeVisible = shouldGoToTodayBeVisible()
                    if (isGoToTodayVisible != shouldGoToTodayBeVisible) {
                        (activity as? com.simplemobiletools.studentcalendarpaid.activities.MainActivity)?.toggleGoToTodayVisibility(shouldGoToTodayBeVisible)
                        isGoToTodayVisible = shouldGoToTodayBeVisible
                    }
                }
            })
            currentItem = defaultDailyPage
        }
        updateActionBarTitle()
    }

    private fun getDays(code: String): List<String> {
        val days = ArrayList<String>(PREFILLED_DAYS)
        val today = Formatter.getDateTimeFromCode(code)
        for (i in -PREFILLED_DAYS / 2..PREFILLED_DAYS / 2) {
            days.add(Formatter.getDayCodeFromDateTime(today.plusDays(i)))
        }
        return days
    }

    override fun goLeft() {
        viewPager!!.currentItem = viewPager!!.currentItem - 1
    }

    override fun goRight() {
        viewPager!!.currentItem = viewPager!!.currentItem + 1
    }

    override fun goToDateTime(dateTime: DateTime) {
        currentDayCode = Formatter.getDayCodeFromDateTime(dateTime)
        setupFragment()
    }

    override fun goToToday() {
        currentDayCode = todayDayCode
        setupFragment()
    }

    override fun refreshEvents() {
        (viewPager?.adapter as? MyDayPagerAdapter)?.updateCalendars(viewPager?.currentItem ?: 0)
    }

    override fun shouldGoToTodayBeVisible() = currentDayCode != todayDayCode

    override fun updateActionBarTitle() {
        (activity as com.simplemobiletools.studentcalendarpaid.activities.MainActivity).updateActionBarTitle(getString(com.simplemobiletools.studentcalendarpaid.R.string.app_launcher_name))
    }

    override fun getNewEventDayCode() = currentDayCode
}
