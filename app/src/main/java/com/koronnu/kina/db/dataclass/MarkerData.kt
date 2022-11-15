package com.koronnu.kina.db.dataclass

import androidx.room.*
import com.koronnu.kina.db.enumclass.ColorStatus
import com.koronnu.kina.db.typeConverters.ColorStatusConverter

@Entity(tableName = "tbl_marker_data")
@TypeConverters(ColorStatusConverter::class)
data class MarkerData(
    @PrimaryKey
    val markerId: Int,
    val cardId: Int,
    @ColumnInfo
    var order:Int,
    var text:String?,
    var marked:Boolean,
    var markerColor: ColorStatus,
    var remembered: Boolean

)
