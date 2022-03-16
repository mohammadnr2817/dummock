package dev.radis.dummock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.model.repository.DirectionRepository
import dev.radis.dummock.utils.SingleUse
import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_CAR
import dev.radis.dummock.utils.mvi.MviModel
import dev.radis.dummock.view.intent.MapIntent
import dev.radis.dummock.view.state.MapState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val directionRepository: DirectionRepository
) : ViewModel(), MviModel<MapIntent, MapState> {

    private val _stateFlow: MutableStateFlow<MapState> = MutableStateFlow(MapState())
    override val stateFlow: StateFlow<MapState> = _stateFlow

    override fun handleIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.LocationSelectedIntent -> {
                addLocationToLocationsList(intent.point)
            }
            is MapIntent.LocationSwitchIntent -> switchLocations(
                intent.firstLocationIndex,
                intent.secondLocationIndex
            )
        }
    }

    private fun switchLocations(firstLocationIndex: Int, secondLocationIndex: Int) {

        viewModelScope.launch {
            Collections.swap(
                stateFlow.value.markers.value,
                firstLocationIndex,
                secondLocationIndex
            )

            getDirectionIfReady()
        }

    }

    private fun canAddLocationToLocationsList(): Boolean {
        return _stateFlow.value.markers.value.size < 2
    }

    private fun addLocationToLocationsList(point: Point) {
        viewModelScope.launch {
            if (canAddLocationToLocationsList()) {
                _stateFlow.emit(
                    stateFlow.value.copy(
                        markers = SingleUse(
                            stateFlow.value.markers.value.toMutableList().apply { add(point) })
                    )
                )
                getDirectionIfReady()
            }
        }
    }

    private fun canGetDirection(): Boolean {
        return stateFlow.value.markers.value.size == 2
    }

    private suspend fun getDirectionIfReady() {
        if (canGetDirection())
            getDirection(
                //stateFlow.value.direction.value.directionRequestType,
                DIRECTION_TYPE_CAR,
                stateFlow.value.markers.value
            )
    }

    private suspend fun getDirection(directionRequestType: String, points: List<Point>) {
        _stateFlow.emit(stateFlow.value.copy(isLoading = SingleUse(true)))
        viewModelScope.launch(Dispatchers.IO) {
            val response = directionRepository.getDirection(directionRequestType, points)
            response.ifNotSuccessful {
                _stateFlow.emit(
                    stateFlow.value.copy(
                        isLoading = SingleUse(false),
                        message = SingleUse("Error")
                    )
                )
            }
            response.ifSuccessful {
                _stateFlow.emit(
                    stateFlow.value.copy(
                        isLoading = SingleUse(false),
                        direction = SingleUse(
                            DirectionModel(
                                origin = it.origin,
                                destination = it.destination,
                                points = it.points,
                                distance = it.distance,
                                duration = it.duration
                            )
                        )
                    )
                )
            }
        }
    }

    override fun onCleared() {
        directionRepository.dispose()
        super.onCleared()
    }
}