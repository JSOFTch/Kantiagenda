package com.simplemobiletools.studentcalendarpaid.activities


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simplemobiletools.studentcalendarpaid.R


class SplashScreenActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)



        val background = object : Thread(){
            override fun run() {
            try {
                Thread.sleep(1000)
                val intent = Intent(baseContext, TodoActivity::class.java)
                startActivity(intent)

            } catch (e:Exception){
                e.printStackTrace()
            }

                        }


        }

        background.start()


    }
}

