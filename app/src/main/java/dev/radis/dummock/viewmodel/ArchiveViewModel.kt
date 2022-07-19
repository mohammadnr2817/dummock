package dev.radis.dummock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.radis.dummock.model.entity.db.ArchiveModel
import dev.radis.dummock.model.repository.DatabaseRepository
import dev.radis.dummock.utils.SingleUse
import dev.radis.dummock.utils.mvi.MviModel
import dev.radis.dummock.view.intent.ArchiveIntent
import dev.radis.dummock.view.state.ArchiveState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArchiveViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) : ViewModel(), MviModel<ArchiveIntent, ArchiveState> {

    private val _stateFlow: MutableStateFlow<ArchiveState> = MutableStateFlow(ArchiveState())
    override val stateFlow: StateFlow<ArchiveState> = _stateFlow


    init {
        getArchivesFromDatabase()
    }

    override fun handleIntent(intent: ArchiveIntent) {
        when (intent) {
            is ArchiveIntent.SelectRouteIntent -> TODO()
            ArchiveIntent.LoadRoutesIntent -> getArchivesFromDatabase()
        }
    }

    private fun getArchivesFromDatabase() {
        viewModelScope.launch {
            val response = databaseRepository.getArchives()
            response.ifNotSuccessful {
                _stateFlow.emit(
                    stateFlow.value.copy(
                        isLoading = SingleUse(false),
                        message = SingleUse(it.toString())
                    )
                )
            }
            response.ifSuccessful { archives ->
                _stateFlow.emit(
                    stateFlow.value.copy(
                        isLoading = SingleUse(false),
                        archives = SingleUse(archives)
                    )
                )
            }
        }
    }

    private fun getArchiveFromDatabase(id: Int) {
        viewModelScope.launch {
            val response = databaseRepository.getArchive(id)
            response.ifNotSuccessful {
                _stateFlow.emit(
                    stateFlow.value.copy(
                        message = SingleUse(it.toString())
                    )
                )
            }
            response.ifSuccessful { archive ->
                // TODO: Handle archive
            }
        }
    }

    private fun deleteArchiveFromDatabase(archive: ArchiveModel) {
        viewModelScope.launch {
            databaseRepository.deleteArchive(archive.id)
        }
    }
}