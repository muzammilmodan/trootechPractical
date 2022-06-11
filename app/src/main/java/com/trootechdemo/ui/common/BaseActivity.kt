package com.trootechdemo.ui.common

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.trootechdemo.utils.Progress
import com.trootechdemo.utils.Utils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
open class BaseActivity : AppCompatActivity(){

    lateinit var progress: Progress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setProgressbar()
    }

    //Common used this progressbar
    fun setProgressbar() {
        progress = Progress(this, lifecycle)
        progress.setCancelable(false)
    }



    fun hideSoftKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboard() {
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        hideSoftKeyboard(view)
    }

    // ========================================== Internet Connection ===================================================================
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    //Internet connection available or not
    fun isOnline(): Boolean {
        return if (Utils.isOnline(this)) {
            true
        } else {
            //showSnackBar("No internet available", R.color.red)
            startActivity(Intent(this, NoInternetActivity::class.java))
            finishAffinity()
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showSnackBar(message: String, color: Int) {

        val viewGroup =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup

        val snackBarView = Snackbar.make(viewGroup.getChildAt(0), message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(color, null)
            )
        val layoutParams = snackBarView.view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP
        snackBarView.view.layoutParams = layoutParams
        snackBarView.show()
    }
}