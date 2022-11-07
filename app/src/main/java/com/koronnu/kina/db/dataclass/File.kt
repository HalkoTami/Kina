package com.koronnu.kina.db.dataclass

import androidx.room.*
import com.koronnu.kina.db.enumclass.ColorStatus
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.db.typeConverters.ColorStatusConverter
import com.koronnu.kina.db.typeConverters.FileStatusConverter

@Entity(tableName = "tbl_file",
)
@TypeConverters(ColorStatusConverter::class,FileStatusConverter::class)
data class File(
    @PrimaryKey(autoGenerate = true)
    val fileId:Int,
    @ColumnInfo var  title: String? = null,
    var deleted: Boolean? = false,
    var colorStatus: ColorStatus = ColorStatus.GRAY,
    var fileStatus: FileStatus,
    var parentFileId: Int? = null,
    var fileBefore: Int? = null,



    )





