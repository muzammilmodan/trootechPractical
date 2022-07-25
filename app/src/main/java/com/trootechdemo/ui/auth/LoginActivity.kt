package com.kloeapp.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.firebase.iid.FirebaseInstanceId
import com.kloeapp.R
import com.kloeapp.databinding.ActivityLoginBinding
import com.kloeapp.model.request.SignInRequest
import com.kloeapp.model.request.SignUpRequest
import com.kloeapp.model.request.SocialSignInRequest
import com.kloeapp.restapi.WebFiled
import com.kloeapp.restapi.api.ApiCallback
import com.kloeapp.restapi.api.ApiServiceProvider
import com.kloeapp.ui.auth.viewmodel.AuthViewModel
import com.kloeapp.ui.common.BaseActivity
import com.kloeapp.ui.common.SetOnSocialLoginSuccessListener
import com.kloeapp.ui.common.SocialLoginModel
import com.kloeapp.ui.createaccount.CreateNameActivity
import com.kloeapp.ui.home.DashboardActivity
import com.kloeapp.ui.onboarding.OnBodingActivity
import com.kloeapp.utils.CommonMethods
import com.kloeapp.utils.Constant
import com.kloeapp.utils.KeyboardUtility
import com.kloeapp.utils.session_manager.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity(), SetOnSocialLoginSuccessListener {

    private val mainViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var apiServiceProvider: ApiServiceProvider

    private lateinit var binding: ActivityLoginBinding
    lateinit var mContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        mContext = this
        init()

        socialLoginModel = SocialLoginModel(this, this)
    }

    private fun init() {
        with(binding) {
            binding.tvSignUpTtl.text =
                Html.fromHtml(mContext.resources.getString(R.string.dont_account_signup))

            binding.rlHeader.ivBack.setOnClickListener {
                finish()
            }

            // binding.tvEmailTIL.error = "Please enter email address."
            binding.tvEmailTIL.isErrorEnabled = false

            binding.edtEmailTIL.addTextChangedListener(mTextWatcher)
            binding.edtPasswordTIL.addTextChangedListener(mTextWatcher)

            clearFocused()
            checkFieldsForEmptyValues()
        }
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i2: Int, i3: Int) {}
        override fun onTextChanged(charSequence: CharSequence?, i: Int, i2: Int, i3: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues()
        }
    }

    private fun checkFieldsForEmptyValues() {
        val sEmail: String = binding.edtEmailTIL.text.toString().trim()
        val sPsw: String = binding.edtPasswordTIL.text.toString().trim()

        if (sEmail == "" || sPsw == "") {
            binding.tvSignInBtnAL.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.button_black_bg_light))

            binding.tvSignInBtnAL.isClickable = false
        } else {
            binding.tvSignInBtnAL.isClickable = true

            binding.tvSignInBtnAL.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.button_black_bg))
        }
    }

    //Clicked
    fun forgotPwOpen(view: View) {
        startActivity(Intent(mContext, ForgotPasswordActivity::class.java))
    }

    fun signInCall(view: View) {
        if (isOnline()) {
            if (isValidation()) {
                callLoginApi()
            }
        }
    }

    fun signUpOpen(view: View) {
        startActivity(Intent(mContext, OnBodingActivity::class.java))
    }

    lateinit var socialLoginModel: SocialLoginModel
    fun googleSignInCall(view: View) {
        socialLoginModel.googleSignIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        socialLoginModel.onActivityResult(requestCode, resultCode, data)
    }

    override fun onLoginSuccess(
        firstName: String,
        lastName: String,
        email: String,
        facebookID: String,
        type: String,
        profileImage: String,
        googleId: String
    ) {
        Log.e("email----=--=>", "$email")
        Log.e("googleId----=--=>", "$googleId")

        Log.e("firstName----=--=>", "$firstName")
        Log.e("lastName----=--=>", "$lastName")
        if (isOnline())
            callSocialLoginApi(email, googleId, Constant.SOCIAL_GOOGLE_TYPE, firstName, lastName)
    }

    override fun onLoginError(error: String) {
        Toast.makeText(this, error ?: "Something went wrong", Toast.LENGTH_LONG).show()
    }

    override fun onRedirectToNextActivity() {

    }


    lateinit var strEmail: String
    lateinit var strPassword: String

    private fun isValidation(): Boolean {
        strEmail = binding.edtEmailTIL.text.toString().trim()
        strPassword = binding.edtPasswordTIL.text.toString().trim()

        if (strEmail.isEmpty()) {
            binding.edtEmailTIL.requestFocus()

            binding.tvEmailErrorAL.text = getString(R.string.email_empty)
            binding.tvEmailErrorAL.visibility = View.VISIBLE
            binding.tvPasswordErrorAL.visibility = View.GONE
            return false
        } else if (!CommonMethods.isEmailValid(strEmail)) {
            binding.edtEmailTIL.requestFocus()

            binding.tvEmailErrorAL.text = getString(R.string.valid_email_empty)
            binding.tvEmailErrorAL.visibility = View.VISIBLE
            binding.tvPasswordErrorAL.visibility = View.GONE

            return false
        } else if (strPassword.isEmpty()) {
            binding.edtPasswordTIL.requestFocus()

            binding.tvPasswordErrorAL.text = getString(R.string.password_empty)
            binding.tvPasswordErrorAL.visibility = View.VISIBLE
            binding.tvEmailErrorAL.visibility = View.GONE

            return false
        }/* else if (strPassword.length < 8) {
            binding.edtPasswordTIL.requestFocus()

            binding.tvPasswordErrorAL.text = getString(R.string.valid_password)
            binding.tvPasswordErrorAL.visibility=View.VISIBLE
            binding.tvEmailErrorAL.visibility=View.GONE

            return false
        }*/

        return true
    }

    private fun clearFocused() {
        KeyboardUtility.hideKeyboard(mContext, binding.edtEmailTIL)

        binding.edtEmailTIL.clearFocus()
        binding.edtPasswordTIL.clearFocus()

        binding.tvPasswordErrorAL.visibility = View.GONE
        binding.tvEmailErrorAL.visibility = View.GONE
    }


    //API call integration
    private fun callLoginApi() {
        progress.show()
        Log.e("FCM_TOKEN", myPrefs[WebFiled.PREF_FCM_TOKEN, ""])
        val signInRequest = SignInRequest(
            email = strEmail,
            password = strPassword,
            userType = myPrefs[WebFiled.PREF_USER_TYPE, "0"],
            firebase_token = FirebaseInstanceId.getInstance().token!!,
            deviceType = "0"
        )
        mainViewModel.fetchLoginResponse(signInRequest)
        fetchResponseData()

/*
         lifecycleScope.launch {
             apiServiceProvider.callPostSignInApi(signInRequest).collect {
                 when (it) {
                     is ApiCallback.OnSuccess<*> -> {
                         progress.hide()
                         if (it.data!!.status == 1) {
                             myPrefs[WebFiled.PREF_IS_LOGGED_IN] = true
                             myPrefs[WebFiled.PREF_TOKEN] = it.data.data.token

                             clearFocused()
                             SessionManager.setIsUserLoggedin(mContext, true)

                             val intent = Intent(applicationContext, DashboardActivity::class.java)
                             intent.putExtra(Constant.CLASS_NAME_PARAM, "Home")
                             intent.flags =
                                 Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                             startActivity(intent)
                             finish()

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
         }*/
    }

    private fun fetchResponseData() {
        mainViewModel.responseLoginMain.observe(this) { response ->
            when (response) {
                is ApiCallback.OnSuccess -> {
                    progress.hide()
                    if (response.data!!.status == 1) {
                        myPrefs[WebFiled.PREF_IS_LOGGED_IN] = true
                        myPrefs[WebFiled.PREF_TOKEN] = response.data.data.token

                        clearFocused()
                        SessionManager.setIsUserLoggedin(mContext, true)

                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        intent.putExtra(Constant.CLASS_NAME_PARAM, "Home")
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        showSnackBar(response.data.message, R.color.red)
                    }

                }

                is ApiCallback.OnError -> {
                    progress.hide()
                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ApiCallback.OnLoading -> {
                    progress.hide()
                }
            }
        }
    }

    private fun callSocialLoginApi(
        socialEmail: String,
        socialId: String,
        socialType: String,
        firstName: String,
        lastName: String
    ) {
        Log.e("FCM_TOKEN", myPrefs[WebFiled.PREF_FCM_TOKEN, ""])
        val socialSignInRequest = SocialSignInRequest(
            social_id = socialId,
            type = socialType,
            device_type = Constant.DEVICE_TYPE,
            email = socialEmail,
        )

        SessionManager.clearSignAppSession(mContext)
        progress.show()
        lifecycleScope.launch {
            apiServiceProvider.callSocialSignupApi(socialSignInRequest).collect {
                when (it) {
                    is ApiCallback.OnSuccess<*> -> {
                        progress.hide()
                        //first_time=1/0 means firstTimeSignup/secondTimeSignup

                        with(it.data!!) {
                            if (status == 1) {
                                with(data) {
                                    if (first_time == 0) {
                                        myPrefs[WebFiled.PREF_IS_LOGGED_IN] = true
                                        myPrefs[WebFiled.PREF_TOKEN] = it.data.data.token

                                        clearFocused()
                                        SessionManager.setIsUserLoggedin(mContext, true)

                                        val intent =
                                            Intent(
                                                applicationContext,
                                                DashboardActivity::class.java
                                            )
                                        intent.putExtra(Constant.CLASS_NAME_PARAM, "Home")
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        //
                                        var userDetails = SignUpRequest()
                                        userDetails.apply {
                                            this.firstName = firstName
                                            this.lastName = lastName
                                            this.email = socialEmail
                                        }
                                        SessionManager.setSignUpUserDetails(mContext, userDetails!!)

                                        val intent = Intent(
                                            applicationContext,
                                            CreateNameActivity::class.java
                                        )
                                        intent.putExtra(Constant.CLASS_NAME_PARAM, "create")
                                        startActivity(intent)
                                    }
                                }
                            } else {
                                showSnackBar(it.data.message, R.color.red)
                            }
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
}