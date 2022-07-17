package dev.radis.dummock.view.state

import android.content.Intent
import dev.radis.dummock.model.entity.db.ArchiveModel
import dev.radis.dummock.utils.SingleUse
import dev.radis.dummock.utils.mvi.MviState

data class ArchiveState(
    val isLoading: SingleUse<Boolean>? = null,
    val message: SingleUse<String>? = null,
    val archives: SingleUse<List<ArchiveModel>> = SingleUse(emptyList()),
    val executeIntent: SingleUse<Pair<Intent, Boolean>>? = null
) : MviState