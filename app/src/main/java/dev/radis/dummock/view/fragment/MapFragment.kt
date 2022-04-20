package dev.radis.dummock.view.fragment

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.carto.graphics.Color
import com.carto.styles.LineStyle
import com.carto.styles.LineStyleBuilder
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import dev.radis.dummock.R
import dev.radis.dummock.databinding.FragmentMapBinding
import dev.radis.dummock.di.map.MapComponentBuilder
import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.model.repository.LocationProvider
import dev.radis.dummock.utils.LocationUtils
import dev.radis.dummock.utils.SingleUse
import dev.radis.dummock.utils.constants.DirectionType
import dev.radis.dummock.utils.constants.NumericConstants.FIRST_LOCATION_INDEX
import dev.radis.dummock.utils.constants.NumericConstants.MAP_ACTION_TIME
import dev.radis.dummock.utils.constants.NumericConstants.MAP_ZOOM
import dev.radis.dummock.utils.constants.NumericConstants.MARKER_SIZE
import dev.radis.dummock.utils.constants.NumericConstants.POLYLINE_WIDTH
import dev.radis.dummock.utils.constants.NumericConstants.SECOND_LOCATION_INDEX
import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_BIKE
import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_CAR
import dev.radis.dummock.utils.extension.*
import dev.radis.dummock.utils.mvi.MviView
import dev.radis.dummock.view.intent.MapIntent
import dev.radis.dummock.view.state.MapState
import dev.radis.dummock.viewmodel.MapViewModel
import dev.radis.dummock.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.neshan.mapsdk.model.Marker
import org.neshan.mapsdk.model.Polyline
import javax.inject.Inject

class MapFragment : Fragment(), MviView<MapState> {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var viewModel: MapViewModel
    private var currentDirectionPolyline: Polyline? = null

    private lateinit var routeDetailBottomSheetBehavior: BottomSheetBehavior<MaterialCardView>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var isLocationProviderServiceConnected: Boolean = false
    private var locationProviderService: LocationProvider? = null
    private var navigationMarker: Marker? = null

    private lateinit var serviceIntent: Intent

    private val locationProviderConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            isLocationProviderServiceConnected = true
            locationProviderService =
                (binder as LocationProvider.LocationProviderBinder).getService()
            lifecycleScope.launch {
                locationProviderService?.locationFlow?.collect(this@MapFragment::observeLocations)
            }
            locationProviderService?.startProvidingLocations(
                requireNotNull(viewModel.stateFlow.value.direction?.value).points,
                viewModel.stateFlow.value.speed.toFloat()
            )
            viewModel.handleIntent(MapIntent.ChangeProviderServiceStateIntent(true))
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            isLocationProviderServiceConnected = false
            locationProviderService = null
            viewModel.handleIntent(MapIntent.ChangeProviderServiceStateIntent(false))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapComponentBuilder.getComponent().inject(this)
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[MapViewModel::class.java]
        routeDetailBottomSheetBehavior =
            BottomSheetBehavior.from(binding.mapViewBottom.btmSheetRouteDetailRoot)

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.stateFlow.collect { state ->
                renderState(state)
            }
        }

        binding.mapNeshanMapView.isTrafficEnabled = true
        binding.mapNeshanMapView.isPoiEnabled = true

        serviceIntent = Intent(activity, LocationProvider::class.java)

        // TODO: Set margin dynamically!
        binding.mapNeshanMapView.settings.setNeshanLogoMargins(12.toPx.toInt(), 112.toPx.toInt())

        routeDetailBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                with(binding.mapViewBottom) {
                    btmSheetRouteDetailBtnStartNavPeek.alpha = 1 - slideOffset
                    btmSheetRouteDetailGpSettings.alpha = slideOffset
                }
            }
        })

        binding.mapBtnChooseLocation.setOnClickListener {
            viewModel.handleIntent(
                MapIntent.LocationSelectedIntent(
                    Point(
                        binding.mapNeshanMapView.cameraTargetPosition.latitude,
                        binding.mapNeshanMapView.cameraTargetPosition.longitude
                    )
                )
            )
            binding.mapChooseLocationMarker.bounceAnimation(800)
        }

        binding.mapImgSwitchRoutes.setOnClickListener {
            viewModel.handleIntent(
                MapIntent.LocationSwitchIntent(
                    FIRST_LOCATION_INDEX, SECOND_LOCATION_INDEX
                )
            )
        }

        binding.mapViewBottom.btmSheetRouteDetailSettingsTypeCar.setOnClickListener {
            viewModel.handleIntent(
                MapIntent.SwitchDirectionTypeIntent(DIRECTION_TYPE_CAR)
            )
        }

        binding.mapViewBottom.btmSheetRouteDetailSettingsTypeBike.setOnClickListener {
            viewModel.handleIntent(
                MapIntent.SwitchDirectionTypeIntent(DIRECTION_TYPE_BIKE)
            )
        }

        binding.mapBtnOrigin.setOnLongClickListener {
            viewModel.handleIntent(
                MapIntent.CopyToClipboardIntent(binding.mapBtnOrigin.text.toString())
            )
            false
        }

        binding.mapBtnDestination.setOnLongClickListener {
            viewModel.handleIntent(
                MapIntent.CopyToClipboardIntent(binding.mapBtnDestination.text.toString())
            )
            false
        }

        binding.mapViewBottom.btmSheetRouteDetailBtnStartNavPeek.setOnClickListener {
            setLocationProviderState()
        }

        binding.mapViewBottom.btmSheetRouteDetailBtnStartNav.setOnClickListener {
            setLocationProviderState()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun renderState(state: MapState) {
        state.isLoading?.let {
            loadingState(it.value)
        }
        if (state.message != null) messageState(state.message.value)
        addMarkerState(state.markers)
        addRouteDetailsState(state.direction)
        switchDirectionType(state.directionRequestType)
        renderProviderServiceState(state.serviceRunning)
        chooseLocationState()
        setRouteDetailsState()
    }

    private fun loadingState(isLoading: Boolean) {
        if (isLoading)
            binding.mapViewBottom.btmSheetRouteDetailRoot.isVisible = isLoading
        binding.mapViewBottom.btmSheetRouteDetailProgress.isVisible = isLoading
        binding.mapViewBottom.btmSheetRouteDetailMainViews.isVisible = !isLoading
        binding.mapTxtHelper.isVisible = isLoading
        binding.mapViewTopViews.isVisible = !isLoading
    }

    private fun messageState(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun addMarkerState(markers: SingleUse<List<Point>>?) {
        markers?.ifNotUsedBefore()?.forEach { point ->
            binding.mapNeshanMapView.addMarker(
                Marker(
                    point.toLatLng(),
                    MarkerStyleBuilder().apply {
                        size = MARKER_SIZE
                        bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
                            BitmapFactory.decodeResource(
                                resources, R.drawable.location
                            )
                        )
                    }.buildStyle()
                )
            )
        }
    }

    private fun chooseLocationState() {
        viewModel.stateFlow.value.markers.value.also {
            binding.mapBtnChooseLocation.text =
                getString(if (it.isEmpty()) R.string.txt_choose_origin else R.string.txt_choose_destination)
            if (it.size < 2) {
                binding.mapChooseLocationMarker.fadeVisible()
                binding.mapBtnChooseLocation.fadeVisible()
            } else {
                binding.mapBtnChooseLocation.fadeInVisible()
                binding.mapChooseLocationMarker.fadeInVisible()
            }
        }
    }

    private fun addRouteDetailsState(details: SingleUse<DirectionModel>?) {
        details?.let {
            it.ifNotUsedBefore()?.let { model ->

                changeRouteDetailsTexts(model)

                removePreviousPolyline()

                currentDirectionPolyline = Polyline(
                    ArrayList(model.points.map { point ->
                        point.toLatLng()
                    }),
                    createPolylineStyle()
                )

                binding.mapNeshanMapView.addPolyline(currentDirectionPolyline)
            }
        }
    }

    private fun createPolylineStyle(): LineStyle {
        return LineStyleBuilder().apply {
            width = POLYLINE_WIDTH
            color = Color(
                ContextCompat.getColor(
                    requireNotNull(context),
                    R.color.blue_berry
                )
            )
        }.buildStyle()
    }

    private fun removePreviousPolyline() {
        currentDirectionPolyline?.let { polyline ->
            binding.mapNeshanMapView.removePolyline(polyline)
        }
    }

    private fun changeRouteDetailsTexts(model: DirectionModel) {
        binding.mapBtnOrigin.text = model.origin?.toString()?.toPersianDigits()
        binding.mapBtnDestination.text = model.destination?.toString()?.toPersianDigits()
        binding.mapViewBottom.btmSheetRouteDetailTxtRouteDistance.text =
            model.distance?.toPersianDigits()
        binding.mapViewBottom.btmSheetRouteDetailTxtRouteDuration.text =
            model.duration?.toPersianDigits()
    }

    private fun switchDirectionType(@DirectionType directionType: String) {
        when (directionType) {
            DIRECTION_TYPE_CAR -> {
                setDirectionButtonState(
                    binding.mapViewBottom.btmSheetRouteDetailSettingsTypeCar,
                    true
                )
                setDirectionButtonState(
                    binding.mapViewBottom.btmSheetRouteDetailSettingsTypeBike,
                    false
                )
            }
            DIRECTION_TYPE_BIKE -> {
                setDirectionButtonState(
                    binding.mapViewBottom.btmSheetRouteDetailSettingsTypeCar,
                    false
                )
                setDirectionButtonState(
                    binding.mapViewBottom.btmSheetRouteDetailSettingsTypeBike,
                    true
                )
            }
        }
    }

    private fun setDirectionButtonState(button: MaterialButton, active: Boolean) {
        val stateColor = ContextCompat.getColor(
            requireNotNull(context),
            if (active) R.color.pink_700 else R.color.grey_700
        )
        button.apply {
            setTextColor(stateColor)
            strokeColor = ColorStateList.valueOf(stateColor)
            iconTint = ColorStateList.valueOf(stateColor)
        }
    }

    private fun renderProviderServiceState(serviceRunning: SingleUse<Boolean>?) {
        serviceRunning?.ifNotUsedBefore()?.let {
            val stateColor = ContextCompat.getColor(
                requireNotNull(context),
                if (it) R.color.red_700 else R.color.pink_700
            )
            val stateText = if (it) "پایان" else "شروع"
            //if (it) binding.mapGpRouteIntents.fadeInVisible() else binding.mapGpRouteIntents.fadeVisible()
            binding.mapGpRouteIntents.isVisible = !it
            binding.mapViewBottom.btmSheetRouteDetailBtnStartNavPeek.apply {
                text = stateText
                setBackgroundColor(stateColor)
            }
            binding.mapViewBottom.btmSheetRouteDetailBtnStartNav.apply {
                text = stateText
                setBackgroundColor(stateColor)
            }
        }
    }

    private fun setRouteDetailsState() {
        if (isLocationProviderServiceConnected)
            routeDetailBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else {
            navigationMarker?.let {
                binding.mapNeshanMapView.removeMarker(navigationMarker)
            }
            navigationMarker = null
        }
        lifecycleScope.launch {
            delay(500)
            binding.mapViewBottom.btmSheetRouteDetailGpSettings.isVisible =
                !isLocationProviderServiceConnected
        }
    }

    private fun setLocationProviderState() {
        if (isLocationProviderServiceConnected)
            stopProvidingLocation()
        else
            startProvidingLocation()
    }

    private fun startProvidingLocation() {
        activity?.startService(serviceIntent)
        activity?.bindService(serviceIntent, locationProviderConnection, 0)
    }

    private fun stopProvidingLocation() {
        activity?.stopService(serviceIntent)
        binding.mapNeshanMapView.moveCamera(
            viewModel.stateFlow.value.markers.value.firstOrNull()?.toLatLng(), MAP_ACTION_TIME
        )
    }

    private fun observeLocations(point: Point?) {
        point?.let {
            var bearing: Double? = null

            navigationMarker?.let { navMarker ->
                bearing = LocationUtils.findBearing(
                    Point(
                        navMarker.latLng.latitude,
                        navMarker.latLng.longitude
                    ),
                    it
                )
                binding.mapNeshanMapView.removeMarker(navigationMarker)
            }

            navigationMarker = Marker(
                it.toLatLng(),
                MarkerStyleBuilder().apply {
                    size = MARKER_SIZE
                    bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
                        BitmapFactory.decodeResource(
                            resources, R.drawable.nav_icon
                        ).rotate(bearing?.toFloat() ?: 0F)
                    )
                }.buildStyle()
            )

            binding.mapNeshanMapView.moveCamera(
                it.toLatLng(), MAP_ACTION_TIME
            )

            binding.mapNeshanMapView.setZoom(MAP_ZOOM, MAP_ACTION_TIME)
            binding.mapNeshanMapView.addMarker(navigationMarker)
        }
    }

}