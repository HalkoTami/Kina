package com.example.tangochoupdated.db.typeConverters

import androidx.room.TypeConverter
import com.example.tangochoupdated.db.enumclass.ActivityStatus

class ActivityStatusConverter {
    @TypeConverter
    fun toActivityStatus(value: Int): ActivityStatus = enumValues<ActivityStatus>()[value]

    @TypeConverter
    fun fromActivityStatus(value: ActivityStatus): Int = value.ordinal
}