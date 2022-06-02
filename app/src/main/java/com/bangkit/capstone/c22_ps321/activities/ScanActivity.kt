package com.bangkit.capstone.c22_ps321.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.c22_ps321.R
import com.bangkit.capstone.c22_ps321.databinding.ActivityScanBinding
import com.bangkit.capstone.c22_ps321.helper.uriToFile
import com.bangkit.capstone.c22_ps321.ml.PlantDeseaseConvertedModelNew
import com.bangkit.capstone.c22_ps321.user.UserPreferences
import com.bangkit.capstone.c22_ps321.viewmodels.ScanningViewModel
import com.bangkit.capstone.c22_ps321.viewmodels.ViewModelFactory
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ScanActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityScanBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: ScanningViewModel
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private val imageSize = 224
    private lateinit var labels: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[ScanningViewModel::class.java]

        if (!allPermissionsGranted()) {
            requestPermissions()
        }

        isEnabledButton(false)
        Log.d("GetFileInit", getFile.toString())
        binding.btnSearch.isEnabled = false
        initSpinnerPlants()

        viewModel.getFile.observe(this) {
            binding.btnSearch.isEnabled = it != null
        }

        binding.apply {
            btnCamera.setOnClickListener {
                takePhoto()
            }
            btnGallery.setOnClickListener {
                startGallery()
            }
            spinnerPlants.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    if (position == 0) {
                        isEnabledButton(false)
                    } else {
                        isEnabledButton(true)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    isEnabledButton(false)
                }

            }

        }

        labels =
            application.assets.open("capstone_labels.txt").bufferedReader().use { it.readText() }
                .split("\n")
        Log.d("Labels", "Size: ${labels.size}, $labels")
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
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
            viewModel.isFileAvailable(getFile)
            Log.d("GetFile", getFile.toString())

            val imgBitmap = BitmapFactory.decodeFile(myFile?.path)
            binding.previewImageView.setImageBitmap(imgBitmap)

            classifyImage(imgBitmap)
        }
    }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val myFile = File(currentPhotoPath)
                getFile = myFile
                val result = BitmapFactory.decodeFile(myFile.path)
                val rotateBitmap = rotateBitmap(result)

                viewModel.isFileAvailable(getFile)
                Log.d("GetFile", getFile.toString())

                binding.previewImageView.setImageBitmap(rotateBitmap)

                classifyImage(rotateBitmap)
            }
        }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.choose_picture))
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

    private fun initSpinnerPlants() {
        val list = resources.getStringArray(R.array.list_plants).toList()
        val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            list
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView
                view.setTypeface(view.typeface, Typeface.BOLD)

                if (position == binding.spinnerPlants.selectedItemPosition && position != 0) {
                    view.background = ColorDrawable(Color.parseColor("#F7E7CE"))
                    view.setTextColor(Color.parseColor("#333399"))
                }

                if (position == 0) {
                    view.setTextColor(Color.LTGRAY)
                }

                return view
            }

            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }

        binding.spinnerPlants.adapter = adapter
    }

    private fun rotateBitmap(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90F)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }

    private fun classifyImage(bitmap: Bitmap) {
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

    private fun isEnabledButton(isEnabled: Boolean) {
        when (isEnabled) {
            true -> {
                binding.btnCamera.isEnabled = true
                binding.btnGallery.isEnabled = true
            }
            false -> {
                binding.btnCamera.isEnabled = false
                binding.btnGallery.isEnabled = false
            }
        }
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