package com.example.tangochoupdated.db.dataclass

import androidx.room.*
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.ColorStatusConverter
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.FileStatusConverter

@Entity(tableName = "tbl_file",
)
@TypeConverters(ColorStatusConverter::class,FileStatusConverter::class)
data class File(
    @PrimaryKey(autoGenerate = true)
    var fileId:Int,
    @ColumnInfo(defaultValue = "title") var  title: String? = null,
    @ColumnInfo var deleted: Boolean? = false,
    @ColumnInfo var colorStatus: ColorStatus = ColorStatus.GRAY,
    @ColumnInfo var fileStatus: FileStatus,
    @ColumnInfo var parentFileId: Int?,
    @ColumnInfo(name= "library_order")
    var hasChild:Boolean = false,
    var hasParent:Boolean = false,
    var libOrder: Int? = 0 ,
    var childFoldersAmount:Int = 0,
    var childFlashCardCoversAmount: Int = 0,
    var childCardsAmount:Int = 0





    )
@Entity(
    tableName = "file_xref",
//    indices = [Index("parentFileId", unique = true),
//        Index("childFileId", unique = true)],
//
//    foreignKeys = [
//        ForeignKey(
//            entity = File::class,
//            parentColumns = arrayOf("fileId"),
//            childColumns = arrayOf("parentFileId")
//        ),
//        ForeignKey(
//            entity = File::class,
//            parentColumns = arrayOf("fileId"),
//            childColumns = arrayOf("childFileId")
//        )
//    ]
)
data class FileXRef(
    @PrimaryKey(autoGenerate = true) val id:Int,
    val parentFileId: Int?,
    val childFileId: Int,
)

//class FileWithChild {
//    @Embedded
//    lateinit var parentFile: File
//
//    @Relation(
//        parentColumn = "fileId",
//        entityColumn = "fileId",
//        associateBy = Junction(
//            value = FileXRef::class,
//            parentColumn = "parentFileId",
//            entityColumn = "childFileId"
//        )
//    )
//    lateinit var childFiles: List<File>
//}

