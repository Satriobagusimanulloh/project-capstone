package com.bangkit.capstone.c22_ps321.activities

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.c22_ps321.databinding.ActivityRegisterBinding
import com.bangkit.capstone.c22_ps321.responses.RegisterResponse
import com.bangkit.capstone.c22_ps321.retrofit.ApiConfig
import com.bangkit.capstone.c22_ps321.user.User
import com.bangkit.capstone.c22_ps321.user.UserPreferences
import com.bangkit.capstone.c22_ps321.viewmodels.RegisterViewModel
import com.bangkit.capstone.c22_ps321.viewmodels.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    companion object {
        private const val TAG = "RegisterActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
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
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[RegisterViewModel::class.java]
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

        val service = ApiConfig.getApiService().register(email, password)
        service.enqueue(object: Callback<RegisterResponse> {

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ){
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    Log.e(TAG, "onSuccess: ${response.message()}")
                    if (responseBody.message != "Email not available"){
                        registerViewModel.saveUser(User(email, password, false,""))
                        AlertDialog.Builder(this@RegisterActivity).apply {
                            setTitle("Register")
                            setMessage("Register success!")
                            setPositiveButton("YEP") { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                } else {
                    if(responseBody?.message == "Email not available")
                        registerViewModel.saveUser(User(email, password, false,""))
                    AlertDialog.Builder(this@RegisterActivity).apply {
                        setTitle("Register")
                        setMessage("Whoops, somebody already has your email, \nPlease create new email")
                        setPositiveButton("YEP") { _, _ ->
                        }
                        create()
                        show()
                    }
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable){
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}