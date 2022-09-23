package com.korokoro.kina.db.typeConverters

import androidx.room.TypeConverter
import com.korokoro.kina.db.enumclass.ActivityStatus

class ActivityStatusConverter {
    @TypeConverter
    fun toActivityStatus(value: Int): ActivityStatus = enumValues<ActivityStatus>()[value]

    @TypeConverter
    fun fromActivityStatus(value: ActivityStatus): Int = value.ordinal
}