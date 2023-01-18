package com.koronnu.kina.data.source.local.typeConverters

import androidx.room.TypeConverter
import com.koronnu.kina.data.source.local.entity.enumclass.ColorStatus


class ColorStatusConverter {
    @TypeConverter
    fun toColorStatus(value: Int): ColorStatus = enumValues<ColorStatus>()[value]

    @TypeConverter
    fun fromColorStatus(value: ColorStatus): Int = value.ordinal
}