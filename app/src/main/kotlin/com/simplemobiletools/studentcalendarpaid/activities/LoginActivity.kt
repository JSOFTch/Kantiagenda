package com.simplemobiletools.studentcalendarpaid.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.simplemobiletools.commons.extensions.appLaunched
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var prefs: SharedPreferences? = null

    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val mGoogleApiClient: GoogleApiClient? = null
    private var signInButton: SignInButton? = null
    private var button1: Button? = null
    lateinit var userName: TextView
    private var userImage: ImageView? = null
    private var userEmail: TextView? = null
    private var profilepicture: ImageView? = null
    private var profilename: TextView? = null
    private val txtEmailAddress: EditText? = null
    private val txtPassword: EditText? = null
    private val signInBtn: Button? = null
    private val registration: Button? = null


    private var mAuth: FirebaseAuth? = null
    internal var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.simplemobiletools.studentcalendarpaid.R.layout.activity_login)
        appLaunched(com.simplemobiletools.studentcalendarpaid.BuildConfig.APPLICATION_ID)




        prefs = getSharedPreferences("com.simplemobiletools.studentcalendar", MODE_PRIVATE)
        if (prefs!!.getBoolean("firstrun", true)) {
            val h = Intent(this@LoginActivity, OnBoardingActivity::class.java)
            startActivity(h)
            prefs!!.edit().putBoolean("firstrun", false).apply();
        }

        mAuth = FirebaseAuth.getInstance()


        signInButton = findViewById<View>(com.simplemobiletools.studentcalendarpaid.R.id.sign_in_button) as SignInButton
        profilepicture = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.profilepicture)
        profilename = findViewById(com.simplemobiletools.studentcalendarpaid.R.id.profilename)
        mAuth = FirebaseAuth.getInstance()

        val currenUser = mAuth!!.currentUser

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
                com.simplemobiletools.studentcalendarpaid.R.string.navigation_draw_open, com.simplemobiletools.studentcalendarpaid.R.string.navigation_draw_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("156153175964-asjkli0fjui1ealaqt2hardmnqu16tsc.apps.googleusercontent.com")
                .requestEmail()
                .build()



        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton!!.setOnClickListener { signIn() }


        if (mAuth!!.currentUser != null) {
            val metadata = mAuth!!.currentUser!!.metadata
            if (metadata!!.creationTimestamp == metadata.lastSignInTimestamp) {
                Toast.makeText(this@LoginActivity, "Welcome ", Toast.LENGTH_LONG).show()


            } else {
                Toast.makeText(this@LoginActivity, "Welcome back ", Toast.LENGTH_LONG).show()
            }

        }


    }


    override fun onStart() {
        super.onStart()

        //mAuth.addAuthStateListener(mAuthListener);


        /*
        if (mAuth.getCurrentUser() != null);

            FirebaseUser user = mAuth.getCurrentUser();
            updateUIEmail(user);

*/


        if (GoogleSignIn.getLastSignedInAccount(this) != null) {

            val account = GoogleSignIn.getLastSignedInAccount(this)
            firebaseAuthWithGoogle(account!!)

        }


    }

    private fun signInSilently() {
        val signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        signInClient.silentSignIn().addOnCompleteListener(this
        ) { task ->
            if (task.isSuccessful) {
                // The signed in account is stored in the task's result.
                val signedInAccount = task.result!!
                firebaseAuthWithGoogle(signedInAccount)
            } else {
                // Player will need to sign-in explicitly using via UI
            }
        }
    }



    protected fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }


        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "signInWithCredential:success");
                        val user = mAuth!!.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        //Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        //firebaseAuthUpdateUI(null);
                    }

                    // ...
                }
    }





    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            /*

            val personName = user.displayName
            val personPhotoUrl = Objects.requireNonNull<Uri>(user.photoUrl).toString()
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
                    .into(userImage!!)


            val signInButton = findViewById<SignInButton>(com.simplemobiletools.studentcalendarpro.R.id.sign_in_button)
            signInButton.visibility = View.GONE

            /*
            txtEmailAddress.setVisibility(View.GONE);
            txtPassword.setVisibility(View.GONE);
            registration.setVisibility(View.GONE);
            signInBtn.setVisibility(View.GONE);
*/
        */} else {

            val signInButton1 = findViewById<SignInButton>(com.simplemobiletools.studentcalendarpaid.R.id.sign_in_button)
            signInButton1.visibility = View.VISIBLE
            Toast.makeText(this@LoginActivity, "Something went wrong2", Toast.LENGTH_SHORT).show()

        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // here is the main place where we need to work on.
        val id = item.itemId
        when (id) {

            com.simplemobiletools.studentcalendarpaid.R.id.nav_calendar -> {
                val h = Intent(this@LoginActivity, com.simplemobiletools.studentcalendarpaid.activities.MainActivity::class.java)
                startActivity(h)
            }
            com.simplemobiletools.studentcalendarpaid.R.id.nav_info ->{
                val k = Intent(this@LoginActivity, com.simplemobiletools.studentcalendarpaid.activities.InfoActivity::class.java)
                startActivity(k)
            }

/*
            com.simplemobiletools.StudentCalendarPro.R.id.nav_grades -> {
                val i = Intent(this@LoginActivity, com.simplemobiletools.StudentCalendarPro.activities.GradeActivity::class.java)
                startActivity(i)
            }
*/
            com.simplemobiletools.studentcalendarpaid.R.id.nav_overview -> {
                val j = Intent(this@LoginActivity, com.simplemobiletools.studentcalendarpaid.activities.OverviewActivity::class.java)
                startActivity(j)
            }

          /*  com.simplemobiletools.studentcalendarpro.R.id.nav_account -> {
                val l = Intent(this@LoginActivity, AccountActivity::class.java)
                startActivity(l)

            }*/

            com.simplemobiletools.studentcalendarpaid.R.id.nav_todo -> {
                val m = Intent(this@LoginActivity, TodoActivity::class.java)
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


     fun onClick(v: View) {
        when (v.id) {
            com.simplemobiletools.studentcalendarpaid.R.id.sign_in_button -> signIn()
        }// ...


    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }














}
