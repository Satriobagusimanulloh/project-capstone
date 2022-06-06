package com.bangkit.capstone.c22_ps321.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bangkit.capstone.c22_ps321.databinding.ActivityPhoneLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class PhoneLoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPhoneLoginBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        auth = FirebaseAuth.getInstance()
//
//        // Callback function for Phone Auth
//        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            // This method is called when the verification is completed
//            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                startActivity(Intent(applicationContext, MainActivity::class.java))
//                finish()
//                Log.d("GFG" , "onVerificationCompleted Success")
//            }
//
//            // Called when verification is failed add log statement to see the exception
//            override fun onVerificationFailed(e: FirebaseException) {
//                Log.d("GFG" , "onVerificationFailed  $e")
//            }
//
//            // On code is sent by the firebase this method is called
//            // in here we start a new activity where user can enter the OTP
//            override fun onCodeSent(
//                verificationId: String,
//                token: PhoneAuthProvider.ForceResendingToken
//            ) {
//                Log.d("GFG","onCodeSent: $verificationId")
//                storedVerificationId = verificationId
//                resendToken = token
//
//                // Start a new activity using intent
//                // also send the storedVerificationId using intent
//                // we will use this id to send the otp back to firebase
//                val intent = Intent(applicationContext,OtpActivity::class.java)
//                intent.putExtra("storedVerificationId",storedVerificationId)
//                startActivity(intent)
//                finish()
//            }
//        }
    }

//    private fun login() {
//        number = findViewById<EditText>(R.id.et_phone_number).text.trim().toString()
//        // get the phone number from edit text and append the country cde with it
//        if (number.isNotEmpty()){
//            number = "+91$number"
//            sendVerificationCode(number)
//        }else{
//            Toast.makeText(this,"Enter mobile number", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // this method sends the verification code
//    // and starts the callback of verification
//    // which is implemented above in onCreate
//    private fun sendVerificationCode(number: String) {
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(number) // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(this) // Activity (for callback binding)
//            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//        Log.d("GFG" , "Auth started")
//    }
}