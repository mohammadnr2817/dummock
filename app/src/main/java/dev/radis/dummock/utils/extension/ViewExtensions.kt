package dev.radis.dummock.utils.extension

import android.view.View

fun View.fadeVisible() {
    this.animate().alpha(1.0f)
}

fun View.fadeInVisible() {
    this.animate().alpha(0.0f)
}