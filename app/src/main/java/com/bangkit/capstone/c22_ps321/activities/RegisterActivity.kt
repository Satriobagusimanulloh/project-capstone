package com.bangkit.capstone.c22_ps321.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.c22_ps321.R
import com.bangkit.capstone.c22_ps321.databinding.ActivityRegisterBinding
import com.bangkit.capstone.c22_ps321.user.UserPreferences
import com.bangkit.capstone.c22_ps321.viewmodels.RegisterLoginViewModel
import com.bangkit.capstone.c22_ps321.viewmodels.ViewModelFactory
import com.google.firebase.auth.FirebaseUser
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private lateinit var registerLoginViewModel: RegisterLoginViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()

        registerLoginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        registerLoginViewModel.isMessage.observe(this) {
            showToast(it.getContentIfNotHandled())
        }
        registerLoginViewModel.addResponse.observe(this) {
            if (it != null) {
                updateUI(it)
            } else {
                updateUI(null)
            }
        }

        setupAction()
        setButtonRegisterEnabled()
        initEditText()
        moveToLogin()
    }
    
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        registerLoginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[RegisterLoginViewModel::class.java]
    }
    
    private fun setupAction() {
        binding.btnRegister.setOnClickListener { 
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edtEmail.error = "Enter your email"
                }
                password.isEmpty() -> {
                    binding.edtPassword.error = "Enter your password"
                }
                password.length < 6 -> {
                    binding.edtPassword.error = "Minimum character is 6"
                }
                else -> {
                    registerData(email, password)
                }
            }
        }
    }

    private fun registerData(email: String, password: String){
        registerLoginViewModel.createUserWithEmailPassword(email, password)
    }

    private fun moveToLogin() {
        val spannableString = SpannableString(resources.getString(R.string.already_have_an_account))
        val register: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@RegisterActivity,
                        androidx.core.util.Pair(binding.tvRegister, "tv_login"),
                        androidx.core.util.Pair(binding.borderEmail, "email"),
                        androidx.core.util.Pair(binding.borderPassword, "password"),
                        androidx.core.util.Pair(binding.btnRegister, "btn_login"),
                    )
                startActivity(intent, optionsCompat.toBundle())
            }
        }
        Log.d("Language", Locale.getDefault().language)
        if (Locale.getDefault().language == "in"){
            spannableString.setSpan(register, 18, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvHaveAccount.apply {
                text = spannableString
                movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            spannableString.setSpan(register, 33, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvHaveAccount.apply {
                text = spannableString
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun initEditText() {
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.contains("@") == false) {
                    binding.edtEmail.error = resources.getString(R.string.error_email)
                }
                setButtonRegisterEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length != null) {
                    if (p0.length < 6) {
                        binding.edtPassword.error = resources.getString(R.string.error_pass)
                    }
                }
                setButtonRegisterEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun setButtonRegisterEnabled() {
        val emailRes = binding.edtEmail.text
        val passRes = binding.edtPassword.text

        binding.btnRegister.isEnabled =
            emailRes != null && passRes != null && emailRes.contains("@") && passRes.length >= 6
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }

    private fun showToast(message: String?) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }
}