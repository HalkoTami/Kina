package com.example.tangochoupdated.db.typeConverters

import androidx.room.TypeConverter
import com.example.tangochoupdated.db.enumclass.XRefType


class XRefTypeConverter {
    @TypeConverter
    fun toDBTableType(value: Int): XRefType = enumValues<XRefType>()[value]

    @TypeConverter
    fun fromDBTableType(value: XRefType): Int = value.ordinal
}