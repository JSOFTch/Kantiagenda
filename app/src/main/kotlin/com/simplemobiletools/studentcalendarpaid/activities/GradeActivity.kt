package com.simplemobiletools.studentcalendarpaid.activities


import android.app.DialogFragment
import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_todo.*
import org.json.JSONArray
import java.util.ArrayList
import com.firebase.ui.auth.data.model.User
import com.simplemobiletools.studentcalendarpaid.R


class GradeActivity: AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {


    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    lateinit var userName: TextView
    private var userImage: ImageView? = null
    private var userEmail: TextView? = null

    private var mAuth: FirebaseAuth? = null
    internal lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    override fun onStart() {
        super.onStart()

        mAuth!!.addAuthStateListener(mAuthListener)

    }


    override fun onPostResume() {
        super.onPostResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)


        val toolbar: androidx.appcompat.widget.Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.title = "My Grades"
        setSupportActionBar(toolbar)






        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView!!.bringToFront()


        navigationView!!.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()




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
                // Toast.makeText(this@GradeActivity, "You're logged out", Toast.LENGTH_SHORT).show()

            }
        }












        fab.setOnClickListener { }

    }

    override fun onPause() {
        super.onPause()


    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // here is the main place where we need to work on.
        val id = item.itemId
        when (id) {

            R.id.nav_calendar -> {
                val h = Intent(this@GradeActivity, MainActivity::class.java)
                startActivity(h)
            }

            R.id.nav_grades -> {
                val i = Intent(this@GradeActivity, GradeActivity::class.java)
                startActivity(i)
            }


            R.id.nav_overview -> {
                val j = Intent(this@GradeActivity, OverviewActivity::class.java)
                startActivity(j)
            }
            /* com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val l = Intent(this@GradeActivity, com.simplemobiletools.studentcalendarpro.activities.LoginActivity::class.java)
                startActivity(l)
                startActivity(l)

            }
*/
            R.id.nav_info -> {
                val k = Intent(this@GradeActivity, InfoActivity::class.java)
                startActivity(k)


            }

            R.id.nav_todo -> {
                val m = Intent(this@GradeActivity, TodoActivity::class.java)
                startActivity(m)

            }


        }


        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


}
