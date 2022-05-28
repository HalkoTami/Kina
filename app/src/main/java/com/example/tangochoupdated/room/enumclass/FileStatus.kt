package com.example.tangochoupdated.room.enumclass

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

enum class FileStatus(Value:Int) {
    TANGO_CHO(0),
    FILE(1)
}


@ProvidedTypeConverter
class FileStatusConverter {
    @TypeConverter
    fun toFileStatus(value: Int): FileStatus = enumValues<FileStatus>()[value]

    @TypeConverter
    fun fromFileStatus(value: FileStatus): Int = value.ordinal
}
