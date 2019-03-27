package com.simplemobiletools.studentcalendarpaid.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.simplemobiletools.studentcalendarpaid.R

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class AddGradeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener  {

    lateinit var etFirstName: EditText
    internal lateinit var etLastName: EditText
    internal lateinit var etFavFood: EditText
    internal lateinit var btnAdd: Button
    internal lateinit var btnView: Button
    internal lateinit var myDB: DatabaseHelper
    internal lateinit var fab: FloatingActionButton
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_grade)
        etFavFood = findViewById<View>(R.id.etFavFood) as EditText
        etFirstName = findViewById<View>(R.id.etFirstName) as EditText
        etLastName = findViewById<View>(R.id.etLastName) as EditText
        btnAdd = findViewById<View>(R.id.btnAdd) as Button
        myDB = DatabaseHelper(this)

        val toolbar: androidx.appcompat.widget.Toolbar
        toolbar = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.title = "Add Grades"
        setSupportActionBar(toolbar)






        drawerLayout = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout)
        navigationView = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.nav_view)
        navigationView!!.bringToFront()


        navigationView!!.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()




        btnAdd.setOnClickListener {
            val fName = etFirstName.text.toString()
            val lName = etLastName.text.toString()
            val fFood = etFavFood.text.toString()
            if (fName.length != 0 && lName.length != 0 && fFood.length != 0) {
                AddData(fName, lName, fFood)
                etFavFood.setText("")
                etLastName.setText("")
                etFirstName.setText("")
            } else {
                Toast.makeText(this@AddGradeActivity, "You must put something in the text field!", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // here is the main place where we need to work on.
        val id = item.itemId
        when (id) {

            com.simplemobiletools.studentcalendarpaid.R.id.nav_calendar -> {
                val h = Intent(this@AddGradeActivity, com.simplemobiletools.studentcalendarpaid.activities.MainActivity::class.java)
                startActivity(h)
            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_grades -> {
                val i = Intent(this@AddGradeActivity, AddGradeActivity::class.java)
                startActivity(i)
            }


            com.simplemobiletools.studentcalendarpaid.R.id.nav_overview -> {
                val j = Intent(this@AddGradeActivity, ViewListContents::class.java)
                startActivity(j)
            }
            /* com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val l = Intent(this@GradeActivity, com.simplemobiletools.studentcalendarpro.activities.LoginActivity::class.java)
                startActivity(l)
                startActivity(l)

            }
*/
            com.simplemobiletools.studentcalendarpaid.R.id.nav_info -> {
                val k = Intent(this@AddGradeActivity, com.simplemobiletools.studentcalendarpaid.activities.InfoActivity::class.java)
                startActivity(k)


            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_todo -> {
                val m = Intent(this@AddGradeActivity, TodoActivity::class.java)
                startActivity(m)

            }


        }


        val drawer = findViewById<View>(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun AddData(firstName: String, lastName: String, favFood: String) {
        val insertData = myDB.addData(firstName, lastName, favFood)

        if (insertData == true) {
            Toast.makeText(this@AddGradeActivity, "Successfully Entered Data!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this@AddGradeActivity, "Something went wrong :(.", Toast.LENGTH_LONG).show()
        }
    }
}
