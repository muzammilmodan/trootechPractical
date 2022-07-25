package com.kloeapp.ui.common

import android.animation.Animator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import com.airbnb.lottie.LottieAnimationView
import com.kloeapp.R
import com.kloeapp.utils.CommonMethods
import com.kloeapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment(), LifecycleObserver, Animator.AnimatorListener {

    open lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext=activity!!
    }

    fun warn(msg: String?) {
        CommonMethods.ShowToastWarn(mContext, msg)
    }

    fun successMessage(msg: String?) {
        CommonMethods.ShowToastSuccess(mContext, msg)
    }

    var loaderDialog: Dialog? = null
    var progressAnimation: LottieAnimationView? = null
    fun loading(isShow: Boolean) {
        try {
            if (loaderDialog == null) {
                loaderDialog = Dialog(requireContext(), R.style.FullScreenDialogStyle)
                loaderDialog!!.setCancelable(false)
                loaderDialog!!.setContentView(R.layout.loader_layout)
                progressAnimation = loaderDialog!!.findViewById<LottieAnimationView>(R.id.progressAnimation)

                progressAnimation?.apply {
                    setAnimation("progress.json")
                    loop(false)
                    playAnimation()
                }
                progressAnimation!!.addAnimatorListener(this)


                val window = loaderDialog!!.window
                if (window != null) {
                    window.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    window.setBackgroundDrawable(ColorDrawable(0))
                }
            }

            if (!(requireContext() as Activity).isFinishing) {
                if (isShow) {
                    if (!loaderDialog!!.isShowing)
                        loaderDialog!!.show()
                } else
                    loaderDialog!!.dismiss()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun startAct(cls: Class<*>?, isFinishThis: Boolean) {
        startActivity(Intent(mContext, cls))
        if (isFinishThis) requireActivity().finish()
    }

    fun hideSoftKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboard() {
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(mContext)
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

    override fun onAnimationStart(p0: Animator?) {

    }

    override fun onAnimationEnd(p0: Animator?) {
    }

    override fun onAnimationCancel(p0: Animator?) {

    }

    override fun onAnimationRepeat(p0: Animator?) {
    }

    //Internet connection available or not
    fun isOnline(): Boolean {
        return if (Utils.isOnline(requireContext())) {
            true
        } else {
            //showSnackBar("No internet available", R.color.red)
            startActivity(Intent(mContext, NoInternetActivity::class.java))
            false
        }
    }

}