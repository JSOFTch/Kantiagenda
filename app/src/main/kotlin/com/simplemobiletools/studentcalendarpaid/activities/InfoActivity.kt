package com.simplemobiletools.studentcalendarpaid.activities


import android.annotation.SuppressLint
import android.content.*

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.simplemobiletools.studentcalendarpaid.R
import androidx.appcompat.app.ActionBarDrawerToggle

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics


class InfoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    lateinit var userName: TextView
    private var userImage: ImageView? = null
    private var userEmail: TextView? = null
    private var versionName: TextView? = null
    private var ratebtn: Button? = null
    private var donate: Button? = null

    lateinit var mAdView : AdView
    private lateinit var firebaseAnalytics: FirebaseAnalytics



    private var mAuth: FirebaseAuth? = null
    internal lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    @SuppressLint("ResourceType")
    override fun onStart() {
        super.onStart()

        mAuth!!.addAuthStateListener(mAuthListener)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)





    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.simplemobiletools.studentcalendarpaid.R.layout.activity_info)

        MobileAds.initialize(this, "ca-app-pub-3247019304236225~1779730855")


        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-3247019304236225/8074919735"

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val params = Bundle()

        params.putString("version_name", com.simplemobiletools.studentcalendarpaid.BuildConfig.VERSION_NAME)


        mAdView.setOnClickListener { firebaseAnalytics.logEvent("Ad_clicked", params) }


        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout)
        navigationView = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.nav_view)
        versionName = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.versionName)
        ratebtn = findViewById(R.id.rateButton)
        donate = findViewById(R.id.donate)

        navigationView!!.setNavigationItemSelectedListener(this)




        ratebtn!!.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.simplemobiletools.studentcalendarpro"))) }



        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
















donate!!.setOnClickListener{ val m = Intent(this@InfoActivity, DonationsActivity::class.java)
    startActivity(m)


}



        mAuth = FirebaseAuth.getInstance()
        //val user = mAuth!!.currentUser




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
                Toast.makeText(this@InfoActivity, "You're logged out", Toast.LENGTH_SHORT).show()

            }
        }

        versionName!!.text = "version : " + com.simplemobiletools.studentcalendarpaid.BuildConfig.VERSION_NAME

        //fetchUsers()

    }











/*
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User1::class.java)
                    val personPhotoUrl = user!!.profileImageUrl
                    userImage = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userImage)


                    if (user != null) {
                        Glide.with(applicationContext).load(personPhotoUrl)
                                .thumbnail(0.3f)
                                .apply(RequestOptions.circleCropTransform())

                                .into(userImage!!)

                    }
                }

            }


        })
    }

*/
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
                val h = Intent(this@InfoActivity, com.simplemobiletools.studentcalendarpaid.activities.MainActivity::class.java)
                startActivity(h)
            }

/*
            com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val m = Intent(this@InfoActivity, AccountActivity::class.java)
                startActivity(m)
            }
           */

            R.id.nav_grades -> {
                val i = Intent(this@InfoActivity, AddGradeActivity::class.java)
                startActivity(i)
            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_overview -> {
                val j = Intent(this@InfoActivity, ViewListContents::class.java)
                startActivity(j)
            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_info -> {
                val k = Intent(this@InfoActivity, InfoActivity::class.java)
                startActivity(k)
            }

            com.simplemobiletools.studentcalendarpaid.R.id.nav_todo -> {
                val m = Intent(this@InfoActivity, TodoActivity::class.java)
                startActivity(m)

            }


        }


        val drawer = findViewById<View>(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}


