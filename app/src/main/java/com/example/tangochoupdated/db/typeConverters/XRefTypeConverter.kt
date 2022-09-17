package com.example.tangochoupdated.db.typeConverters

import androidx.room.TypeConverter
import com.example.tangochoupdated.db.enumclass.XRefType


class XRefTypeConverter {
    @TypeConverter
    fun toXRefType(value: Int): XRefType = enumValues<XRefType>()[value]

    @TypeConverter
    fun fromXRefType(value: XRefType): Int = value.ordinal
}