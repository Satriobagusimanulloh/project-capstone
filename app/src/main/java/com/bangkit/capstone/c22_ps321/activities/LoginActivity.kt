package com.bangkit.capstone.c22_ps321.activities

import android.app.Activity
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.c22_ps321.R
import com.bangkit.capstone.c22_ps321.databinding.ActivityLoginBinding
import com.bangkit.capstone.c22_ps321.user.UserPreferences
import com.bangkit.capstone.c22_ps321.viewmodels.RegisterLoginViewModel
import com.bangkit.capstone.c22_ps321.viewmodels.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var loginViewModel: RegisterLoginViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()

        auth = Firebase.auth

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        loginViewModel.isMessage.observe(this) {
            showToast(it.getContentIfNotHandled())
        }
        loginViewModel.addResponse.observe(this) {
            if (it != null) {
                updateUI(it)
            } else {
                updateUI(null)
            }
        }
        setupAction()

        setButtonLoginEnabled()
        initEditText()
        moveToRegister()

        // Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLoginGoogle.setOnClickListener { view: View? ->
            loginWithGoogleAccount()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun setButtonLoginEnabled() {
        val emailRes = binding.edtEmail.text
        val passRes = binding.edtPassword.text

        binding.btnLogin.isEnabled =
            emailRes != null && passRes != null && emailRes.contains("@") && passRes.length >= 6
    }

    private fun loginWithGoogleAccount() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun initEditText() {
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.contains("@") == false) {
                    binding.edtEmail.error = resources.getString(R.string.error_email)
                }
                setButtonLoginEnabled()
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
                setButtonLoginEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, e.message.toString())
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
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
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[RegisterLoginViewModel::class.java]
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edtEmail.error = "Enter your email"
                }
                password.isEmpty() -> {
                    binding.edtPassword.error = "Enter your password"
                }
                else -> {
                    loginViewModel.signUsingEmailPassword(this, email, password)
                }
            }
        }
    }

    private fun moveToRegister() {
        val spannableString = SpannableString(resources.getString(R.string.don_t_have_an_account))
        val register: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@LoginActivity,
                        androidx.core.util.Pair(binding.tvLogin, "tv_login"),
                        androidx.core.util.Pair(binding.borderEmail, "email"),
                        androidx.core.util.Pair(binding.borderPassword, "password"),
                        androidx.core.util.Pair(binding.btnLogin, "btn_login"),
                    )
                startActivity(intent, optionsCompat.toBundle())
            }
        }
        Log.d("Language", Locale.getDefault().language)
        if (Locale.getDefault().language == "in") {
            spannableString.setSpan(register, 18, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvNoAccount.apply {
                text = spannableString
                movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            spannableString.setSpan(register, 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvNoAccount.apply {
                text = spannableString
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun showToast(message: String?) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}