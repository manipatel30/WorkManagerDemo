package com.workmanagerdemo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location

class App : Application() {


    override fun onCreate() {
        super.onCreate()

        // initialise app as a singleton
        sInstance = this
    }

    companion object {

        // Needs to be volatile as another thread can see a half initialised instance.
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private lateinit var sInstance: App

        var currentLocation: Location? = null

        fun getInstance(): App {
            if (sInstance == null) {
                synchronized(App::class.java) {
                    if (sInstance == null) {
                        sInstance = App()
                    }
                }
            }
            return sInstance
        }

        val context: Context
            get() = sInstance.applicationContext

    }

}