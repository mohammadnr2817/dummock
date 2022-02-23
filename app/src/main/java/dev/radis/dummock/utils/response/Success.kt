package dev.radis.dummock.utils.response

data class Success<out S>(val response: S) : Response<S, Nothing>()