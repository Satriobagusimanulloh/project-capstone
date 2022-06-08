package com.bangkit.capstone.c22_ps321.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import com.bangkit.capstone.c22_ps321.R
import com.bangkit.capstone.c22_ps321.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private val aboutUrl = "https://github.com/C22-PS321/project-capstone/blob/master/README.md"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding.apply {
            tvAppName.typeface = ResourcesCompat.getFont(this@MainActivity, R.font.kdam_font)
            cvScan.setOnClickListener {
                startActivity(Intent(this@MainActivity, ScanActivity::class.java))
            }
            cvHistory.setOnClickListener {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
            }
            cvLogout.setOnClickListener { alertLogout() }
            cvMyProfile.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        ProfileActivity::class.java
                    )
                )
            }
            btnSettings.setOnClickListener {
                val popUpMenu = PopupMenu(this@MainActivity, btnSettings)
                popUpMenu.menuInflater.inflate(R.menu.main_menu, popUpMenu.menu)
                popUpMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.change_language -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        R.id.about_menu -> {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(aboutUrl))
                            startActivity(intent)
                        }
                        R.id.logout_menu -> alertLogout()
                    }
                    true
                }
                popUpMenu.show()
            }
        }

        firebaseUser.let {
            val name = firebaseUser.email
            binding.apply {
                Glide.with(this@MainActivity)
                    .load(firebaseUser.photoUrl)
                    .placeholder(R.drawable.placeholder_avatar)
                    .into(binding.icAvatar)
                binding.tvSayHello.text = getString(R.string.tv_say_hello, name)
            }
        }
    }

    override fun onBackPressed() {
        alertCloseApp()
    }

    private fun alertCloseApp() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(R.string.app_name)
            setMessage(R.string.message_logout)
            setPositiveButton(
                R.string.yes
            ) { _, _ ->
                finish()
            }
            setNegativeButton(
                R.string.no
            ) { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.create()
        builder.show()
    }

    private fun alertLogout() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(R.string.app_name)
            setMessage(R.string.message_logout)
            setPositiveButton(
                R.string.yes
            ) { _, _ ->
                signOut()
            }
            setNegativeButton(
                R.string.no
            ) { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.create()
        builder.show()
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}