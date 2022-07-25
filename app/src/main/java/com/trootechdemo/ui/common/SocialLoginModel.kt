package com.kloeapp.ui.common

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kloeapp.R
import org.json.JSONException
import java.util.*

class SocialLoginModel(
    var context: ComponentActivity,
    var setOnSocialLoginSuccessListener: SetOnSocialLoginSuccessListener
) {

    // callbackmanager for facebook
 //   private var callbackManager: CallbackManager? = null

    // google sign in client
    private lateinit var googleSignInClient: GoogleSignInClient

    // request code for google signin used in startActivityForResult
    private val requestOneTap = 2

    private lateinit var auth: FirebaseAuth

    init {
        setUpGoogle() // initialize google setup
    }

    private fun setUpGoogle() {
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }



    /*thid method is used to open google sign-in dialog*/
    fun googleSignIn() {
        Firebase.auth.signOut()
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        context.startActivityForResult(signInIntent, requestOneTap)
    }



    /*if request code = requestOneTap then restrive data from google and
    default method for facebook callbackmanager is use*/

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      //  callbackManager?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestOneTap) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.e("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("TAG", "Google sign in failed", e)
                setOnSocialLoginSuccessListener.onLoginError("Something went wrong")
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.e("ddddd : ", "in else if")
            setOnSocialLoginSuccessListener.onLoginError("Something went wrong")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    getDataFromGoogleSignIN(user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("TAG", "signInWithCredential:failure", task.exception)
                    Firebase.auth.signOut()
                    setOnSocialLoginSuccessListener.onLoginError("signInWithCredential:failure")
                }
            }
    }

    /* retrive data from google sign-in and passed to activity/fragment via interface */
    private fun getDataFromGoogleSignIN(user: FirebaseUser) {
        googleSignInClient.revokeAccess().addOnCompleteListener(context) {
            Log.e("revoke : ", "access revoked")
        }
        Firebase.auth.signOut()
        val socialId = user.uid ?: ""
        val fullName = "${user.displayName}"
        var lastName = ""
        var firstName = ""
        if (fullName.contains(" ")) {
            val splitted = fullName.split("\\s+".toRegex())
            firstName = splitted[0]
            lastName = splitted[1]
        } else {
            firstName = fullName
        }

        var profilepic = ""
        if (!user.photoUrl.toString().isEmpty()) {
            profilepic = user.photoUrl.toString()
        }
        val email = user.email ?: ""
        Log.e("data path : ", "${socialId} ${fullName} and ${profilepic} ${email}")

        if (email.isEmpty()) {
            setOnSocialLoginSuccessListener.onLoginError("Email not found. email is required to complete signup process")
            return
        }

        if (socialId.isNullOrEmpty()) {
            setOnSocialLoginSuccessListener.onLoginError("Google sign-in failed. Please try with diffrent account")
            return
        }

        setOnSocialLoginSuccessListener.onLoginSuccess(
            firstName,
            lastName,
            email,
            "",
            "google",
            profilepic,
            socialId
        )
    }

}


interface SetOnSocialLoginSuccessListener {
    fun onLoginSuccess(
        firstName: String,
        lastName: String,
        email: String,
        facebookID: String,
        type: String,
        profileImage: String,
        googleId: String
    )

    fun onLoginError(error: String)

    fun onRedirectToNextActivity()
}