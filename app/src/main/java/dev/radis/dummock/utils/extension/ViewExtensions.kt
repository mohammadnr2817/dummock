package dev.radis.dummock.utils.extension

import android.content.res.Resources
import android.util.TypedValue
import android.view.View

fun View.fadeVisible() {
    this.animate().alpha(1.0f)
}

fun View.fadeInVisible() {
    this.animate().alpha(0.0f)
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )