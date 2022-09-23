package com.korokoro.kina.db.dataclass

import androidx.room.*
import com.korokoro.kina.db.enumclass.ColorStatus
import com.korokoro.kina.db.typeConverters.ColorStatusConverter

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
