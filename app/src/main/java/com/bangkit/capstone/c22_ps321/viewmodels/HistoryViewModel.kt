package com.bangkit.capstone.c22_ps321.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.c22_ps321.helper.Event
import com.bangkit.capstone.c22_ps321.models.HistoryModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _responseData = MutableLiveData<ArrayList<HistoryModels?>>()
    val responseData: LiveData<ArrayList<HistoryModels?>> = _responseData

    fun getAllData() {
        _isLoading.value = true
        val uId = Firebase.auth.currentUser?.uid.toString()
        val database = Firebase.database
        val databaseRef = database.getReference("users")

        databaseRef.child(uId).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _isLoading.value = false
                Log.d("HistoryViewModel", snapshot.value.toString())
                val list = ArrayList<HistoryModels?>()
                for (postSnapshot in snapshot.children) {
                    val models = HistoryModels(
                        postSnapshot.child("name").value.toString(),
                        postSnapshot.child("disease").value.toString(),
                        postSnapshot.child("description").value.toString(),
                        postSnapshot.child("treatment").value.toString(),
                        postSnapshot.child("photoUrl").value.toString()
                    )
                    list.add(models)
                }
                _responseData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
                _responseData.value = null
                _isMessage.value = Event(error.message)
                Log.e("HistoryViewModel", "Failed to read data", error.toException())
            }
        })
    }
}