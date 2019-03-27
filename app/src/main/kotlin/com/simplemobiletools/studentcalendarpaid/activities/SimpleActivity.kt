package com.simplemobiletools.studentcalendarpaid.activities

import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.simplemobiletools.studentcalendarpaid.fragments.EventListFragment
import com.simplemobiletools.commons.activities.BaseSimpleActivity

open class SimpleActivity : BaseSimpleActivity() {
    override fun getAppIconIDs() = arrayListOf(
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_red,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_pink,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_purple,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_deep_purple,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_indigo,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_blue,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_light_blue,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_cyan,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_teal,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_green,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_light_green,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_lime,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_yellow,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_amber,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_deep_orange,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_brown,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_blue_grey,
            com.simplemobiletools.studentcalendarpaid.R.mipmap.ic_launcher_grey_black
    )

    override fun getAppLauncherName() = getString(com.simplemobiletools.studentcalendarpaid.R.string.app_launcher_name)
    open fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Handle navigation view item clicks here.
        // here is the main place where we need to work on.
        val id = item.itemId
        when (id) {

            com.simplemobiletools.studentcalendarpaid.R.id.nav_calendar -> {
                val h = Intent(this@SimpleActivity, com.simplemobiletools.studentcalendarpaid.activities.MainActivity::class.java)
                startActivity(h)
            }

         /*   com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val l = Intent(this@SimpleActivity, com.simplemobiletools.studentcalendarpro.activities.LoginActivity::class.java)
                startActivity(l)
                startActivity(Intent(applicationContext, com.simplemobiletools.studentcalendarpro.activities.LoginActivity::class.java))

            }*/


/*
            com.simplemobiletools.StudentCalendarPro.R.id.nav_grades -> {
                val i = Intent(this@SimpleActivity, com.simplemobiletools.StudentCalendarPro.activities.LoginActivity::class.java)
                startActivity(i)
            }

*/

            com.simplemobiletools.studentcalendarpaid.R.id.nav_info -> {

                val k = Intent(this@SimpleActivity, com.simplemobiletools.studentcalendarpaid.activities.InfoActivity::class.java)
                startActivity(k)
            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_overview -> {
                val j = Intent(this@SimpleActivity, EventListFragment::class.java)
                startActivity(j)
            }


        }// this is done, now let us go and intialise the home page.
        // after this lets start copying the above.
        // FOLLOW MEEEEE>>>


        val drawer = findViewById<View>(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
