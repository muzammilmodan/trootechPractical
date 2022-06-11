package com.trootechdemo.ui.common

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.trootechdemo.R
import com.trootechdemo.databinding.ActivityNoInternetBinding


class NoInternetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoInternetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_no_internet)

        init()
    }

    private fun init() {

        binding.btnRestartApp.setOnClickListener {

            val packageManager: PackageManager = this.packageManager
            val intent = packageManager.getLaunchIntentForPackage(this.packageName)
            val componentName = intent!!.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }
    }
}