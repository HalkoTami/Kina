package com.example.tangochoupdated.room.dataclass

import androidx.room.*
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
abstract class ChoiceDao: DataAccessObject<Choice>{
    @Query("DELETE FROM tbl_choice")
    abstract suspend fun clearTblChoice()
}
