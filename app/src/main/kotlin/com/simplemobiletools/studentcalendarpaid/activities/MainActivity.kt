package com.simplemobiletools.studentcalendarpaid.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.simplemobiletools.studentcalendarpaid.adapters.EventListAdapter
import com.simplemobiletools.studentcalendarpaid.dialogs.ExportEventsDialog
import com.simplemobiletools.studentcalendarpaid.dialogs.FilterEventTypesDialog
import com.simplemobiletools.studentcalendarpaid.dialogs.ImportEventsDialog
import com.simplemobiletools.studentcalendarpaid.extensions.*
import com.simplemobiletools.studentcalendarpaid.fragments.*
import com.simplemobiletools.studentcalendarpaid.helpers.*
import com.simplemobiletools.studentcalendarpaid.helpers.Formatter
import com.simplemobiletools.studentcalendarpaid.models.Event
import com.simplemobiletools.studentcalendarpaid.models.EventType
import com.simplemobiletools.studentcalendarpaid.models.ListEvent
import com.simplemobiletools.commons.dialogs.FilePickerDialog
import com.simplemobiletools.commons.dialogs.RadioGroupDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.commons.interfaces.RefreshRecyclerViewListener
import com.simplemobiletools.commons.models.RadioItem
import com.simplemobiletools.commons.models.Release
import com.simplemobiletools.studentcalendarpaid.R
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : com.simplemobiletools.studentcalendarpaid.activities.SimpleActivity(), RefreshRecyclerViewListener , NavigationView.OnNavigationItemSelectedListener{
    var prefs: SharedPreferences? = null







    private val CALDAV_SYNC_DELAY = 1000L



    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private lateinit var userName: TextView
    private var userImage: ImageView? = null
    private var userEmail: TextView? = null

    private var bgColor = Color.parseColor("#311b92")


    private var showCalDAVRefreshToast = false
    private var mShouldFilterBeVisible = false
    private var mIsSearchOpen = false
    private var mLatestSearchQuery = ""
    private var mCalDAVSyncHandler = Handler()
    private var mSearchMenuItem: MenuItem? = null
    private var shouldGoToTodayBeVisible = false
    private var goToTodayButton: MenuItem? = null
    private var currentFragments = ArrayList<MyFragmentHolder>()

    private var mStoredTextColor = 0
    private var mStoredBackgroundColor = 0
    private var mStoredPrimaryColor = bgColor
    private var mStoredDayCode = ""
    private var mStoredIsSundayFirst = false
    private var mStoredUse24HourFormat = false
    private var mStoredDimPastEvents = true
    private var mAuth: FirebaseAuth? = null
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.simplemobiletools.studentcalendarpaid.R.layout.activity_main)
        appLaunched(com.simplemobiletools.studentcalendarpaid.BuildConfig.APPLICATION_ID)


        prefs = getSharedPreferences("com.simplemobiletools.studentcalendarpro", MODE_PRIVATE);
        prefs = getSharedPreferences("com.simplemobiletools.studentcalendarpro", MODE_PRIVATE);


/*
        prefs = getSharedPreferences("com.simplemobiletools.studentcalendarpro", MODE_PRIVATE)
        if (prefs!!.getBoolean("firstrun", true)) {
            val h = Intent(this@MainActivity, OnBoardingActivity::class.java)
            startActivity(h)
            prefs!!.edit().putBoolean("firstrun", false).apply();
        }
*/






        drawerLayout = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout)
        navigationView = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.nav_view)
        navigationView!!.bringToFront()


        navigationView!!.setNavigationItemSelectedListener(this)




/*
        checkWhatsNewDialog()
        calendar_fab.beVisible()
        */
        calendar_fab.setOnClickListener {
            launchNewEventIntent(currentFragments.last().getNewEventDayCode())
        }
       storeStateVariables()
        if (resources.getBoolean(com.simplemobiletools.studentcalendarpaid.R.bool.portrait_only)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        if (!hasPermission(PERMISSION_WRITE_CALENDAR) || !hasPermission(PERMISSION_READ_CALENDAR)) {
            config.caldavSync = false
        }

        if (config.caldavSync) {
            refreshCalDAVCalendars(false)
        }

        swipe_refresh_layout.setOnRefreshListener {
            refreshCalDAVCalendars(false)
        }

        if (!checkViewIntents()) {
            return
        }

        if (!checkOpenIntents()) {
            updateViewPager()
        }

        checkAppOnSDCard()




        mAuth = FirebaseAuth.getInstance()
        val user = mAuth!!.currentUser


        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
/*
                val personName = user!!.displayName
                val personPhotoUrl = Objects.requireNonNull<Uri>(user.photoUrl).toString()
                val email = user.email

                userEmail = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userEmail)
                userName = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userName)
                userImage = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userImage)




                userName.text = personName
                userEmail!!.text = email

                Glide.with(applicationContext).load(personPhotoUrl)
                        .thumbnail(0.3f)
                        .apply(RequestOptions.circleCropTransform())

                        .into(userImage!!)*/
            }

            if (firebaseAuth.currentUser == null) {

            }
        }

        updateViewPager()





    }




    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // here is the main place where we need to work on.
        val id = item.itemId
        when (id) {

            com.simplemobiletools.studentcalendarpaid.R.id.nav_calendar -> {
                val h = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(h)
            }
/*
            com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val l = Intent(this@MainActivity, AccountActivity::class.java)
                startActivity(l)

            }*/
            com.simplemobiletools.studentcalendarpaid.R.id.nav_todo -> {
                val m = Intent(this@MainActivity, TodoActivity::class.java)
                startActivity(m)

            }


                R.id.nav_grades -> {
                val i = Intent(this@MainActivity, AddGradeActivity::class.java)
                startActivity(i)
            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_info -> {

                val k = Intent(this@MainActivity, com.simplemobiletools.studentcalendarpaid.activities.InfoActivity::class.java)
                startActivity(k)
            }
            com.simplemobiletools.studentcalendarpaid.R.id.nav_overview -> {
                val j = Intent(this@MainActivity, ViewListContents::class.java)
                startActivity(j)

            }


        }// this is done, now let us go and intialise the home page.
        // after this lets start copying the above.
        // FOLLOW MEEEEE>>>
        val drawer = findViewById<View>(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)

        return true

        updateViewPager()
    }








    private fun startInfoActivity() {
        val intent = Intent(this, com.simplemobiletools.studentcalendarpaid.activities.InfoActivity::class.java)
        startActivity(intent)


    }


    override fun onResume() {
        super.onResume()

        if (prefs!!.getBoolean("firstrun", true)) {
            val h = Intent(this@MainActivity, OnBoardingActivity::class.java)
            startActivity(h)
            prefs!!.edit().putBoolean("firstrun", false).apply()
        }
        if (mStoredTextColor != config.textColor || mStoredBackgroundColor != config.backgroundColor ||
                 mStoredDayCode != Formatter.getTodayCode() || mStoredDimPastEvents != config.dimPastEvents) {
            updateViewPager()
        }

        dbHelper.getEventTypes {
            mShouldFilterBeVisible = it.size > 1 || config.displayEventTypes.isEmpty()
        }

        if (config.storedView == WEEKLY_VIEW) {
            if (mStoredIsSundayFirst != config.isSundayFirst || mStoredUse24HourFormat != config.use24HourFormat) {
                updateViewPager()
            }
        }

        storeStateVariables()
        updateWidgets()
        if (config.storedView != EVENTS_LIST_VIEW) {
            updateTextColors(calendar_coordinator)
        }
        search_placeholder.setTextColor(config.textColor)
        search_placeholder_2.setTextColor(config.textColor)
        calendar_fab.setColors(config.textColor,bgColor, bgColor)
        search_holder.background = ColorDrawable(bgColor)
        checkSwipeRefreshAvailability()
    }

    override fun onPause() {
        super.onPause()
        storeStateVariables()
    }

    override fun onStop() {
        super.onStop()
        mCalDAVSyncHandler.removeCallbacksAndMessages(null)
        contentResolver.unregisterContentObserver(calDAVSyncObserver)
        closeSearch()
    }


    override fun onStart() {
        super.onStart()

        mAuth!!.addAuthStateListener(mAuthListener)
        updateViewPager()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.simplemobiletools.studentcalendarpaid.R.menu.menu_main, menu)
        menu.apply {
            goToTodayButton = findItem(com.simplemobiletools.studentcalendarpaid.R.id.go_to_today)
            findItem(com.simplemobiletools.studentcalendarpaid.R.id.filter).isVisible = mShouldFilterBeVisible
            findItem(com.simplemobiletools.studentcalendarpaid.R.id.go_to_today).isVisible = shouldGoToTodayBeVisible && config.storedView != EVENTS_LIST_VIEW
        }

        setupSearch(menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu!!.apply {
            findItem(com.simplemobiletools.studentcalendarpaid.R.id.refresh_caldav_calendars).isVisible = config.caldavSync
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.simplemobiletools.studentcalendarpaid.R.id.change_view -> showViewDialog()
            com.simplemobiletools.studentcalendarpaid.R.id.go_to_today -> goToToday()
            com.simplemobiletools.studentcalendarpaid.R.id.filter -> showFilterDialog()
            com.simplemobiletools.studentcalendarpaid.R.id.refresh_caldav_calendars -> refreshCalDAVCalendars(true)
            com.simplemobiletools.studentcalendarpaid.R.id.settings -> launchSettings()
            com.simplemobiletools.studentcalendarpaid.R.id.nav_info -> startActivity(Intent(applicationContext, ActivityInfo::class.java))
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        swipe_refresh_layout.isRefreshing = false
        checkSwipeRefreshAvailability()
        if (currentFragments.size > 1) {
            removeTopFragment()
        } else {
            super.onBackPressed()
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkOpenIntents()
        checkViewIntents()
    }

    private fun storeStateVariables() {
        config.apply {
            mStoredIsSundayFirst = isSundayFirst
            mStoredTextColor = textColor
            mStoredPrimaryColor = bgColor
            mStoredBackgroundColor = backgroundColor
            mStoredUse24HourFormat = use24HourFormat
            mStoredDimPastEvents = dimPastEvents
        }
        mStoredDayCode = Formatter.getTodayCode()
    }

    private fun setupSearch(menu: Menu) {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        mSearchMenuItem = menu.findItem(com.simplemobiletools.studentcalendarpaid.R.id.search)
        (mSearchMenuItem!!.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isSubmitButtonEnabled = false
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String) = false

                override fun onQueryTextChange(newText: String): Boolean {
                    if (mIsSearchOpen) {
                        searchQueryChanged(newText)
                    }
                    return true
                }
            })
        }

        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                mIsSearchOpen = true
                search_holder.beVisible()
                calendar_fab.beVisible()
                searchQueryChanged("")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                mIsSearchOpen = false
                search_holder.beGone()
                calendar_fab.beVisible()
                return true
            }
        })
    }

    private fun closeSearch() {
        mSearchMenuItem?.collapseActionView()
    }

    private fun checkOpenIntents(): Boolean {
        val dayCodeToOpen = intent.getStringExtra(DAY_CODE) ?: ""
        val viewToOpen = intent.getIntExtra(VIEW_TO_OPEN, DAILY_VIEW)
        intent.removeExtra(VIEW_TO_OPEN)
        intent.removeExtra(DAY_CODE)
        if (dayCodeToOpen.isNotEmpty()) {
            calendar_fab.beVisible()
            if (viewToOpen != LAST_VIEW) {
                config.storedView = viewToOpen
            }
            updateViewPager(dayCodeToOpen)
            return true
        }

        val eventIdToOpen = intent.getIntExtra(EVENT_ID, 0)
        val eventOccurrenceToOpen = intent.getIntExtra(EVENT_OCCURRENCE_TS, 0)
        intent.removeExtra(EVENT_ID)
        intent.removeExtra(EVENT_OCCURRENCE_TS)
        if (eventIdToOpen != 0 && eventOccurrenceToOpen != 0) {
            Intent(this, com.simplemobiletools.studentcalendarpaid.activities.EventActivity::class.java).apply {
                putExtra(EVENT_ID, eventIdToOpen)
                putExtra(EVENT_OCCURRENCE_TS, eventOccurrenceToOpen)
                startActivity(this)
            }
        }

        return false
    }

    private fun checkViewIntents(): Boolean {
        if (intent?.action == Intent.ACTION_VIEW && intent.data != null) {
            val uri = intent.data
            if (uri.authority == "com.android.calendar") {
                if (uri.path.startsWith("/events")) {
                    // intents like content://com.android.calendar/events/1756
                    val eventId = uri.lastPathSegment
                    val id = dbHelper.getEventIdWithLastImportId(eventId)
                    if (id != 0) {
                        Intent(this, com.simplemobiletools.studentcalendarpaid.activities.EventActivity::class.java).apply {
                            putExtra(EVENT_ID, id)
                            startActivity(this)
                        }
                    } else {
                        toast(com.simplemobiletools.studentcalendarpaid.R.string.unknown_error_occurred)
                    }
                } else if (intent?.extras?.getBoolean("DETAIL_VIEW", false) == true) {
                    // clicking date on a third party widget: content://com.android.calendar/time/1507309245683
                    val timestamp = uri.pathSegments.last()
                    if (timestamp.areDigitsOnly()) {
                        openDayAt(timestamp.toLong())
                        return false
                    }
                }
            } else {
                tryImportEventsFromFile(uri)
            }
        }
        return true
    }

    private fun showViewDialog() {
        val items = arrayListOf(
                RadioItem(DAILY_VIEW, getString(com.simplemobiletools.studentcalendarpaid.R.string.daily_view)),
                RadioItem(WEEKLY_VIEW, getString(com.simplemobiletools.studentcalendarpaid.R.string.weekly_view)),
                RadioItem(MONTHLY_VIEW, getString(com.simplemobiletools.studentcalendarpaid.R.string.monthly_view)),
                RadioItem(YEARLY_VIEW, getString(com.simplemobiletools.studentcalendarpaid.R.string.yearly_view)),
                RadioItem(EVENTS_LIST_VIEW, getString(com.simplemobiletools.studentcalendarpaid.R.string.simple_event_list)))

        RadioGroupDialog(this, items, config.storedView) {
            calendar_fab.beVisibleIf(it as Int != YEARLY_VIEW)
            resetActionBarTitle()
            closeSearch()
            updateView(it)
            shouldGoToTodayBeVisible = false
            invalidateOptionsMenu()
        }
    }

    private fun goToToday() {
        currentFragments.last().goToToday()
    }

    private fun resetActionBarTitle() {
        updateActionBarTitle(getString(com.simplemobiletools.studentcalendarpaid.R.string.app_launcher_name))
        updateActionBarSubtitle("")
    }

    private fun showFilterDialog() {
        FilterEventTypesDialog(this) {
            refreshViewPager()
            updateWidgets()
        }
    }

    fun toggleGoToTodayVisibility(beVisible: Boolean) {
        shouldGoToTodayBeVisible = beVisible
        if (goToTodayButton?.isVisible != beVisible) {
            invalidateOptionsMenu()
        }
    }

    private fun refreshCalDAVCalendars(showRefreshToast: Boolean) {
        showCalDAVRefreshToast = showRefreshToast
        if (showRefreshToast) {
            toast(com.simplemobiletools.studentcalendarpaid.R.string.refreshing)
        }

        syncCalDAVCalendars(this, calDAVSyncObserver)
        scheduleCalDAVSync(true)
    }

    private val calDAVSyncObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            if (!selfChange) {
                mCalDAVSyncHandler.removeCallbacksAndMessages(null)
                mCalDAVSyncHandler.postDelayed({
                    recheckCalDAVCalendars {
                        refreshViewPager()
                        if (showCalDAVRefreshToast) {
                            toast(com.simplemobiletools.studentcalendarpaid.R.string.refreshing_complete)
                        }
                        runOnUiThread {
                            swipe_refresh_layout.isRefreshing = false
                        }
                    }
                }, CALDAV_SYNC_DELAY)
            }
        }
    }

    private fun addHolidays() {
        val items = getHolidayRadioItems()
        RadioGroupDialog(this, items) {
            toast(com.simplemobiletools.studentcalendarpaid.R.string.importing)
            Thread {
                val holidays = getString(com.simplemobiletools.studentcalendarpaid.R.string.holidays)
                var eventTypeId = dbHelper.getEventTypeIdWithTitle(holidays)
                if (eventTypeId == -1) {
                    val eventType = EventType(0, holidays, resources.getColor(com.simplemobiletools.studentcalendarpaid.R.color.default_holidays_color))
                    eventTypeId = dbHelper.insertEventType(eventType)
                }

                val result = IcsImporter(this).importEvents(it as String, eventTypeId, 0, false)
                handleParseResult(result)
                if (result != IcsImporter.ImportResult.IMPORT_FAIL) {
                    runOnUiThread {
                        updateViewPager()
                    }
                }
            }.start()
        }
    }

    private fun tryAddBirthdays() {
        handlePermission(PERMISSION_READ_CONTACTS) {
            if (it) {
                Thread {
                    addContactEvents(true) {
                        if (it > 0) {
                            toast(com.simplemobiletools.studentcalendarpaid.R.string.birthdays_added)
                            updateViewPager()
                        } else {
                            toast(com.simplemobiletools.studentcalendarpaid.R.string.no_birthdays)
                        }
                    }
                }.start()
            } else {
                toast(com.simplemobiletools.studentcalendarpaid.R.string.no_contacts_permission)
            }
        }
    }

    private fun tryAddAnniversaries() {
        handlePermission(PERMISSION_READ_CONTACTS) {
            if (it) {
                Thread {
                    addContactEvents(false) {
                        if (it > 0) {
                            toast(com.simplemobiletools.studentcalendarpaid.R.string.anniversaries_added)
                            updateViewPager()
                        } else {
                            toast(com.simplemobiletools.studentcalendarpaid.R.string.no_anniversaries)
                        }
                    }
                }.start()
            } else {
                toast(com.simplemobiletools.studentcalendarpaid.R.string.no_contacts_permission)
            }
        }
    }

    private fun handleParseResult(result: IcsImporter.ImportResult) {
        toast(when (result) {
            IcsImporter.ImportResult.IMPORT_OK -> com.simplemobiletools.studentcalendarpaid.R.string.holidays_imported_successfully
            IcsImporter.ImportResult.IMPORT_PARTIAL -> com.simplemobiletools.studentcalendarpaid.R.string.importing_some_holidays_failed
            else -> com.simplemobiletools.studentcalendarpaid.R.string.importing_holidays_failed
        }, Toast.LENGTH_LONG)
    }

    private fun addContactEvents(birthdays: Boolean, callback: (Int) -> Unit) {
        var eventsAdded = 0
        val uri = ContactsContract.Data.CONTENT_URI
        val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.CONTACT_LAST_UPDATED_TIMESTAMP,
                ContactsContract.CommonDataKinds.Event.START_DATE)

        val selection = "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Event.TYPE} = ?"
        val type = if (birthdays) ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY else ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY
        val selectionArgs = arrayOf(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE, type.toString())
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor?.moveToFirst() == true) {
                val dateFormats = getDateFormats()
                val existingEvents = if (birthdays) dbHelper.getBirthdays() else dbHelper.getAnniversaries()
                val importIDs = existingEvents.map { it.importId }
                val eventTypeId = if (birthdays) getBirthdaysEventTypeId() else getAnniversariesEventTypeId()

                do {
                    val contactId = cursor.getIntValue(ContactsContract.CommonDataKinds.Event.CONTACT_ID).toString()
                    val name = cursor.getStringValue(ContactsContract.Contacts.DISPLAY_NAME)
                    val startDate = cursor.getStringValue(ContactsContract.CommonDataKinds.Event.START_DATE)

                    for (format in dateFormats) {
                        try {
                            val formatter = SimpleDateFormat(format, Locale.getDefault())
                            val date = formatter.parse(startDate)
                            if (date.year < 70) {
                                date.year = 70
                            }

                            val timestamp = (date.time / 1000).toInt()
                            val source = if (birthdays) SOURCE_CONTACT_BIRTHDAY else SOURCE_CONTACT_ANNIVERSARY
                            val lastUpdated = cursor.getLongValue(ContactsContract.CommonDataKinds.Event.CONTACT_LAST_UPDATED_TIMESTAMP)
                            val event = Event(0, timestamp, timestamp, name, importId = contactId, flags = FLAG_ALL_DAY, repeatInterval = YEAR,
                                    eventType = eventTypeId, source = source, lastUpdated = lastUpdated)

                            if (!importIDs.contains(contactId)) {
                                dbHelper.insert(event, false) {
                                    eventsAdded++
                                }
                            }
                            break
                        } catch (e: Exception) {
                        }
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            cursor?.close()
        }

        runOnUiThread {
            callback(eventsAdded)
        }
    }

    private fun getBirthdaysEventTypeId(): Int {
        val birthdays = getString(com.simplemobiletools.studentcalendarpaid.R.string.birthdays)
        var eventTypeId = dbHelper.getEventTypeIdWithTitle(birthdays)
        if (eventTypeId == -1) {
            val eventType = EventType(0, birthdays, resources.getColor(com.simplemobiletools.studentcalendarpaid.R.color.default_birthdays_color))
            eventTypeId = dbHelper.insertEventType(eventType)
        }
        return eventTypeId
    }

    private fun getAnniversariesEventTypeId(): Int {
        val anniversaries = getString(com.simplemobiletools.studentcalendarpaid.R.string.anniversaries)
        var eventTypeId = dbHelper.getEventTypeIdWithTitle(anniversaries)
        if (eventTypeId == -1) {
            val eventType = EventType(0, anniversaries, resources.getColor(com.simplemobiletools.studentcalendarpaid.R.color.default_anniversaries_color))
            eventTypeId = dbHelper.insertEventType(eventType)
        }
        return eventTypeId
    }

    private fun updateView(view: Int) {
        calendar_fab.beVisibleIf(view != YEARLY_VIEW)
        config.storedView = view
        checkSwipeRefreshAvailability()
        updateViewPager()
        if (goToTodayButton?.isVisible == true) {
            shouldGoToTodayBeVisible = false
            invalidateOptionsMenu()
        }
    }

    private fun updateViewPager(dayCode: String? = Formatter.getTodayCode()) {
        val fragment = getFragmentsHolder()
        currentFragments.forEach {
            supportFragmentManager.beginTransaction().remove(it).commitNow()
        }
        currentFragments.clear()
        currentFragments.add(fragment)
        val bundle = Bundle()

        when (config.storedView) {
            DAILY_VIEW, MONTHLY_VIEW -> bundle.putString(DAY_CODE, dayCode)
            WEEKLY_VIEW -> bundle.putString(WEEK_START_DATE_TIME, getThisWeekDateTime())
        }

        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().add(com.simplemobiletools.studentcalendarpaid.R.id.fragments_holder, fragment).commitNow()
    }

    fun openMonthFromYearly(dateTime: DateTime) {
        if (currentFragments.last() is MonthFragmentsHolder) {
            return
        }

        val fragment = MonthFragmentsHolder()
        currentFragments.add(fragment)
        val bundle = Bundle()
        bundle.putString(DAY_CODE, Formatter.getDayCodeFromDateTime(dateTime))
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().add(com.simplemobiletools.studentcalendarpaid.R.id.fragments_holder, fragment).commitNow()
        resetActionBarTitle()
        calendar_fab.beVisible()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun openDayFromMonthly(dateTime: DateTime) {
        if (currentFragments.last() is DayFragmentsHolder) {
            return
        }

        val fragment = DayFragmentsHolder()
        currentFragments.add(fragment)
        val bundle = Bundle()
        bundle.putString(DAY_CODE, Formatter.getDayCodeFromDateTime(dateTime))
        fragment.arguments = bundle
        try {
            supportFragmentManager.beginTransaction().add(com.simplemobiletools.studentcalendarpaid.R.id.fragments_holder, fragment).commitNow()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } catch (e: Exception) {
        }
    }

    private fun getThisWeekDateTime(): String {
        var thisweek = DateTime().withDayOfWeek(1).withTimeAtStartOfDay().minusDays(if (config.isSundayFirst) 1 else 0)
        if (DateTime().minusDays(7).seconds() > thisweek.seconds()) {
            thisweek = thisweek.plusDays(7)
        }
        return thisweek.toString()
    }

    private fun getFragmentsHolder() = when (config.storedView) {
        DAILY_VIEW -> DayFragmentsHolder()
        MONTHLY_VIEW -> MonthFragmentsHolder()
        YEARLY_VIEW -> YearFragmentsHolder()
        EVENTS_LIST_VIEW -> EventListFragment()
        else -> WeekFragmentsHolder()
    }

    private fun removeTopFragment() {
        supportFragmentManager.beginTransaction().remove(currentFragments.last()).commit()
        currentFragments.removeAt(currentFragments.size - 1)
        toggleGoToTodayVisibility(currentFragments.last().shouldGoToTodayBeVisible())
        currentFragments.last().apply {
            refreshEvents()
            updateActionBarTitle()
        }
        calendar_fab.beGoneIf(currentFragments.size == 1 && config.storedView == YEARLY_VIEW)
        supportActionBar?.setDisplayHomeAsUpEnabled(currentFragments.size > 1)
    }

    private fun refreshViewPager() {
        runOnUiThread {
            if (!isDestroyed) {
                currentFragments.last().refreshEvents()
            }
        }
    }

    private fun tryImportEvents() {
        handlePermission(PERMISSION_READ_STORAGE) {
            if (it) {
                importEvents()
            }
        }
    }

    private fun importEvents() {
        FilePickerDialog(this) {
            showImportEventsDialog(it)
        }
    }

    private fun tryImportEventsFromFile(uri: Uri) {
        when {
            uri.scheme == "file" -> showImportEventsDialog(uri.path)
            uri.scheme == "content" -> {
                val tempFile = getTempFile()
                if (tempFile == null) {
                    toast(com.simplemobiletools.studentcalendarpaid.R.string.unknown_error_occurred)
                    return
                }

                val inputStream = contentResolver.openInputStream(uri)
                val out = FileOutputStream(tempFile)
                inputStream.copyTo(out)
                showImportEventsDialog(tempFile.absolutePath)
            }
            else -> toast(com.simplemobiletools.studentcalendarpaid.R.string.invalid_file_format)
        }
    }

    private fun showImportEventsDialog(path: String) {
        ImportEventsDialog(this, path) {
            if (it) {
                runOnUiThread {
                    updateViewPager()
                }
            }
        }
    }

    private fun tryExportEvents() {
        handlePermission(PERMISSION_WRITE_STORAGE) {
            if (it) {
                exportEvents()
            }
        }
    }

    private fun exportEvents() {
        FilePickerDialog(this, pickFile = false, showFAB = true) {
            ExportEventsDialog(this, it) { exportPastEvents, file, eventTypes ->
                Thread {
                    val events = dbHelper.getEventsToExport(exportPastEvents).filter { eventTypes.contains(it.eventType.toString()) }
                    if (events.isEmpty()) {
                        toast(com.simplemobiletools.studentcalendarpaid.R.string.no_entries_for_exporting)
                    } else {
                        IcsExporter().exportEvents(this, file, events as ArrayList<Event>, true) {
                            toast(when (it) {
                                IcsExporter.ExportResult.EXPORT_OK -> com.simplemobiletools.studentcalendarpaid.R.string.exporting_successful
                                IcsExporter.ExportResult.EXPORT_PARTIAL -> com.simplemobiletools.studentcalendarpaid.R.string.exporting_some_entries_failed
                                else -> com.simplemobiletools.studentcalendarpaid.R.string.exporting_failed
                            })
                        }
                    }
                }.start()
            }
        }
    }

    private fun launchSettings() {
        startActivity(Intent(applicationContext, com.simplemobiletools.studentcalendarpaid.activities.SettingsActivity::class.java))
    }



    private fun searchQueryChanged(text: String) {
        mLatestSearchQuery = text
        search_placeholder_2.beGoneIf(text.length >= 2)
        if (text.length >= 2) {
            dbHelper.getEventsWithSearchQuery(text) { searchedText, events ->
                if (searchedText == mLatestSearchQuery) {
                    runOnUiThread {
                        search_results_list.beVisibleIf(events.isNotEmpty())
                        search_placeholder.beVisibleIf(events.isEmpty())
                        val listItems = getEventListItems(events)
                        val eventsAdapter = EventListAdapter(this, listItems, true, this, search_results_list) {
                            if (it is ListEvent) {
                                Intent(applicationContext, com.simplemobiletools.studentcalendarpaid.activities.EventActivity::class.java).apply {
                                    putExtra(EVENT_ID, it.id)
                                    startActivity(this)
                                }
                            }
                        }

                        search_results_list.adapter = eventsAdapter
                    }
                }
            }
        } else {
            search_placeholder.beVisible()
            search_results_list.beGone()
        }
    }

    private fun checkSwipeRefreshAvailability() {
        swipe_refresh_layout.isEnabled = config.caldavSync && config.pullToRefresh && config.storedView != WEEKLY_VIEW
        if (!swipe_refresh_layout.isEnabled) {
            swipe_refresh_layout.isRefreshing = false
        }
    }

    // only used at active search
    override fun refreshItems() {
        searchQueryChanged(mLatestSearchQuery)
        refreshViewPager()
    }

    private fun openDayAt(timestamp: Long) {
        val dayCode = Formatter.getDayCodeFromTS((timestamp / 1000).toInt())
        calendar_fab.beVisible()
        config.storedView = DAILY_VIEW
        updateViewPager(dayCode)
    }

    private fun getHolidayRadioItems(): ArrayList<RadioItem> {
        val items = ArrayList<RadioItem>()

        LinkedHashMap<String, String>().apply {
            put("Algeria", "algeria.ics")
            put("Argentina", "argentina.ics")
            put("Australia", "australia.ics")
            put("België", "belgium.ics")
            put("Bolivia", "bolivia.ics")
            put("Brasil", "brazil.ics")
            put("Canada", "canada.ics")
            put("China", "china.ics")
            put("Colombia", "colombia.ics")
            put("Česká republika", "czech.ics")
            put("Danmark", "denmark.ics")
            put("Deutschland", "germany.ics")
            put("Eesti", "estonia.ics")
            put("España", "spain.ics")
            put("Éire", "ireland.ics")
            put("France", "france.ics")
            put("Hanguk", "southkorea.ics")
            put("Hellas", "greece.ics")
            put("Hrvatska", "croatia.ics")
            put("India", "india.ics")
            put("Indonesia", "indonesia.ics")
            put("Ísland", "iceland.ics")
            put("Italia", "italy.ics")
            put("Latvija", "latvia.ics")
            put("Lietuva", "lithuania.ics")
            put("Luxemburg", "luxembourg.ics")
            put("Makedonija", "macedonia.ics")
            put("Malaysia", "malaysia.ics")
            put("Magyarország", "hungary.ics")
            put("México", "mexico.ics")
            put("Nederland", "netherlands.ics")
            put("日本", "japan.ics")
            put("Norge", "norway.ics")
            put("Österreich", "austria.ics")
            put("Pākistān", "pakistan.ics")
            put("Polska", "poland.ics")
            put("Portugal", "portugal.ics")
            put("Россия", "russia.ics")
            put("România", "romania.ics")
            put("Schweiz", "switzerland.ics")
            put("Singapore", "singapore.ics")
            put("Srbija", "serbia.ics")
            put("Slovenija", "slovenia.ics")
            put("Slovensko", "slovakia.ics")
            put("South Africa", "southafrica.ics")
            put("Suomi", "finland.ics")
            put("Sverige", "sweden.ics")
            put("Ukraine", "ukraine.ics")
            put("United Kingdom", "unitedkingdom.ics")
            put("United States", "unitedstates.ics")

            var i = 0
            for ((country, file) in this) {
                items.add(RadioItem(i++, country, file))
            }
        }

        return items
    }

    private fun checkWhatsNewDialog() {
        arrayListOf<Release>().apply {
            add(Release(39, com.simplemobiletools.studentcalendarpaid.R.string.release_39))
            add(Release(40, com.simplemobiletools.studentcalendarpaid.R.string.release_40))
            add(Release(42, com.simplemobiletools.studentcalendarpaid.R.string.release_42))
            add(Release(44, com.simplemobiletools.studentcalendarpaid.R.string.release_44))
            add(Release(46, com.simplemobiletools.studentcalendarpaid.R.string.release_46))
            add(Release(48, com.simplemobiletools.studentcalendarpaid.R.string.release_48))
            add(Release(49, com.simplemobiletools.studentcalendarpaid.R.string.release_49))
            add(Release(51, com.simplemobiletools.studentcalendarpaid.R.string.release_51))
            add(Release(52, com.simplemobiletools.studentcalendarpaid.R.string.release_52))
            add(Release(54, com.simplemobiletools.studentcalendarpaid.R.string.release_54))
            add(Release(57, com.simplemobiletools.studentcalendarpaid.R.string.release_57))
            add(Release(59, com.simplemobiletools.studentcalendarpaid.R.string.release_59))
            add(Release(60, com.simplemobiletools.studentcalendarpaid.R.string.release_60))
            add(Release(62, com.simplemobiletools.studentcalendarpaid.R.string.release_62))
            add(Release(67, com.simplemobiletools.studentcalendarpaid.R.string.release_67))
            add(Release(69, com.simplemobiletools.studentcalendarpaid.R.string.release_69))
            add(Release(71, com.simplemobiletools.studentcalendarpaid.R.string.release_71))
            add(Release(73, com.simplemobiletools.studentcalendarpaid.R.string.release_73))
            add(Release(76, com.simplemobiletools.studentcalendarpaid.R.string.release_76))
            add(Release(77, com.simplemobiletools.studentcalendarpaid.R.string.release_77))
            add(Release(80, com.simplemobiletools.studentcalendarpaid.R.string.release_80))
            add(Release(84, com.simplemobiletools.studentcalendarpaid.R.string.release_84))
            add(Release(86, com.simplemobiletools.studentcalendarpaid.R.string.release_86))
            add(Release(88, com.simplemobiletools.studentcalendarpaid.R.string.release_88))
            add(Release(98, com.simplemobiletools.studentcalendarpaid.R.string.release_98))
            add(Release(117, com.simplemobiletools.studentcalendarpaid.R.string.release_117))
            add(Release(119, com.simplemobiletools.studentcalendarpaid.R.string.release_119))
            add(Release(129, com.simplemobiletools.studentcalendarpaid.R.string.release_129))
            checkWhatsNew(this, com.simplemobiletools.studentcalendarpaid.BuildConfig.VERSION_CODE)
        }
    }



}


