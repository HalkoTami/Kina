package com.koronnu.kina.data.source.local.typeConverters

import androidx.room.TypeConverter
import com.koronnu.kina.data.source.local.entity.enumclass.XRefType


class XRefTypeConverter {
    @TypeConverter
    fun toXRefType(value: Int): XRefType = enumValues<XRefType>()[value]

    @TypeConverter
    fun fromXRefType(value: XRefType): Int = value.ordinal
}