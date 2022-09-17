package com.example.tangochoupdated.db.typeConverters

import androidx.room.TypeConverter
import com.example.tangochoupdated.db.enumclass.DBTable


class DBTableConverter {
    @TypeConverter
    fun toDBTable(value: Int): DBTable = enumValues<DBTable>()[value]

    @TypeConverter
    fun fromDBTable(value: DBTable): Int = value.ordinal
}