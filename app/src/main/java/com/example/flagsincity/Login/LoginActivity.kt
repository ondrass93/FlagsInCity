package com.example.flagsincity.Login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import com.example.flagsincity.MainActivity
import com.example.flagsincity.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


const val LOGIN_TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private lateinit var singInButton: com.google.android.gms.common.SignInButton
    private lateinit var continueToGameButton: Button
    private lateinit var userNameLogin: TextView
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)




        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]

        userNameLogin = findViewById(R.id.userName)
        singInButton = findViewById(R.id.sign_in_button_google)
        singInButton.setOnClickListener {
            signIn()
        }

        continueToGameButton = findViewById(R.id.continueToGameButton)
        continueToGameButton.visibility = INVISIBLE
        continueToGameButton.setOnClickListener {
            continueToGame()
        }


        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly...
        val currentUser = auth.currentUser

        if (currentUser != null) {
            updateUI(currentUser)
        }



    }
    // [END on_start_check_user]

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                    saveName(user) // save name to Shared preferences
                    Log.d("userNameSharedPref", "Saving user name")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }
    // [END signin]

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            userNameLogin.text = user.email
            continueToGameButton.visibility = VISIBLE
        } else {
            userNameLogin.text = getString(R.string.Sign_in_problem)
        }
    }

    private fun continueToGame() {

        var userName = userNameLogin.text.toString()
/*        userName = userName.replace(".","_").replace("@","_")
        Log.d("MainActivityIntent", userName)*/


        val intent = Intent(this, MainActivity::class.java)
/*        intent.putExtra("userName", userName)*/
        startActivity(intent)
    }

    private fun saveName(user: FirebaseUser?) {

        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        var userName = user?.email.toString()
        userName = userName.replace(".","_").replace("@","_")



        editor.apply {
            putString("userName", userName)
            apply()

        }
        Log.d("userNameSharedPref", userName)
    }


    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

}