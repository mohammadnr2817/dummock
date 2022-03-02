package dev.radis.dummock.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dev.radis.dummock.databinding.ActivityMapBinding
import dev.radis.dummock.di.map.MapComponentBuilder
import dev.radis.dummock.viewmodel.MapViewModel
import dev.radis.dummock.viewmodel.ViewModelFactory
import javax.inject.Inject

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var viewModel: MapViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapComponentBuilder.getComponent().inject(this)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, viewModelFactory)[MapViewModel::class.java]

    }
}