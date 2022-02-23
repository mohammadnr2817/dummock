package dev.radis.dummock.model.dataSource

interface Reader<O> {
    fun read(): O

    interface IO<I, O> {
        fun read(input: I): O

        interface Suspend<I, O> {
            suspend fun read(input: I): O
        }
    }
}