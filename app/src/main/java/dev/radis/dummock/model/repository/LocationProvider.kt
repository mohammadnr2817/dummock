package dev.radis.dummock.model.repository

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.JTSUtils
import dev.radis.dummock.utils.LocationUtils
import dev.radis.dummock.utils.constants.NumericConstants.LINE_INDEX_INTERVAL
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import org.locationtech.jts.linearref.LengthIndexedLine

class LocationProvider : Service() {
    private val binder = LocationProviderBinder()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var timerJob: Job? = null
    private var lengthIndexedLine: LengthIndexedLine? = null
    private var lineIndex: Double = 0.0
    private var speed: Float = 0F
    private lateinit var locationManager: LocationManager

    inner class LocationProviderBinder() : Binder() {
        fun getService(): LocationProvider = this@LocationProvider
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        addMockProvider()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun startProvidingLocations(points: List<Point>, speed: Float) {
        lengthIndexedLine = JTSUtils.convertPointsToLengthIndexedLine(points)
        cancelLastProvide()
        this.speed = speed

        timerJob = coroutineScope.launch {
            flow<Nothing> {
                while (true) {
                    val lastLocation = requireNotNull(lengthIndexedLine).extractPoint(lineIndex)
                    provideNewLocation()
                    val nextLocation = requireNotNull(lengthIndexedLine).extractPoint(lineIndex)
                    val distance = LocationUtils.haversine(
                        Point.fromCoordinate(lastLocation),
                        Point.fromCoordinate(nextLocation)
                    )
                    val interval = (distance / speed) * 60 * 60 * 1000
                    delay(interval.toLong())
                }
            }
        }
    }

    private fun provideNewLocation() {
        val nextIndex =
            if (requireNotNull(lengthIndexedLine?.isValidIndex(lineIndex + LINE_INDEX_INTERVAL)))
                lineIndex + LINE_INDEX_INTERVAL
            else requireNotNull(lengthIndexedLine?.endIndex)
        val nextCoordinate =
            Point.fromCoordinate(requireNotNull(lengthIndexedLine).extractPoint(nextIndex))
        submitLocationToProvider(nextCoordinate)
        lineIndex = nextIndex
    }

    private fun cancelLastProvide() {
        timerJob?.cancel()
        lineIndex = 0.0
    }


    fun stopProvidingLocations() {
        timerJob?.cancel()
    }

    @SuppressLint("WrongConstant")
    private fun addMockProvider() {
        try {
            locationManager.addTestProvider(
                "gps", // TODO: move to const
                false,
                false,
                false,
                false,
                false,
                true,
                true,
                Criteria.POWER_LOW,
                Criteria.ACCURACY_FINE
            )
            locationManager.setTestProviderEnabled("gps", true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun submitLocationToProvider(point: Point) {
        val location = Location("gps").apply {
            latitude = point.lat
            longitude = point.lng
            altitude = 0.0
            speed = this@LocationProvider.speed
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            accuracy = 5F // TODO: move to const
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bearingAccuracyDegrees = 0.1f // TODO: move to const
                verticalAccuracyMeters = 0.1f // TODO: move to const
                speedAccuracyMetersPerSecond = 0.01f // TODO: move to const
            }
        }
        locationManager.setTestProviderLocation("gps", location)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

}