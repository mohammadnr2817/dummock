package dev.radis.dummock.utils

import android.util.Log

object Logit {
    private const val LOGIT_TAG = "logit"

    /**
     * @param message
     */
    fun d(message: Any) {
        Log.d(LOGIT_TAG, message.toString())
    }

}