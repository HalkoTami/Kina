package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.ColorStatusConverter
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.FileStatusConverter
import com.example.tangochoupdated.room.rvclasses.LibraryRV

@Entity(tableName = "tbl_file",
    indices = [Index("fileId", unique = true, name = "file_id"),
              Index("parentFile", unique = true, name = "parent_file_id")],
    foreignKeys = [ForeignKey(
        entity = File::class,
        parentColumns = arrayOf("parentFile"),
        childColumns = arrayOf("fileId"),
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.CASCADE
    )]
)
@TypeConverters(ColorStatusConverter::class,FileStatusConverter::class)
data class File(
    @PrimaryKey
    var fileId:Int,
    var parentFile: Int,
    @ColumnInfo(defaultValue = "title") var  title: String?,
    @ColumnInfo var deleted: Boolean?,
    @ColumnInfo var colorStatus: ColorStatus,
    @ColumnInfo var fileStatus: FileStatus,
    @ColumnInfo(name= "library_order")
    var libOrder: Int,





    )

