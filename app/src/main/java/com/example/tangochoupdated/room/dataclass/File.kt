package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.ColorStatusConverter
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.FileStatusConverter
import com.example.tangochoupdated.room.rvclasses.LibraryRV

@Entity(tableName = "tbl_file",
    foreignKeys = [ForeignKey(
        entity = File::class,
        parentColumns = arrayOf("parent_file_id"),
        childColumns = arrayOf("file_id"),
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.CASCADE
    )]
)
@TypeConverters(ColorStatusConverter::class,FileStatusConverter::class)
data class File(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "file_id") val fileId:Int,
    @Embedded( prefix = "parent")
    val parentFile: File?,
    @ColumnInfo(defaultValue = "title") val  title: String?,
    @ColumnInfo val deleted: Boolean?,
    @ColumnInfo var colorStatus: ColorStatus,
    @ColumnInfo var fileStatus: FileStatus,
    @ColumnInfo(name= "library_order")
    val libOrder: Int,
    @ColumnInfo(name = "containing_folders_amount")
    val containingFolderAmount:Int,
    @ColumnInfo(name = "containing_flash_card_covers_amount")
    val containingFlashCardCoverAmount: Int,
    @ColumnInfo(name = "containing_cards_amount")
    val containingCardAmount:Int,




    )




