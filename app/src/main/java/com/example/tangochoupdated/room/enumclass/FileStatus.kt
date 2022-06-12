package com.example.tangochoupdated.room.enumclass

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

enum class FileStatus(Value:Int) {
    TANGO_CHO_COVER(0),
    FOLDER(1),
    TAG(3)
}


@ProvidedTypeConverter
class FileStatusConverter {
    @TypeConverter
    fun toFileStatus(value: Int): FileStatus = enumValues<FileStatus>()[value]

    @TypeConverter
    fun fromFileStatus(value: FileStatus): Int = value.ordinal
}
