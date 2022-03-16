package dev.radis.dummock.utils

class SingleUse<T>(val value: T) {
    private var hasUsedBefore = false

    fun ifNotUsedBefore(): T? {
        if (!hasUsedBefore) {
            hasUsedBefore = true
            return value
        }
        return null
    }

}