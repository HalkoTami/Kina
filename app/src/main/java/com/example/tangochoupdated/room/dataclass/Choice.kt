package com.example.tangochoupdated.room.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_choice",
    foreignKeys = [ForeignKey(
        entity = Card::class,
        parentColumns = arrayOf("card_id"),
        childColumns = arrayOf("choice_belonging_card_id"),
        onDelete = ForeignKey.CASCADE
    )])
data class Choice(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "choice_belonging_card_id")
    val belongingCardId: Int?,
    @ColumnInfo(name = "choice_id")
    val id: Int,
    @ColumnInfo
    val text:String?,

    )