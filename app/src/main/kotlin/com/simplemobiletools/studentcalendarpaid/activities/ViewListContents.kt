package com.simplemobiletools.studentcalendarpaid.activities


import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.simplemobiletools.studentcalendarpaid.R

import java.util.ArrayList
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


/**
 * Created by Mitch on 2016-05-13.
 */
class ViewListContents : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    internal lateinit var myDB: DatabaseHelper
    internal lateinit var userList: ArrayList<User>
    internal lateinit var listView: ListView
    internal lateinit var user: User
    internal lateinit var fab: FloatingActionButton
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewcontents_layout)

        val toolbar: androidx.appcompat.widget.Toolbar
        toolbar = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.title = "Overview"
        setSupportActionBar(toolbar)







        drawerLayout = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout)
        navigationView = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.nav_view)
        navigationView!!.bringToFront()


        navigationView!!.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()


        myDB = DatabaseHelper(this)

        userList = ArrayList()
        val data = myDB.listContents
        fab = findViewById<View>(R.id.fab) as FloatingActionButton


        fab.setOnClickListener {
            val intent = Intent(this@ViewListContents, AddGradeActivity::class.java)
            startActivity(intent)
        }


        val numRows = data.count
        if (numRows == 0) {
            Toast.makeText(this@ViewListContents, "The Database is empty  :(.", Toast.LENGTH_LONG).show()
        } else {
            var i = 0
            while (data.moveToNext()) {
                user = User(data.getString(1), data.getString(2), data.getString(3))
                userList.add(i, user)
                println(data.getString(1) + " " + data.getString(2) + " " + data.getString(3))
                println(userList[i].firstName)
                i++
            }
            val adapter = ThreeColumn_ListAdapter(this, R.layout.grade_item, userList)
            listView = findViewById<View>(R.id.listView) as ListView
            listView.adapter = adapter
            listView.setOnItemClickListener { parent, view, position, id ->  val intent = Intent(this@ViewListContents, AddGradeActivity::class.java)
                startActivity(intent) }
        }

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // here is the main place where we need to work on.
        val id = item.itemId
        when (id) {

            com.simplemobiletools.studentcalendarpaid.R.id.nav_calendar -> {
                val h = Intent(this@ViewListContents, com.simplemobiletools.studentcalendarpaid.activities.MainActivity::class.java)
                startActivity(h)
            }


            com.simplemobiletools.studentcalendarpaid.R.id.nav_grades -> {
                val i = Intent(this@ViewListContents, AddGradeActivity::class.java)
                startActivity(i)
            }


            com.simplemobiletools.studentcalendarpaid.R.id.nav_overview -> {
                val j = Intent(this@ViewListContents, ViewListContents::class.java)
                startActivity(j)
            }
            /* com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val l = Intent(this@GradeActivity, com.simplemobiletools.studentcalendarpro.activities.LoginActivity::class.java)
                startActivity(l)
                startActivity(l)

            }
*/
            com.simplemobiletools.studentcalendarpaid.R.id.nav_info -> {
                val k = Intent(this@ViewListContents, com.simplemobiletools.studentcalendarpaid.activities.InfoActivity::class.java)
                startActivity(k)


            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_todo -> {
                val m = Intent(this@ViewListContents, TodoActivity::class.java)
                startActivity(m)

            }


        }


        val drawer = findViewById<View>(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


}
