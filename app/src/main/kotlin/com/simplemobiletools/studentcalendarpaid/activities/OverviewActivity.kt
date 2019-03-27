package com.simplemobiletools.studentcalendarpaid.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.simplemobiletools.studentcalendarpaid.R


class OverviewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.simplemobiletools.studentcalendarpaid.R.layout.activity_overview)


        val toolbar: androidx.appcompat.widget.Toolbar
        toolbar = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout)
        navigationView = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.nav_view)
        navigationView!!.bringToFront()


        navigationView!!.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                com.simplemobiletools.studentcalendarpaid.R.string.navigation_draw_open, com.simplemobiletools.studentcalendarpaid.R.string.navigation_draw_close)
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
                        .into(userImage!!)*/
            }

            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this@OverviewActivity, "You're logged out", Toast.LENGTH_SHORT).show()

            }
        }
    }


    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // here is the main place where we need to work on.
        val id = item.itemId
        when (id) {

            com.simplemobiletools.studentcalendarpaid.R.id.nav_calendar -> {
                val h = Intent(this@OverviewActivity, com.simplemobiletools.studentcalendarpaid.activities.MainActivity::class.java)
                startActivity(h)
            }


            R.id.nav_grades -> {
                val i = Intent(this@OverviewActivity, GradeActivity::class.java)
                startActivity(i)
            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_overview -> {
                val j = Intent(this@OverviewActivity, OverviewActivity::class.java)
                startActivity(j)
            }
            com.simplemobiletools.studentcalendarpaid.R.id.nav_info -> {
                val k = Intent(this@OverviewActivity, com.simplemobiletools.studentcalendarpaid.activities.InfoActivity::class.java)
                startActivity(k)
            }
          /*  com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val l = Intent(this@OverviewActivity, AccountActivity::class.java)
                startActivity(l)

            }*/
        }


        val drawer = findViewById<View>(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
