package dev.radis.dummock.utils.mvi

import kotlinx.coroutines.flow.Flow

interface MviModel<I : MviIntent, S : MviState> {

    val stateFlow: Flow<S>

    fun handleIntent(intent: I)

}