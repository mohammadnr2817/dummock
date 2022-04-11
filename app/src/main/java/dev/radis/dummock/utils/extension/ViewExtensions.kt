package dev.radis.dummock.utils.extension

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.TypedValue
import android.view.View

fun View.fadeVisible() {
    this.animate().alpha(1.0f)
}

fun View.fadeInVisible() {
    this.animate().alpha(0.0f)
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )