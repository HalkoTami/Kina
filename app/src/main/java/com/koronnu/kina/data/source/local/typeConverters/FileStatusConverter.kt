package com.koronnu.kina.data.source.local.typeConverters

import androidx.room.TypeConverter
import com.koronnu.kina.data.source.local.entity.enumclass.FileStatus


class FileStatusConverter {
    @TypeConverter
    fun toFileStatus(value: Int): FileStatus = enumValues<FileStatus>()[value]

    @TypeConverter
    fun fromFileStatus(value: FileStatus): Int = value.ordinal
}