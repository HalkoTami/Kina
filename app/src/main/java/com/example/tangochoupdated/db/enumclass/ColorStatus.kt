package com.example.tangochoupdated.db.enumclass

import androidx.room.TypeConverter

enum class ColorStatus(value: Int) {
    GRAY(0),
    RED(1),
    BLUE(2),
    YELLOW(3)
}

class ColorStatusConverter {
    @TypeConverter
    fun toColorStatus(value: Int): ColorStatus = enumValues<ColorStatus>()[value]

    @TypeConverter
    fun fromColorStatus(value: ColorStatus): Int = value.ordinal
}