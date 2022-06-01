package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.ColorStatusConverter
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.FileStatusConverter

@Entity(tableName = "tbl_file",
    foreignKeys = [ForeignKey(
        entity = File::class,
        parentColumns = arrayOf("file_id"),
        childColumns = arrayOf("belonging_file_id"),
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.CASCADE
    )]
)
@TypeConverters(ColorStatusConverter::class,FileStatusConverter::class)
data class File(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "file_id") val fileId:Int,
    @ColumnInfo( name = "file_belonging_file_id")
    val belongingFileId: Int?,
    @ColumnInfo(defaultValue = "title") val  title: String?,
    @ColumnInfo val deleted: Boolean?,
    @ColumnInfo var colorStatus: ColorStatus,
    @ColumnInfo var fileStatus: FileStatus,
    @ColumnInfo(name= "library_order")
    val libOrder: Int


    )

