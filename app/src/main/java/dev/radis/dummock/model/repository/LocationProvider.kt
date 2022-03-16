package dev.radis.dummock.model.repository

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.JTSUtils
import dev.radis.dummock.utils.constants.NumericConstants.LOCATION_PROVIDING_INTERVAL
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow

class LocationProvider : Service() {
    private val binder = LocationProviderBinder()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var timerJob: Job? = null

    inner class LocationProviderBinder() : Binder() {
        fun getService(): LocationProvider = this@LocationProvider
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun startProvidingLocations(points: List<Point>, speed: Int) {
        val lengthIndexedLine = JTSUtils.convertPointsToLengthIndexedLine(points)
        timerJob = coroutineScope.launch {
            flow<Nothing> {
                while (true) {
                    provideNewLocation()
                    delay(LOCATION_PROVIDING_INTERVAL)
                }
            }
        }
    }

    private fun provideNewLocation() {

    }


    fun stopProvidingLocations() {
        timerJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

}