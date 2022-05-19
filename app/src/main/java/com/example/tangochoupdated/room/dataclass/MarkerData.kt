package com.example.tangochoupdated.room.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.tangochoupdated.room.enumclass.ColorStatus

@Entity(tableName = "marker_data",
    foreignKeys =[ForeignKey(entity = Card::class,
        parentColumns = arrayOf("card_id"),
        childColumns = arrayOf("marker_data_belonging_card_id"),
        onDelete = ForeignKey.CASCADE)])
data class MarkerData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "marker_data_id")
    val id: Int,
    @ColumnInfo(name = "marker_data_belonging_card_id")
    val cardId: Int,
    @ColumnInfo
    val text:String?,
    val marked:Boolean,
    var markerColor: ColorStatus,
    val remembered: Boolean

)
