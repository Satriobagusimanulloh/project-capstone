package com.bangkit.capstone.c22_ps321.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bangkit.capstone.c22_ps321.R
import com.bangkit.capstone.c22_ps321.databinding.ActivityPhoneLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class PhoneLoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPhoneLoginBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnOtp.setOnClickListener {
            val phoneNumber = binding.edtPhoneNumber.text
            if (phoneNumber.isEmpty()) {
                binding.edtPhoneNumber.error = "Please insert the phone number"
            } else {
                val validNumber = "+62${binding.edtPhoneNumber.text}"
                sendOtp(validNumber)
            }
        }

    }

    private fun sendOtp(phoneNumber: String) {
        showLoading(true)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    showLoading(false)
                    countDown()
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d(TAG, "onVerificationCompleted:$credential")
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w(TAG, "onVerificationFailed", e)
                    showLoading(false)
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Log.e(TAG, e.message.toString())
                        showToast(e.message.toString())
                    } else if (e is FirebaseTooManyRequestsException) {
                        Log.e(TAG, e.message.toString())
                        showToast(e.message.toString())
                    }
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        showLoading(true)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    showToast(resources.getString(R.string.verification_login_success))
                    val user = task.result?.user
                    updateUI(user)

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.e(TAG, task.exception.toString())
                        showToast(task.exception.toString())
                    }
                }
            }
            .addOnFailureListener { e ->
                showLoading(false)
                showToast(e.message.toString())
                Log.e(TAG, e.message.toString())
            }
    }

    private fun countDown() {
        val timer = object: CountDownTimer(60000, 1000){
            override fun onTick(p0: Long) {
                val remaining = "Remaining: ${p0/1000} Seconds"
                binding.tvTimer.apply {
                    visibility = View.VISIBLE
                    text = remaining
                }
                binding.btnOtp.isEnabled = false
            }

            override fun onFinish() {
                binding.btnOtp.isEnabled = true
                binding.tvTimer.visibility = View.GONE
            }
        }
        timer.start()
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@PhoneLoginActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@PhoneLoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoginPhone.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnOtp.isEnabled = !isLoading
    }

    companion object {
        const val TAG = "PhoneLoginMethode"
    }
}