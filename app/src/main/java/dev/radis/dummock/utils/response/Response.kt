package dev.radis.dummock.utils.response

sealed class Response<out S, out E> {

    fun isSuccessful() = this is Success

    fun isNotSuccessful() = this is Failure

    inline fun ifSuccessful(function: (response: S) -> Unit) {
        if (isSuccessful()) function.invoke((this as Success).response)
    }

    inline fun ifNotSuccessful(function: (error: E) -> Unit) {
        if (isNotSuccessful()) function.invoke((this as Failure).error)
    }

}

data class Success<out S>(val response: S) : Response<S, Nothing>()

data class Failure<out E>(val error: E) : Response<Nothing, E>()