package dev.radis.dummock.model.dataSource

interface Writer<O> {
    fun write(): O

    interface IO<I, O> {
        fun write(input: I): O

        interface Suspend<I, O> {
            suspend fun write(input: I): O
        }
    }
}