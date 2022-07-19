package dev.radis.dummock.utils.constants

import androidx.annotation.StringDef
import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_BIKE
import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_CAR

object StringConstants {
    const val BASE_DIRECTION_URL = "https://api.neshan.org/"
    const val APP_NAME = "DUMMOCK"
    const val ARCHIVE_DATABASE_NAME = "dummock_archive_database"
    const val ARCHIVE_TABLE_NAME = "dummock_archive_table"
    const val DIALOG_OK = "Ok"
    const val DIALOG_CANCEL = "Cancel"
    const val DUMMOCK_TAG = "DUMMOCK_TAG"
    const val DUMMOCK_EXTENSION = ".dmmc"
    const val DIRECTION_TYPE_CAR = "car"
    const val DIRECTION_TYPE_BIKE = "motorcycle"
    const val PERSIAN_DIGITS = "۰۱۲۳۴۵۶۷۸۹"
    const val COPIED = "کپی شد!"
    const val PROVIDER_GPS = "gps"
    const val FOREGROUND_NOTIFICATION_CHANNEL_ID = "LocationServiceId"
    const val FOREGROUND_NOTIFICATION_CHANNEL_NAME = "Location Service"
    const val FOREGROUND_NOTIFICATION_CONTENT_TITLE = "Dummock is mocking locations!"
    const val FOREGROUND_NOTIFICATION_ACTION_STOP = "Stop"
    const val PREFERENCES_SPEED_KEY = "speed_key"
    const val DATE_FORMAT = "yyyy.MM.dd HH:mm:ss"
    const val MOVE_SPEED_FA = " کیلومتر بر ساعت"
}

@StringDef(DIRECTION_TYPE_CAR, DIRECTION_TYPE_BIKE)
@Retention(AnnotationRetention.SOURCE)
annotation class DirectionType