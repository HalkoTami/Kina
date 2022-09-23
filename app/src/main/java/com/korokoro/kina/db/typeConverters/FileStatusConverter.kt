package com.korokoro.kina.db.typeConverters

import androidx.room.TypeConverter
import com.korokoro.kina.db.enumclass.FileStatus


class FileStatusConverter {
    @TypeConverter
    fun toFileStatus(value: Int): FileStatus = enumValues<FileStatus>()[value]

    @TypeConverter
    fun fromFileStatus(value: FileStatus): Int = value.ordinal
}