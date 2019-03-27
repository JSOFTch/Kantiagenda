package com.simplemobiletools.studentcalendarpaid.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
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
import kotlinx.android.synthetic.main.activity_login.*


class AccountActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    lateinit var userName: TextView
    private var deleteAccountBtn: Button? = null
    private var userImage: ImageView? = null
    private var userEmail: TextView? = null
    private var profilepicture: ImageView? = null
    private var profilename: TextView? = null
    private var loginBtn: Button? = null
    private var mAuth: FirebaseAuth? = null
    internal lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    override fun onStart() {
        super.onStart()

        mAuth!!.addAuthStateListener(mAuthListener)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        toolbar.title = "My Account"

       verifyUserIsLoggedIn()
        deleteAccountBtn = findViewById(R.id.deleteAccount)
        loginBtn = findViewById(R.id.loginButton)

        profilepicture = this.findViewById(com.simplemobiletools.studentcalendarpaid.R.id.profilepicture)
        profilename = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.profilename)

        mAuth = FirebaseAuth.getInstance()

        val user = mAuth!!.currentUser

        /*

        txtEmailAddress = (EditText) findViewById(R.id.email);
        txtPassword = (EditText) findViewById(R.id.password);
        signInBtn = findViewById(R.id.signInBtn);
        registration = findViewById(R.id.registration);

*/

        drawerLayout = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout)
        navigationView = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.nav_view)

        navigationView!!.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
        loginBtn!!.visibility = View.GONE

        deleteAccountBtn!!.setOnClickListener{

            val user = FirebaseAuth.getInstance().currentUser

            user?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                        }
                    }
            loginBtn!!.visibility = View.VISIBLE

        }
        loginBtn!!.setOnClickListener{ val l = Intent(this@AccountActivity, LoginActivity::class.java)
            startActivity(l)

        }

        mAuth = FirebaseAuth.getInstance()


        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
/*
                val personName = user!!.displayName
                //val personPhotoUrl = Objects.requireNonNull<Uri>(user.photoUrl).toString()
                val email = user.email

                userEmail = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userEmail)
                userName = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userName)
                userImage = findViewById(com.simplemobiletools.studentcalendarpro.R.id.userImage)




                userName.text = personName
                userEmail!!.text = email
                profilename!!.text = personName



                Glide.with(applicationContext).load(personPhotoUrl)
                        .thumbnail(0.3f)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilepicture!!)

                Glide.with(applicationContext).load(personPhotoUrl)
                        .thumbnail(0.3f)
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage!!)*/
            }

            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this@AccountActivity, "You're logged out", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val a = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(a)

        }


    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // here is the main place where we need to work on.
        val id = item.itemId
        when (id) {

            com.simplemobiletools.studentcalendarpaid.R.id.nav_calendar -> {
                val h = Intent(this@AccountActivity, MainActivity::class.java)
                startActivity(h)
            }
            com.simplemobiletools.studentcalendarpaid.R.id.nav_info ->{
                val k = Intent(this@AccountActivity, InfoActivity::class.java)
                startActivity(k)
            }

/*
            com.simplemobiletools.StudentCalendarPro.R.id.nav_grades -> {
                val i = Intent(this@LoginActivity, com.simplemobiletools.StudentCalendarPro.activities.GradeActivity::class.java)
                startActivity(i)
            }
*/
            com.simplemobiletools.studentcalendarpaid.R.id.nav_overview -> {
                val j = Intent(this@AccountActivity, OverviewActivity::class.java)
                startActivity(j)
            }
/*
            com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val l = Intent(this@AccountActivity, AccountActivity::class.java)
                startActivity(l)

            }
            */



            com.simplemobiletools.studentcalendarpaid.R.id.nav_todo -> {
                val m = Intent(this@AccountActivity, TodoActivity::class.java)
                startActivity(m)

            }


        }// this is done, now let us go and intialise the home page.
        // after this lets start copying the above.
        // FOLLOW MEEEEE>>>


        val drawer = findViewById<View>(com.simplemobiletools.studentcalendarpaid.R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }


    }


}
