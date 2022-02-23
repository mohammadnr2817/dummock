package dev.radis.dummock.utils.response

sealed class Response<out S, out E> {

    fun isSuccessful() = this is Success

    fun isNotSuccessful() = this is Failure

    inline fun ifSuccessful(function: () -> Unit) {
        if (isSuccessful()) function.invoke()
    }

    inline fun ifNotSuccessful(function: () -> Unit) {
        if (isNotSuccessful()) function.invoke()
    }

}