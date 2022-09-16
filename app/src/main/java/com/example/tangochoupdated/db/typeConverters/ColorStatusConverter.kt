package com.example.tangochoupdated.db.typeConverters

import androidx.room.TypeConverter
import com.example.tangochoupdated.db.enumclass.CardStatus
import com.example.tangochoupdated.db.enumclass.ColorStatus


class ColorStatusConverter {
    @TypeConverter
    fun toColorStatus(value: Int): ColorStatus = enumValues<ColorStatus>()[value]

    @TypeConverter
    fun fromColorStatus(value: ColorStatus): Int = value.ordinal
}