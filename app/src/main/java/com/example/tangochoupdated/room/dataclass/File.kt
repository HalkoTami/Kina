package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.ColorStatus
import com.example.tangochoupdated.ColorStatusConverter
import com.example.tangochoupdated.User

@Entity(tableName = "tbl_file",
    foreignKeys = arrayOf(
        ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE
    )
    )
)
@TypeConverters(ColorStatusConverter::class)
data class File(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "file_id") val fileid:Int,
    @ColumnInfo(name = "user_id") val userid:Int,
    @Embedded(prefix = "belonging",)
    val belongingFile: File?,
    @ColumnInfo(defaultValue = "title") val  title: String?,
    @ColumnInfo val deleted: Boolean?,
    @ColumnInfo var colorStatus: ColorStatus?,


    )