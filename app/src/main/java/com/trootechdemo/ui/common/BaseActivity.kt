package com.kloeapp.ui.common

import android.animation.Animator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.kloeapp.App
import com.kloeapp.R
import com.kloeapp.model.request.CreateTagRequest
import com.kloeapp.model.request.SetPreferencesRequest
import com.kloeapp.restapi.api.ApiCallback
import com.kloeapp.restapi.api.ApiServiceProvider
import com.kloeapp.ui.tags.TagsSetupCompletedActivity
import com.kloeapp.utils.CommonMethods
import com.kloeapp.utils.Progress
import com.kloeapp.utils.Utils
import com.kloeapp.utils.session_manager.MyPrefs
import com.kloeapp.utils.session_manager.SessionManager
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


open class BaseActivity : AppCompatActivity(), LifecycleObserver, Animator.AnimatorListener {

    @Inject
    lateinit var myPrefs: MyPrefs


    lateinit var progress: Progress

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setProgressbar()
    }

    fun setProgressbar() {
        progress = Progress(this, lifecycle)
        progress.setCancelable(false)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(App.localeManager!!.setLocale(base))
    }

    private var mCurrentLocale: Locale? = null

    override fun onStart() {
        super.onStart()
        mCurrentLocale = Locale(SessionManager.getLanguage(this))

        Log.e("mCurrentLocale=----=-> ", "$mCurrentLocale")
    }

    override fun onRestart() {
        super.onRestart()
        val locale: Locale = CommonMethods.getLocale(this)
        if (locale != mCurrentLocale) {
            mCurrentLocale = locale
            recreate()
        }
    }

    fun warn(msg: String?) {
        CommonMethods.ShowToastWarn(this@BaseActivity, msg)
    }

    fun successMessage(msg: String?) {
        CommonMethods.ShowToastSuccess(this@BaseActivity, msg)
    }

    fun checkAccessAndDisplayMessage(code: Int, message: String?) {
        Log.e("code : ", "$code nn ")
        if (code == 403) {
            warn(message)
            //GeneralUtils.gotoLogin(this);
        } else {
            if (message != null && !message.isEmpty()) {
                if (message.equals("Session expired", ignoreCase = true)) {
                    warn(message)
                    //GeneralUtils.gotoLogin(this);
                } else {
                    warn(message)
                }
            } else {
                warn(message)
            }
        }
    }

    var progressAnimation: LottieAnimationView? = null
    var loaderDialog: Dialog? = null
    fun loading(isShow: Boolean) {
        if (loaderDialog == null) {
            loaderDialog = Dialog(this, R.style.FullScreenDialogStyle)
            loaderDialog!!.setCancelable(false)
            loaderDialog!!.setContentView(R.layout.loader_layout)

            progressAnimation =
                loaderDialog!!.findViewById<LottieAnimationView>(R.id.progressAnimation)

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

        if (!(this as Activity).isFinishing) {
            if (isShow) loaderDialog!!.show() else loaderDialog!!.dismiss()
        }

    }

    fun startAct(cls: Class<*>?, isFinishThis: Boolean) {
        startActivity(Intent(this, cls))
        if (isFinishThis) finish()
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
        if (loaderDialog != null && loaderDialog!!.isShowing) {
            loaderDialog!!.cancel()
        }
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
        return if (Utils.isOnline(this)) {
            true
        } else {
            //showSnackBar("No internet available", R.color.red)
            startActivity(Intent(this, NoInternetActivity::class.java))
            finishAffinity()
            false
        }
    }

    fun showSnackBar(message: String, color: Int) {

        val viewGroup =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup

        val snackBarView = Snackbar.make(viewGroup.getChildAt(0), message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(
                resources.getColor(
                    color, null
                )
            )
        val layoutParams = snackBarView.view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP
        snackBarView.view.layoutParams = layoutParams
        snackBarView.show()
    }


    fun loadFragment(fragment: Fragment) {
        /* val transaction = supportFragmentManager.beginTransaction()
         transaction.replace(R.id.flFragments, fragment)
         transaction.addToBackStack(null)
         transaction.commit()*/
    }


    //Todo: Create Tag Update.... Start
    var caption: String = ""
    var country_name: String = ""
    var home_town: String = ""
    var birthplace: String = ""
    var occupation: String = ""
    var height: Int = 0
    var religion: String = ""
    var smoking: Int = 0
    var drinking: Int = 0
    var late_night: Int = 0
    var bio: String = ""
    var you_fav_quote: String = ""
    var fav_choice_1: String = ""
    var fav_choice_2: String = ""
    var fav_choice_3: String = ""
    var instalink: String = ""
    var tiktok: String = ""
    var signupRequest = CreateTagRequest()

    fun callCreateProfileApi(apiServiceProvider: ApiServiceProvider, isFinish: Boolean) {

        if (SessionManager.getTagsDetails(this) != null) {
            setTagsData()
            signupRequest = CreateTagRequest(
                caption,
                country_name,
                home_town,
                birthplace,
                occupation,
                height,
                religion,
                smoking,
                drinking,
                late_night,
                bio,
                you_fav_quote,
                fav_choice_1,
                fav_choice_2,
                fav_choice_3,
                instalink,
                tiktok
            )
        } else {
            signupRequest = CreateTagRequest(
                caption,
                country_name,
                home_town,
                birthplace,
                occupation,
                height,
                religion,
                smoking,
                drinking,
                late_night,
                bio,
                you_fav_quote,
                fav_choice_1,
                fav_choice_2,
                fav_choice_3,
                instalink,
                tiktok
            )
        }

        progress.show()
        lifecycleScope.launch {
            apiServiceProvider.callPostCreateProfileApi(signupRequest).collect {
                when (it) {
                    is ApiCallback.OnSuccess<*> -> {
                        progress.hide()
                        if (it.data!!.status == 1) {

                            SessionManager.setTagsDetails(this@BaseActivity, signupRequest)

                            if (!isFinish)
                                finish()
                            else {
                                startActivity(
                                    Intent(
                                        this@BaseActivity,
                                        TagsSetupCompletedActivity::class.java
                                    )
                                )
                                finish()
                            }
                        } else {
                            showSnackBar(it.data.message, R.color.red)
                        }
                    }

                    is ApiCallback.OnError<*> -> {
                        progress.hide()
                        showSnackBar(it.message.toString(), R.color.red)
                    }
                }
            }
        }

    }

    private fun setTagsData() {
        if (bio == "") {
            if (SessionManager.getTagsDetails(this)!!.bio != null)
                bio = SessionManager.getTagsDetails(this)!!.bio!!
        }

        if (country_name == "") {
            if (SessionManager.getTagsDetails(this)!!.country_name != null)
                country_name = SessionManager.getTagsDetails(this)!!.country_name!!
        }

        if (home_town == "") {
            if (SessionManager.getTagsDetails(this)!!.home_town != null)
                home_town = SessionManager.getTagsDetails(this)!!.home_town!!
        }

        if (birthplace == "") {
            if (SessionManager.getTagsDetails(this)!!.birthplace != null)
                birthplace = SessionManager.getTagsDetails(this)!!.birthplace!!
        }

        if (occupation == "") {
            if (SessionManager.getTagsDetails(this)!!.occupation != null)
                occupation = SessionManager.getTagsDetails(this)!!.occupation!!
        }
        if (height == 0) {
            if (SessionManager.getTagsDetails(this)!!.height != null)
                height = SessionManager.getTagsDetails(this)!!.height!!
        }
        if (religion == "") {
            if (SessionManager.getTagsDetails(this)!!.religion != null)
                religion = SessionManager.getTagsDetails(this)!!.religion!!
        }
        if (smoking == 0) {
            if (SessionManager.getTagsDetails(this)!!.smoking != null)
                smoking = SessionManager.getTagsDetails(this)!!.smoking!!
        }
        if (drinking == 0) {
            if (SessionManager.getTagsDetails(this)!!.drinking != null)
                drinking = SessionManager.getTagsDetails(this)!!.drinking!!
        }
        if (late_night == 0) {
            if (SessionManager.getTagsDetails(this)!!.late_night != null)
                late_night = SessionManager.getTagsDetails(this)!!.late_night!!
        }

        if (you_fav_quote == "") {
            if (SessionManager.getTagsDetails(this)!!.you_fav_quote != null)
                you_fav_quote = SessionManager.getTagsDetails(this)!!.you_fav_quote!!
        }
        if (fav_choice_1 == "") {
            if (SessionManager.getTagsDetails(this)!!.fav_choice_1 != null)
                fav_choice_1 = SessionManager.getTagsDetails(this)!!.fav_choice_1!!
        }
        if (fav_choice_2 == "") {
            if (SessionManager.getTagsDetails(this)!!.fav_choice_2 != null)
                fav_choice_2 = SessionManager.getTagsDetails(this)!!.fav_choice_2!!
        }
        if (fav_choice_3 == "") {
            if (SessionManager.getTagsDetails(this)!!.fav_choice_3 != null)
                fav_choice_3 = SessionManager.getTagsDetails(this)!!.fav_choice_3!!
        }
        if (instalink == "") {
            if (SessionManager.getTagsDetails(this)!!.instalink != null)
                instalink = SessionManager.getTagsDetails(this)!!.instalink!!
        }
        if (tiktok == "") {
            if (SessionManager.getTagsDetails(this)!!.tiktok != null)
                tiktok = SessionManager.getTagsDetails(this)!!.tiktok!!
        }
    }

    //Todo: Create Tag Update.... End

    //Todo: Set Preferences... Start
    var age_prefer: String = ""
    var looking_for_prefer: String = ""
    var distance_prefer: String = ""
    var drinking_prefer: String = ""
    var smoking_prefer: String = ""
    var late_night_prefer: String = ""
    var searching_for_id_prefer: String = ""

    var setPreferencesRequest = SetPreferencesRequest()
    fun callSetPreferencesApi(apiServiceProvider: ApiServiceProvider, isFinish: Boolean) {

        if (SessionManager.getPreferencesDetails(this) != null) {
            setPreferencesData()
            setPreferencesRequest = SetPreferencesRequest(
                age_prefer,
                looking_for_prefer,
                distance_prefer,
                drinking_prefer,
                smoking_prefer,
                late_night_prefer,
                searching_for_id_prefer
            )
        } else {
            setPreferencesRequest = SetPreferencesRequest(
                age_prefer,
                looking_for_prefer,
                distance_prefer,
                drinking_prefer,
                smoking_prefer,
                late_night_prefer,
                searching_for_id_prefer
            )
        }
        progress.show()
        lifecycleScope.launch {
            apiServiceProvider.callSetPreferenceApi(setPreferencesRequest).collect {
                when (it) {
                    is ApiCallback.OnSuccess<*> -> {
                        progress.hide()
                        if (it.data!!.status == 1) {

                            showSnackBar("Preferences Update successfully.", R.color.green_bg)

                            SessionManager.setPreferencesDetails(
                                this@BaseActivity,
                                setPreferencesRequest
                            )



                            if (!isFinish)
                                finish()
                            else {
                                finish()
                            }
                        } else {
                            showSnackBar(it.data.message, R.color.red)
                        }
                    }

                    is ApiCallback.OnError<*> -> {
                        progress.hide()
                        showSnackBar(it.message.toString(), R.color.red)
                    }
                }
            }
        }

    }

    private fun setPreferencesData() {
        if (age_prefer == "") {
            if (SessionManager.getPreferencesDetails(this)!!.agePrefer != null)
                age_prefer = SessionManager.getPreferencesDetails(this)!!.agePrefer!!
        }

        if (looking_for_prefer == "") {
            if (SessionManager.getPreferencesDetails(this)!!.lookingForPrefer != null)
                looking_for_prefer = SessionManager.getPreferencesDetails(this)!!.lookingForPrefer!!
        }

        if (distance_prefer == "") {
            if (SessionManager.getPreferencesDetails(this)!!.distancePrefer != null)
                distance_prefer = SessionManager.getPreferencesDetails(this)!!.distancePrefer!!
        }

        if (drinking_prefer == "") {
            if (SessionManager.getPreferencesDetails(this)!!.drinkingPrefer != null)
                drinking_prefer = SessionManager.getPreferencesDetails(this)!!.drinkingPrefer!!
        }

        if (smoking_prefer == "") {
            if (SessionManager.getPreferencesDetails(this)!!.smokingPrefer != null)
                smoking_prefer = SessionManager.getPreferencesDetails(this)!!.smokingPrefer!!
        }

        if (late_night_prefer == "") {
            if (SessionManager.getPreferencesDetails(this)!!.lateNightPrefer != null)
                late_night_prefer = SessionManager.getPreferencesDetails(this)!!.lateNightPrefer!!
        }
        if (searching_for_id_prefer == "") {
            if (SessionManager.getPreferencesDetails(this)!!.searchingForIdPrefer != null)
                searching_for_id_prefer =
                    SessionManager.getPreferencesDetails(this)!!.searchingForIdPrefer!!
        }

    }
    //Todo: Set Preferences... End
}