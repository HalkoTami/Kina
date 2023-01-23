package com.koronnu.kina.data.source.local.typeConverters

import androidx.room.TypeConverter
import com.koronnu.kina.data.source.local.entity.enumclass.DBTable


class DBTableConverter {
    @TypeConverter
    fun toDBTable(value: Int): DBTable = enumValues<DBTable>()[value]

    @TypeConverter
    fun fromDBTable(value: DBTable): Int = value.ordinal
}