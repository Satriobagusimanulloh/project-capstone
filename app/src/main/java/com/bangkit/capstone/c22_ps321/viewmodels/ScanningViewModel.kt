package com.bangkit.capstone.c22_ps321.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.c22_ps321.database.entity.ClassifyEntity
import com.bangkit.capstone.c22_ps321.database.repository.ClassifyRepository
import com.bangkit.capstone.c22_ps321.helper.Event
import com.bangkit.capstone.c22_ps321.helper.timeStampForServer
import com.bangkit.capstone.c22_ps321.ml.PlantDeseaseConvertedModelNew
import com.bangkit.capstone.c22_ps321.models.HistoryModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileInputStream

class ScanningViewModel(private val repository: ClassifyRepository) : ViewModel() {
    private val _setFile = MutableLiveData<File?>()
    val getFile: LiveData<File?> = _setFile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _getMaxIndex = MutableLiveData<Int>()
    val getMaxIndex: LiveData<Int> = _getMaxIndex

    private val _addResponse = MutableLiveData<String?>()
    val addResponse: LiveData<String?> = _addResponse

    fun isFileAvailable(idFile: File?) {
        _setFile.value = idFile
        Log.d("ScanViewModel", idFile.toString())
    }

    fun getAllData() = repository.allData

    fun insertData(classifyEntity: List<ClassifyEntity>) = repository.insertData(classifyEntity)

    fun getDataByDisease(diseaseIndex: Int) = repository.getDataByDisease(diseaseIndex)

    fun classifyImage(context: Context, bitmap: Bitmap, imageSize: Int) {
        val resized = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
        val model = PlantDeseaseConvertedModelNew.newInstance(context)

        val tbuffer = TensorImage.fromBitmap(resized)
        val byteBuffer = tbuffer.buffer

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        _getMaxIndex.value = getMax(outputFeature0.floatArray)
        Log.d("IndexPrediction", _getMaxIndex.toString())

        model.close()
    }

    private fun getMax(arr: FloatArray): Int {
        var ind = 0
        var min = 0.0f

        for (i in 0..37) {
            if (arr[i] > min) {
                min = arr[i]
                ind = i
            }
        }
        return ind
    }

    fun uploadData(file: File?, classify: String, name: String, description: String, treatment: String) {
        _isLoading.value = true
        val uId = Firebase.auth.currentUser?.uid.toString()
        val fileName = "$timeStampForServer.jpg"
        Log.d("UserIDWhenUpload", "$uId $classify")
        val storageRef = Firebase.storage.reference.child("users/${uId}/${fileName}")
        val stream = FileInputStream(file)
        val database = Firebase.database
        val databaseRef = database.getReference("users").child(uId)

        val uploadTask = storageRef.putStream(stream)
        uploadTask.addOnCompleteListener {
            if (it.isSuccessful) {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("UrlImg", uri.toString())
                    val newData = HistoryModels(
                        name,
                        classify,
                        description,
                        treatment,
                        uri.toString()
                    )
                    val newReference = databaseRef.push()
                    val newKey = newReference.key
                    newReference.setValue(newData)
                        .addOnSuccessListener { response ->
                            _isLoading.value = false
//                            _addResponse.value = newKey
                            _addResponse.value = uri.toString()
                            Log.d("TAG", "DocumentSnapshot added with ID: $newKey")
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            _addResponse.value = null
                            Log.w("TAG", "Error adding document", e)
                            _isMessage.value = Event(e.toString())
                        }
                }
            }
        }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _addResponse.value = null
                Log.w("TAG", "Error adding document", e)
                _isMessage.value = Event(e.toString())
            }
    }
}