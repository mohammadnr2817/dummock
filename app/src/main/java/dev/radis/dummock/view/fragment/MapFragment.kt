package dev.radis.dummock.view.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.paris.extensions.style
import com.carto.graphics.Color
import com.carto.styles.LineStyle
import com.carto.styles.LineStyleBuilder
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import dev.radis.dummock.R
import dev.radis.dummock.databinding.FragmentMapBinding
import dev.radis.dummock.di.map.MapComponentBuilder
import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.SingleUse
import dev.radis.dummock.utils.constants.DirectionType
import dev.radis.dummock.utils.constants.NumericConstants.FIRST_LOCATION_INDEX
import dev.radis.dummock.utils.constants.NumericConstants.POLYLINE_WIDTH
import dev.radis.dummock.utils.constants.NumericConstants.SECOND_LOCATION_INDEX
import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_BIKE
import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_CAR
import dev.radis.dummock.utils.mvi.MviView
import dev.radis.dummock.view.intent.MapIntent
import dev.radis.dummock.view.state.MapState
import dev.radis.dummock.viewmodel.MapViewModel
import dev.radis.dummock.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.neshan.common.model.LatLng
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

        binding.mapNeshanMapView.setOnMapLongClickListener {
            viewModel.handleIntent(
                MapIntent.LocationSelectedIntent(
                    Point(it.latitude, it.longitude)
                )
            )
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
                    LatLng(requireNotNull(point.lat), requireNotNull(point.lng)),
                    MarkerStyleBuilder().apply {
                        size = 32.0F
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

    private fun addRouteDetailsState(details: SingleUse<DirectionModel>?) {
        details?.let {
            it.ifNotUsedBefore()?.let { model ->
                changeRouteDetailsTexts(model)

                removePreviousPolyline()

                currentDirectionPolyline = Polyline(
                    ArrayList(model.points.map { point ->
                        LatLng(
                            requireNotNull(point.lat),
                            requireNotNull(point.lng)
                        )
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
        binding.mapBtnOrigin.text = model.origin?.toString()
        binding.mapBtnDestination.text = model.destination?.toString()
        binding.mapViewBottom.btmSheetRouteDetailTxtRouteDistance.text = model.distance
        binding.mapViewBottom.btmSheetRouteDetailTxtRouteDuration.text = model.duration
    }

    private fun switchDirectionType(@DirectionType directionType: String) {
        when (directionType) {
            DIRECTION_TYPE_CAR -> {
                binding.mapViewBottom.btmSheetRouteDetailSettingsTypeCar.style(R.style.DButton_Outline_Primary_Active)
                binding.mapViewBottom.btmSheetRouteDetailSettingsTypeBike.style(R.style.DButton_Outline_Primary_InActive)
            }
            DIRECTION_TYPE_BIKE -> {
                binding.mapViewBottom.btmSheetRouteDetailSettingsTypeCar.style(R.style.DButton_Outline_Primary_InActive)
                binding.mapViewBottom.btmSheetRouteDetailSettingsTypeBike.style(R.style.DButton_Outline_Primary_Active)
            }
        }
    }

}