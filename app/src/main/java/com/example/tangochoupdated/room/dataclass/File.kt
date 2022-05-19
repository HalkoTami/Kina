package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.DataAccessObject
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.ColorStatusConverter

@Entity(tableName = "tbl_file",
    foreignKeys = [ForeignKey(
        entity = File::class,
        parentColumns = arrayOf("file_id"),
        childColumns = arrayOf("belonging_file_id"),
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.CASCADE
    )]
)
@TypeConverters(ColorStatusConverter::class)
data class File(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "file_id") val fileId:Int,
    @ColumnInfo( name = "belonging_file_id")
    val belongingFileId: Int?,
    @ColumnInfo(defaultValue = "title") val  title: String?,
    @ColumnInfo val deleted: Boolean?,
    @ColumnInfo var colorStatus: ColorStatus?,


    )

@Dao
abstract class FileDao: DataAccessObject<File>{

    @Transaction
    @Query("select * from tbl_file")
    abstract fun loadFileAndCard(): List<FileAndCards>
}

