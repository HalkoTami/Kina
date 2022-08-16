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
    var libOrder: Int? = 0,
    @Embedded
    var childData: ChildData = ChildData(),
    @Embedded
    var descendantsData: DescendantsData = DescendantsData(),
    @Embedded
    var flippedData:AnkiBoxFlippedData = AnkiBoxFlippedData(),
    var rememberedCardAmount:Int = 0,



    )

    data class ChildData(
        var childFoldersAmount:Int = 0,
        var childFlashCardCoversAmount: Int = 0,
        var childCardsAmount:Int = 0
    )
data class DescendantsData(
    var descendantsFoldersAmount:Int = 0,
    var descendantsFlashCardsCoversAmount: Int = 0,
    var descendantsCardsAmount:Int = 0
)



    data class AnkiBoxFlippedData(
        var flippedNeverAmount:Int = 0,
        var flippedOnceAmount:Int = 0,
        var flippedTwiceAmount:Int = 0,
        var flippedThreeTimesAmount:Int = 0 ,
        var flippedFourTimesAmount:Int = 0 ,
        var flippedFiveTimesAmount:Int = 0 ,
    )


@Entity(
    tableName = "file_xref",
)
data class FileXRef(
    @PrimaryKey(autoGenerate = true) val id:Int,
    val parentFileId: Int?,
    val childFileId: Int,
)


