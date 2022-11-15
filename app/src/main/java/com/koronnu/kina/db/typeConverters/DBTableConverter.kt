package com.koronnu.kina.db.typeConverters

import androidx.room.TypeConverter
import com.koronnu.kina.db.enumclass.DBTable


class DBTableConverter {
    @TypeConverter
    fun toDBTable(value: Int): DBTable = enumValues<DBTable>()[value]

    @TypeConverter
    fun fromDBTable(value: DBTable): Int = value.ordinal
}