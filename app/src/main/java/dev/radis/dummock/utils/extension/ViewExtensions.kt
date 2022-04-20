package dev.radis.dummock.utils.extension

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.TypedValue
import android.view.View
import android.view.animation.BounceInterpolator

fun View.fadeVisible() {
    this.animate().alpha(1.0f)
    this.isEnabled = true
}

fun View.fadeInVisible() {
    this.animate().alpha(0.0f)
    this.isEnabled = false
}

fun View.bounceAnimation(duration: Long) {
    val valueAnimator = ValueAnimator.ofFloat(0F, 1F)
    valueAnimator.duration = duration
    valueAnimator.interpolator = BounceInterpolator()
    valueAnimator.addUpdateListener {
        if (it.animatedFraction < 0.5)
            this.translationY = -1 * it.animatedFraction * 2 * 24.toPx
        else
            this.translationY = -1 * (1 - it.animatedFraction) * 2 * 24.toPx
    }
    valueAnimator.start()
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