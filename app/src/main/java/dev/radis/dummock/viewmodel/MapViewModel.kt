package dev.radis.dummock.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.model.entity.db.ArchiveModel
import dev.radis.dummock.model.preference.PreferencesImpl
import dev.radis.dummock.model.repository.DatabaseRepository
import dev.radis.dummock.model.repository.DirectionFileRepository
import dev.radis.dummock.model.repository.DirectionRepository
import dev.radis.dummock.utils.SingleUse
import dev.radis.dummock.utils.constants.DirectionType
import dev.radis.dummock.utils.constants.StringConstants.COPIED
import dev.radis.dummock.utils.constants.StringConstants.DATE_FORMAT
import dev.radis.dummock.utils.mvi.MviModel
import dev.radis.dummock.view.intent.MapIntent
import dev.radis.dummock.view.state.MapState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MapViewModel @Inject constructor(
    private val directionRepository: DirectionRepository,
    private val directionFileRepository: DirectionFileRepository,
    private val databaseRepository: DatabaseRepository,
    private val sharedPreferences: PreferencesImpl,
    private val context: Context
) : ViewModel(), MviModel<MapIntent, MapState> {

    private val _stateFlow: MutableStateFlow<MapState> = MutableStateFlow(MapState())
    override val stateFlow: StateFlow<MapState> = _stateFlow
    private var clipboardManager: ClipboardManager? = null

    init {
        clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun handleIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.LocationSelectedIntent -> {
                addLocationToLocationsList(intent.point)
            }
            is MapIntent.LocationSwitchIntent -> switchLocations(
                intent.firstLocationIndex,
                intent.secondLocationIndex
            )
            is MapIntent.SwitchDirectionTypeIntent -> changeDirectionType(intent.directionRequestType)
            is MapIntent.CopyToClipboardIntent -> copyToClipboard(intent.value)
            is MapIntent.ChangeProviderServiceStateIntent -> changeProviderServiceState(intent.value)
            MapIntent.NavigateInAnotherAppIntent -> navigateInAnotherApp()
            MapIntent.RemoveLastLocationIntent -> removeLocationFromLastLocation()
            MapIntent.ShareRouteIntent -> shareRoute()
            is MapIntent.SaveDefaultSpeedIntent -> setDefaultSpeed(intent.value)
            MapIntent.GetDefaultSpeedIntent -> getDefaultSpeed()
            MapIntent.SaveRouteIntent -> saveRouteToDatabase()
        }
    }

    private fun saveRouteToDatabase() {
        viewModelScope.launch {
            databaseRepository.saveArchive(
                ArchiveModel(
                    id = 0,
                    title = "New test mock",
                    date = SimpleDateFormat(DATE_FORMAT).format(Date()),
                    speed = sharedPreferences.getDefaultSpeed(),
                    direction = stateFlow.value.direction!!.value
                )
            )
            _stateFlow.emit(
                stateFlow.value.copy(
                    message = SingleUse("Route saved")
                )
            )
        }
    }

    private fun getDefaultSpeed() {
        viewModelScope.launch {
            _stateFlow.emit(
                stateFlow.value.copy(
                    speed = sharedPreferences.getDefaultSpeed()
                )
            )
        }
    }

    private fun setDefaultSpeed(speed: Int) {
        viewModelScope.launch {
            async {
                sharedPreferences.setDefaultSpeed(speed)
            }.await()
            getDefaultSpeed()
        }
    }

    private fun shareRoute() {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                directionFileRepository.writeFile(requireNotNull(stateFlow.value.direction).value.points)
            response.ifNotSuccessful {
                _stateFlow.emit(
                    stateFlow.value.copy(
                        message = SingleUse(it.toString())
                    )
                )
            }
            response.ifSuccessful {
                val uri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName.toString() + ".provider",
                    File(it)
                )

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "*/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                }
                _stateFlow.emit(
                    stateFlow.value.copy(
                        executeIntent = SingleUse(Pair(shareIntent, true))
                    )
                )
            }
        }
    }

    private fun removeLocationFromLastLocation() {
        viewModelScope.launch {
            _stateFlow.emit(
                stateFlow.value.copy(
                    markers = SingleUse(
                        stateFlow.value.markers.value.toMutableList().apply { removeLastOrNull() })
                )
            )
        }
    }

    private fun navigateInAnotherApp() {
        viewModelScope.launch {
            val destination = stateFlow.value.markers.value.last()
            val uri = ("geo:" + destination.lat.toString() + "," + destination.lng.toString())
            val navigationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

            _stateFlow.emit(
                stateFlow.value.copy(executeIntent = SingleUse(Pair(navigationIntent, false)))
            )
        }
    }

    private fun changeProviderServiceState(value: Boolean) {
        viewModelScope.launch {
            _stateFlow.emit(stateFlow.value.copy(serviceRunning = SingleUse(value)))
        }
    }

    private fun copyToClipboard(value: String) {
        viewModelScope.launch {
            clipboardManager?.setPrimaryClip(ClipData.newPlainText("Dummock", value))
            _stateFlow.emit(
                stateFlow.value.copy(
                    message = SingleUse(COPIED)
                )
            )
        }
    }

    private fun isDirectionTypeSame(@DirectionType directionRequestType: String): Boolean {
        return directionRequestType == stateFlow.value.directionRequestType
    }

    private fun changeDirectionType(@DirectionType directionRequestType: String) {
        if (isDirectionTypeSame(directionRequestType)) return
        viewModelScope.launch {
            _stateFlow.emit(
                stateFlow.value.copy(
                    directionRequestType = directionRequestType
                )
            )
            getDirectionIfReady()
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
                stateFlow.value.directionRequestType,
                stateFlow.value.markers.value
            )
    }

    private suspend fun getDirection(
        @DirectionType directionRequestType: String,
        points: List<Point>
    ) {
        _stateFlow.emit(stateFlow.value.copy(isLoading = SingleUse(true)))
        viewModelScope.launch(Dispatchers.IO) {
            val response = directionRepository.getDirection(directionRequestType, points)
            response.ifNotSuccessful {
                _stateFlow.emit(
                    stateFlow.value.copy(
                        isLoading = SingleUse(false),
                        message = SingleUse(it.toString())
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
                        ),
                        speed = sharedPreferences.getDefaultSpeed()
                    )
                )
            }
        }
    }

    override fun onCleared() {
        directionRepository.dispose()
        directionFileRepository.dispose()
        databaseRepository.dispose()
        super.onCleared()
    }
}