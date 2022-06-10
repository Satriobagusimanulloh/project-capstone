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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.c22_ps321.R
import com.bangkit.capstone.c22_ps321.database.entity.ClassifyEntity
import com.bangkit.capstone.c22_ps321.databinding.ActivityScanBinding
import com.bangkit.capstone.c22_ps321.helper.uriToFile
import com.bangkit.capstone.c22_ps321.viewmodels.ScanningViewModel
import com.bangkit.capstone.c22_ps321.viewmodels.ScanningViewModelFactory
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

        labels =
            application.assets.open("capstone_labels.txt").bufferedReader().use { it.readText() }
                .split("\n")
        Log.d("Labels", "Size: ${labels.size}, $labels")

        if (!allPermissionsGranted()) {
            requestPermissions()
        }

        viewModel = obtainViewModel()

        isEnabledButton(false)
        Log.d("NilaiGetFIle", getFile.toString())
        binding.btnSearch.isEnabled = false
        initSpinnerPlants()

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isMessage.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }

        viewModel.getFile.observe(this) {
            binding.btnSearch.isEnabled = it != null
        }

        viewModel.getMaxIndex.observe(this) { index ->
//            binding.tvOutput.text = labels[index]

            viewModel.getDataByDisease(index+1).observe(this@ScanActivity) { data ->
//                Toast.makeText(this@ScanActivity, data.disease, Toast.LENGTH_LONG).show()
                binding.btnSearch.setOnClickListener {
                    val name = binding.spinnerPlants.selectedItem.toString()
                    Log.d("BeforeUpload", "File: ${getFile.toString()}, Name: $name, Disease: ${labels[index]}")
                    data.description?.let { desc ->
                        data.treatment?.let { treat ->
                            viewModel.uploadData(getFile, labels[index], name,
                                desc, treat
                            )
                        }
                    }
                    Log.d("NilaiGetFIle", getFile.toString())
                    viewModel.addResponse.observe(this@ScanActivity) { result ->
                        if (result != null) {
                            val intent = Intent(this@ScanActivity, ResultActivity::class.java)
                            intent.putExtra(ResultActivity.EXTRA_KEY, result)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
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
//            btnInserData.setOnClickListener {
//                insertSampleData()
//            }
//            btnShowData.setOnClickListener {
//                viewModel.getDataByDisease(1).observe(this@ScanActivity) {
//                    Toast.makeText(this@ScanActivity, it.treatment, Toast.LENGTH_LONG).show()
//                }
//            }
        }
    }

    private fun obtainViewModel(): ScanningViewModel {
        val factory = ScanningViewModelFactory.getInstance(application)
        return ViewModelProvider(this, factory)[ScanningViewModel::class.java]
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

            viewModel.classifyImage(this, imgBitmap, imageSize)
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

                viewModel.classifyImage(this, rotateBitmap, imageSize)
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSearch.isEnabled = !isLoading
        binding.btnCamera.isEnabled = !isLoading
        binding.btnGallery.isEnabled = !isLoading
    }

    private fun insertSampleData() {
        val data1 = ClassifyEntity(
            1,
            "Apple___Apple_scab",
            "",
            ""
        )
        val data2 = ClassifyEntity(
            1,
            "Apple___Apple_scab",
            "",
            ""
        )
        val data3 = ClassifyEntity(
            1,
            "Apple___Apple_scab",
            "",
            ""
        )
        val listData = listOf(data1, data2, data3)
        viewModel.insertData(listData)
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