package com.bangkit.capstone.c22_ps321.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bangkit.capstone.c22_ps321.databinding.ActivityScanBinding
import com.bangkit.capstone.c22_ps321.helper.uriToFile
import com.bangkit.capstone.c22_ps321.ml.PlantDeseaseConvertedModelNew
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File

class ScanActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityScanBinding.inflate(layoutInflater)
    }
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private val imageSize = 224
    private lateinit var labels: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            btnCamera.setOnClickListener { takePhoto() }
            btnGallery.setOnClickListener { startGallery() }
        }

        labels = application.assets.open("capstone_labels.txt").bufferedReader().use { it.readText() }.split("\n")
        Log.d("Labels", "Size: ${labels.size}, $labels")
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        this.let { base ->
            ContextCompat.checkSelfPermission(
                base.baseContext,
                it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = applicationContext?.let { uriToFile(selectedImg, it) }
            getFile = myFile
            val imgBitmap = BitmapFactory.decodeFile(myFile?.path)
            binding.previewImageView.setImageBitmap(imgBitmap)

            classifyImage(imgBitmap)
//            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val myFile = File(currentPhotoPath)
                getFile = myFile
                val result = BitmapFactory.decodeFile(myFile.path)
                val rotateBitmap = rotateBitmap(result)

                binding.previewImageView.setImageBitmap(rotateBitmap)

                classifyImage(rotateBitmap)
            }
        }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose Picture")
        launcherIntentGallery.launch(chooser)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(this.packageManager)

        com.bangkit.capstone.c22_ps321.helper.createTempFile(this.application).also { file ->
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.bangkit.capstone.c22_ps321",
                file
            )
            currentPhotoPath = file.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun rotateBitmap(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90F)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }

    private fun classifyImage(bitmap: Bitmap){
        val resized = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
        val model = PlantDeseaseConvertedModelNew.newInstance(this)

        val tbuffer = TensorImage.fromBitmap(resized)
        val byteBuffer = tbuffer.buffer

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val max = getMax(outputFeature0.floatArray)
        Log.d("IndexPrediction", max.toString())

        binding.tvOutput.text = labels[max]

        model.close()
    }

    private fun getMax(arr:FloatArray) : Int{
        var ind = 0
        var min = 0.0f

        for(i in 0..37)
        {
            if(arr[i] > min)
            {
                min = arr[i]
                ind = i
            }
        }
        return ind
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}