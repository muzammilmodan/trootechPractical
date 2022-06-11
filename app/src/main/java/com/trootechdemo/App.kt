package com.trootechdemo

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp

/**
 * Regarding Hilt required Hilt Initialization first, So add this line.
 **/
@HiltAndroidApp
class App : Application() {


    val TAG = "App"
    companion object {
        lateinit var context: Context
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this) //Dex memory management after 64k support provide
    }


    override fun onCreate() {
        super.onCreate()
        context = this

        init()
    }

    fun init() {
    }
}