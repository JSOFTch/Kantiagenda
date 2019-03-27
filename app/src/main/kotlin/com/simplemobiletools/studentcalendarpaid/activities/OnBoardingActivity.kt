package com.simplemobiletools.studentcalendarpaid.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.simplemobiletools.studentcalendarpaid.R

class OnBoardingActivity : AppIntro() {

    val COMPLETED_ONBOARDING_PREF_NAME: Boolean? =false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(AppIntroFragment.newInstance("Navigation Drawer", "Swipe from the left side to get access to all our features!", R.drawable.nav_drawer, Color.parseColor("#f64c73")))
        addSlide(AppIntroFragment.newInstance("Reminder", "Set up a reminder so you'll never forget about an event again!", R.drawable.reminderscreen, Color.parseColor("#20d2bb")))
        addSlide(AppIntroFragment.newInstance("To Do List!", "Keep track of your latest events!", R.drawable.todolist_icon1, Color.parseColor("#3395ff")))
        // setFadeAnimation();
        //   setDepthAnimation()
           setFadeAnimation()
        //        setZoomAnimation();
        //       setFlowAnimation();
        //     setSlideOverAnimation();
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.
        val h = Intent(this@OnBoardingActivity, MainActivity::class.java)
        startActivity(h)
        Toast.makeText(this, "You Pressed Skip!", Toast.LENGTH_SHORT).show()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Do something when users tap on Done button.
        val h = Intent(this@OnBoardingActivity, MainActivity::class.java)
        startActivity(h)
        Toast.makeText(this, "You Pressed Done!!", Toast.LENGTH_SHORT).show()

        }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        // Do something when the slide changes.

    }



}
