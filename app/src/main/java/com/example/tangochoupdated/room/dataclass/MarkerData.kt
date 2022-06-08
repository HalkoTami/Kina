package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.ColorStatusConverter

@Entity(tableName = "tbl_marker_data",
    indices = [Index("id", unique = true),
              Index("cardId", unique = true)],
    foreignKeys =[ForeignKey(entity = Card::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("cardId"),
        onDelete = ForeignKey.CASCADE)])
@TypeConverters(ColorStatusConverter::class)
data class MarkerData(
    @PrimaryKey
    val id: Int,
    val cardId: Int,
    @ColumnInfo
    var text:String?,
    var marked:Boolean,
    var markerColor: ColorStatus,
    var remembered: Boolean

)
