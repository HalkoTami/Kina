package com.example.tangochoupdated.room.enumclass

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

@ProvidedTypeConverter
class ActyvityStatusConverter {
    @TypeConverter
    fun toActivityStatus(value: Int): ActivityStatus = enumValues<ActivityStatus>()[value]

    @TypeConverter
    fun fromActivityStatus(value: ActivityStatus): Int = value.ordinal
}
enum class ActivityStatus(value: Int) {
    CREATED(0),
    EDITED(1),
    DELETED(2),
    LOOKED(3),
    RIGHTANSWERSELECTED(4),
    WRONGANSWERSELECTED(5),
    REMEMBERED(6),
    FORGOT(7)
}
