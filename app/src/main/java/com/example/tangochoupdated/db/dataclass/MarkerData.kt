package com.example.tangochoupdated.db.dataclass

import androidx.room.*
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.typeConverters.ColorStatusConverter

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
