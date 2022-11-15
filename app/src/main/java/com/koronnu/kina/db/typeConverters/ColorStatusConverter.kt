package com.koronnu.kina.db.typeConverters

import androidx.room.TypeConverter
import com.koronnu.kina.db.enumclass.ColorStatus


class ColorStatusConverter {
    @TypeConverter
    fun toColorStatus(value: Int): ColorStatus = enumValues<ColorStatus>()[value]

    @TypeConverter
    fun fromColorStatus(value: ColorStatus): Int = value.ordinal
}