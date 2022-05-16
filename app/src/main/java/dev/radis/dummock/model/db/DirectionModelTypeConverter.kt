package dev.radis.dummock.model.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import dev.radis.dummock.model.entity.DirectionModel


class DirectionModelTypeConverter {
    var gson = Gson()

    @TypeConverter
    fun stringToDirectionModel(data: String?): DirectionModel? {
        if (data == null) {
            return null
        }
        return gson.fromJson(data, DirectionModel::class.java)
    }

    @TypeConverter
    fun directionModelToString(model: DirectionModel?): String {
        return gson.toJson(model)
    }

}