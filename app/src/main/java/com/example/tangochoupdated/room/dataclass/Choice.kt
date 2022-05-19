package com.example.tangochoupdated.room.dataclass

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tangochoupdated.room.DataAccessObject

@Entity(tableName = "tbl_choice")
data class Choice(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "choice_id")
    val id: Int,
    @ColumnInfo(name = "choice_belonging_card_id")
    val belongingCardId: Int?,
    @ColumnInfo
    val text:String?,

    )
@Dao
abstract class ChoiceDao: DataAccessObject<Choice>
