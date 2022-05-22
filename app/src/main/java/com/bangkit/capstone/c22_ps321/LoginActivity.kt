package com.bangkit.capstone.c22_ps321

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.bangkit.capstone.c22_ps321.databinding.ActivityLoginBinding
import java.util.*

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        moveRegister()

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        loginValidation()
    }

    private fun loginValidation() {
        val username = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        when {
            username.isEmpty() -> binding.edtUsername.error = "Enter valid username"
            password.isEmpty() -> binding.edtPassword.error = "Enter valid password"
            else -> {
                //Login Process
            }
        }
    }

    private fun moveRegister() {
        val spannableString = SpannableString(resources.getString(R.string.don_t_have_an_account))
        val register: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@LoginActivity,
//                        androidx.core.util.Pair(binding.icStoryApp, "icon"),
//                        androidx.core.util.Pair(binding.tvWelcome, "welcome"),
//                        androidx.core.util.Pair(binding.outlinedEmail, "email"),
                    )
                startActivity(intent, optionsCompat.toBundle())
            }
        }
        if (Locale.getDefault().language == "in"){
            spannableString.setSpan(register, 18, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvNotHavAccount.apply {
                text = spannableString
                movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            spannableString.setSpan(register, 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvNotHavAccount.apply {
                text = spannableString
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }
}