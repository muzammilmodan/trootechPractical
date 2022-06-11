package com.trootechdemo.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.trootechdemo.R


class Progress(context: Context, val lifecycle: Lifecycle) : LifecycleObserver {

    private var dialog: Dialog
    private var isVisible: Boolean = false
    private var view: View

    init {
        lifecycle.addObserver(this)
        dialog = Dialog(context)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view = LayoutInflater.from(context).inflate(R.layout.custom_loading_dialog, LinearLayout(context), false)
        dialog.setContentView(view)
    }

    fun setCancelable(isCancelable: Boolean) {
        dialog.setCancelable(isCancelable)
    }

    fun hide() {
        if (dialog.isShowing) {
            dialog.dismiss()
            isVisible = false
        }
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
            isVisible = true
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        hide()
    }
}