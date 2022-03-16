package dev.radis.dummock.utils.mvi

interface MviView<S : MviState> {

    fun renderState(state: S)

}