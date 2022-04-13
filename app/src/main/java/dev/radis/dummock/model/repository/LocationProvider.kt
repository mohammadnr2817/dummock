package dev.radis.dummock.model.repository

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
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
import androidx.core.app.NotificationCompat
import dev.radis.dummock.R
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.JTSUtils
import dev.radis.dummock.utils.LocationUtils
import dev.radis.dummock.utils.constants.NumericConstants.FOREGROUND_NOTIFICATION_ID
import dev.radis.dummock.utils.constants.NumericConstants.LINE_INDEX_INTERVAL
import dev.radis.dummock.utils.constants.NumericConstants.PROVIDER_ACCURACY
import dev.radis.dummock.utils.constants.NumericConstants.PROVIDER_BEARING_ACCURACY_DEGREES
import dev.radis.dummock.utils.constants.NumericConstants.PROVIDER_SPEED_ACCURACY_METERS_PER_SECOND
import dev.radis.dummock.utils.constants.NumericConstants.PROVIDER_VERTICAL_ACCURACY_METERS
import dev.radis.dummock.utils.constants.StringConstants.FOREGROUND_NOTIFICATION_CHANNEL_ID
import dev.radis.dummock.utils.constants.StringConstants.FOREGROUND_NOTIFICATION_CHANNEL_NAME
import dev.radis.dummock.utils.constants.StringConstants.FOREGROUND_NOTIFICATION_CONTENT_TITLE
import dev.radis.dummock.utils.constants.StringConstants.PROVIDER_GPS
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.locationtech.jts.linearref.LengthIndexedLine

class LocationProvider : Service() {
    private val binder = LocationProviderBinder()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var timerJob: Job? = null
    private var lengthIndexedLine: LengthIndexedLine? = null
    private var lineIndex: Double = 0.0
    private var speed: Float = 0F
    private lateinit var locationManager: LocationManager
    private var tickerInterval: Long = 0

    private val _locationFlow: MutableStateFlow<Point?> = MutableStateFlow(null)
    val locationFlow: StateFlow<Point?> = _locationFlow

    inner class LocationProviderBinder() : Binder() {
        fun getService(): LocationProvider = this@LocationProvider
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        return START_NOT_STICKY
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
            while (true) {
                val lastLocation = requireNotNull(lengthIndexedLine).extractPoint(lineIndex)
                provideNewLocation()
                val nextLocation = requireNotNull(lengthIndexedLine).extractPoint(lineIndex)
                val distance = LocationUtils.haversine(
                    Point.fromCoordinate(lastLocation),
                    Point.fromCoordinate(nextLocation)
                )
                tickerInterval = ((distance / speed) * 60 * 60 * 1000).toLong()
                delay(tickerInterval)
                if (lineIndex == lengthIndexedLine?.endIndex)
                    stopSelf()
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
                PROVIDER_GPS,
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
            locationManager.setTestProviderEnabled(PROVIDER_GPS, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun submitLocationToProvider(point: Point) {
        val location = Location(PROVIDER_GPS).apply {
            latitude = point.lat
            longitude = point.lng
            altitude = 0.0
            speed = this@LocationProvider.speed
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            accuracy = PROVIDER_ACCURACY
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bearingAccuracyDegrees = PROVIDER_BEARING_ACCURACY_DEGREES
                verticalAccuracyMeters = PROVIDER_VERTICAL_ACCURACY_METERS
                speedAccuracyMetersPerSecond = PROVIDER_SPEED_ACCURACY_METERS_PER_SECOND
            }
        }
        locationManager.setTestProviderLocation(PROVIDER_GPS, location)
        coroutineScope.launch {
            _locationFlow.emit(point)
        }
    }

    private fun showNotification() {
        val notification = createNotification()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
            FOREGROUND_NOTIFICATION_ID,
            notification
        )
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun dismissNotification() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            FOREGROUND_NOTIFICATION_ID
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(
                NotificationChannel(
                    FOREGROUND_NOTIFICATION_CHANNEL_ID,
                    FOREGROUND_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
    }

    private fun createNotification(): Notification {
        createNotificationChannel()
        return NotificationCompat.Builder(this, FOREGROUND_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(FOREGROUND_NOTIFICATION_CONTENT_TITLE)
            .setSmallIcon(R.drawable.nav_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

}