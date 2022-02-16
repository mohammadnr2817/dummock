package dev.radis.dummock.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.radis.dummock.R
import dev.radis.dummock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}