package dev.radis.dummock.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.radis.dummock.databinding.FragmentArchiveBinding
import dev.radis.dummock.utils.mvi.MviView
import dev.radis.dummock.view.state.ArchiveState
import dev.radis.dummock.viewmodel.ArchiveViewModel
import dev.radis.dummock.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArchiveFragment : Fragment(), MviView<ArchiveState> {
    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var viewModel: ArchiveViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[ArchiveViewModel::class.java]

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.stateFlow.collect { state ->
                renderState(state)
            }
        }

    }

    override fun renderState(state: ArchiveState) {

    }

}