package dev.radis.dummock.utils.response

data class Failure<out E>(val error: E) : Response<Nothing, E>()