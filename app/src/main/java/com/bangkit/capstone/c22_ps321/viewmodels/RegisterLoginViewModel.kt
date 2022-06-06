package com.bangkit.capstone.c22_ps321.viewmodels

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.bangkit.capstone.c22_ps321.activities.LoginActivity
import com.bangkit.capstone.c22_ps321.helper.Event
import com.bangkit.capstone.c22_ps321.user.User
import com.bangkit.capstone.c22_ps321.user.UserPreferences
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RegisterLoginViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _addResponse = MutableLiveData<FirebaseUser?>()
    val addResponse: LiveData<FirebaseUser?> = _addResponse

    fun createUserWithEmailPassword(email: String, password: String) {
        _isLoading.value = true
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                _isLoading.value = false
                if (it.isSuccessful) {
                    _addResponse.value = auth.currentUser
                } else {
                    _addResponse.value = null
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _isMessage.value = Event(it.message)
                _addResponse.value = null
            }
    }

    fun firebaseAuthWithGoogle(activity: Activity, idToken: String) {
        _isLoading.value = true
        val auth = Firebase.auth
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    Log.d("AuthWithGoogle", "signInWithCredential:success")
                    _addResponse.value = auth.currentUser
                } else {
                    Log.e("AuthWithGoogle", "signInWithCredential:failure", task.exception)
                    _isMessage.value = Event("Authenticate Failed")
                    _addResponse.value = null
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _isMessage.value = Event(e.message)
                Log.e("AuthWithGoogle", "signInWithCredential:failure", e)
            }
    }

    fun signUsingEmailPassword(activity: Activity, email: String, password: String) {
        _isLoading.value = true
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    Log.d("AuthUsingEmailPassword", "signInWithEmail:success")
                    _addResponse.value = auth.currentUser
                } else {
                    Log.e("AuthUsingEmailPassword", "signInWithEmail:failure", task.exception)
                    _addResponse.value = null
                    _isMessage.value = Event("Authenticate Failed")
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _isMessage.value = Event(e.message)
                Log.e("AuthUsingEmailPassword", "signInWithEmail:failure", e)
            }

    }
}