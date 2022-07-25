package com.kloeapp.ui.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.kloeapp.R
import com.kloeapp.databinding.ActivityForgotPasswordBinding
import com.kloeapp.ui.common.BaseActivity
import com.kloeapp.ui.createaccount.CreatePasswordActivity
import com.kloeapp.utils.CommonMethods
import com.kloeapp.utils.KeyboardUtility

class ForgotPasswordActivity : BaseActivity() {


    lateinit var mContext: Context
    lateinit var binding:ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext=this

        init()

        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun init(){
        with(binding){
            binding.rlHeader.ivBack.setOnClickListener {
                finish()
            }
            binding.edtEmailAFP.requestFocus()
            //Validation Managed
            binding.edtEmailAFP.addTextChangedListener(mTextWatcher)

            // clearFocused()
            checkFieldsForEmptyValues()
        }
    }

    fun nextForgorPwCall(view:View){
        if (isValidation()) {
            clearFocused()
            startActivity(Intent(mContext, LoginActivity::class.java))
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
        val sEmail: String = binding.edtEmailAFP.text.toString().trim()

        if (sEmail == "") {
            binding.tvForgotNextAFP.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.button_pink_bg))

            binding.tvForgotNextAFP.isClickable = false
        } else {
            binding.tvForgotNextAFP.isClickable = true

            binding.tvForgotNextAFP.setBackgroundDrawable(mContext.resources.getDrawable(R.drawable.button_pink_bg_light))
        }
    }

    lateinit var strEmail: String

    private fun isValidation(): Boolean {
        strEmail = binding.edtEmailAFP.text.toString().trim()

        if (strEmail.isEmpty()) {
            binding.edtEmailAFP.requestFocus()

            binding.tvEmailErrorAFP.text = getString(R.string.email_empty)
            binding.tvEmailErrorAFP.visibility = View.VISIBLE
            return false
        }  else if (!CommonMethods.isEmailValid(strEmail)) {
            binding.edtEmailAFP.requestFocus()

            binding.tvEmailErrorAFP.text = getString(R.string.valid_email_empty)
            binding.tvEmailErrorAFP.visibility = View.VISIBLE

            return false
        }

        return true
    }

    private fun clearFocused() {
        KeyboardUtility.hideKeyboard(mContext, binding.edtEmailAFP)

        binding.edtEmailAFP.clearFocus()

        binding.tvEmailErrorAFP.visibility = View.GONE
    }
}