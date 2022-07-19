package dev.radis.dummock.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dev.radis.dummock.R
import dev.radis.dummock.databinding.DialogSpeedBinding
import dev.radis.dummock.utils.constants.StringConstants.MOVE_SPEED_FA
import kotlin.math.abs
import kotlin.math.max

class SpeedDialog : DialogFragment() {
    private lateinit var binding: DialogSpeedBinding
    private var speed = 0

    private var filteredUpdate = 0

    var speedListener: (Int) -> Unit = {}

    companion object {
        private const val INITIAL_SPEED_KEY = "initial_speed_key"
        private const val SPEED_UPDATE_RATIO = 5
    }

    fun newInstance(initialSpeed: Int): SpeedDialog {
        val args = Bundle()
        args.putInt(INITIAL_SPEED_KEY, initialSpeed)

        val fragment = SpeedDialog()
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            speed = it.getInt(INITIAL_SPEED_KEY, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSpeedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dialogSteeringWheelTxtSpeed.text = "$speed$MOVE_SPEED_FA"

        binding.dialogSteeringWheelPositive.setOnClickListener {
            speedListener.invoke(speed)
            dialog?.dismiss()
        }

        binding.dialogSteeringWheelNegative.setOnClickListener {
            dialog?.dismiss()
        }

        binding.dialogSteeringWheel.updateListener = { update ->
            filteredUpdate += update
            if (abs(filteredUpdate) >= SPEED_UPDATE_RATIO) {
                speed = max(speed + filteredUpdate / SPEED_UPDATE_RATIO, 0)
                filteredUpdate %= SPEED_UPDATE_RATIO
                binding.dialogSteeringWheelTxtSpeed.text = "$speed$MOVE_SPEED_FA"
            }
        }

    }

    override fun getTheme(): Int {
        return R.style.RoundedCornersDialog
    }

}