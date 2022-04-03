package dev.radis.dummock.utils.extension

import dev.radis.dummock.utils.constants.StringConstants.PERSIAN_DIGITS

fun String.toPersianDigits(): String {
    var result = this
    for (i in 0 until 9)
        result = result.replace(i.toChar(), PERSIAN_DIGITS[i])
    return result
}