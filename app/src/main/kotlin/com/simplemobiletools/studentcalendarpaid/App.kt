package com.simplemobiletools.studentcalendarpaid

import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.simplemobiletools.commons.extensions.checkUseEnglish

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        if (com.simplemobiletools.studentcalendarpaid.BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        checkUseEnglish()
    }
}
