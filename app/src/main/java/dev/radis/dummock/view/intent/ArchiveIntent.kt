package dev.radis.dummock.view.intent

import dev.radis.dummock.utils.mvi.MviIntent

sealed class ArchiveIntent : MviIntent {

    object LoadRoutesIntent : ArchiveIntent()

    data class SelectRouteIntent(val id: Int) : ArchiveIntent()

}