package dev.radis.dummock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.model.repository.DirectionRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val directionRepository: DirectionRepository
) : ViewModel() {

    fun getDirection(points: List<Point>) {
        viewModelScope.launch {
            directionRepository.getDirection(points)
        }
    }

    override fun onCleared() {
        directionRepository.dispose()
        super.onCleared()
    }
}