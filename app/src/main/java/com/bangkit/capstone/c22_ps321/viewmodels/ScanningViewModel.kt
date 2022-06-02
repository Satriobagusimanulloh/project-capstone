package com.bangkit.capstone.c22_ps321.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class ScanningViewModel: ViewModel() {
    private val _setFile = MutableLiveData<File?>()
    val getFile: LiveData<File?> = _setFile

    fun isFileAvailable(idFile: File?) {
        _setFile.value = idFile
        Log.d("ScanViewModel", idFile.toString())
    }
}