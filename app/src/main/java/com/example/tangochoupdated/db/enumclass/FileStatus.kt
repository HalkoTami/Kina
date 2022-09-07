package com.example.tangochoupdated.db.enumclass

import androidx.room.TypeConverter

enum class FileStatus(Value:Int) {
    TANGO_CHO_COVER(0),
    FOLDER(1),
    TAG(2),
    ANKI_BOX_FAVOURITE(3)
}


class FileStatusConverter {
    @TypeConverter
    fun toFileStatus(value: Int): FileStatus = enumValues<FileStatus>()[value]

    @TypeConverter
    fun fromFileStatus(value: FileStatus): Int = value.ordinal
}
