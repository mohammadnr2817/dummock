package dev.radis.dummock.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.radis.dummock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StoragePermissionHandler {
    private lateinit var binding: ActivityMainBinding
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var storagePermissionCallback: (Boolean) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                storagePermissionCallback.invoke(isGranted)
            }
    }

    override fun requestPermission(callback: (Boolean) -> Unit) {
        storagePermissionCallback = callback
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                storagePermissionCallback.invoke(true)
            }
            else -> {
                requestPermissionLauncher?.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
}