package com.example.tangochoupdated.room.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.tangochoupdated.Card
import com.example.tangochoupdated.ColorStatus

@Entity(tableName = "marker_data",
    foreignKeys = arrayOf(
        ForeignKey(entity = Card::class,
        parentColumns = arrayOf("card_id"),
        childColumns = arrayOf("marker_data_belonging_card_id"),
        onDelete = ForeignKey.CASCADE)
    ))
data class MarkerData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "marker_data_id")
    val id: Int,
    @ColumnInfo(name = "marker_data_belonging_card_id")
    val cardid: Int,
    @ColumnInfo
    val text:String?,
    val markered:Boolean,
    var markercolor: ColorStatus

)
