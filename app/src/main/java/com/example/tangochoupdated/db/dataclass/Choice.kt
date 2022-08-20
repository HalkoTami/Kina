package com.example.tangochoupdated.db.dataclass

import androidx.room.*
import com.example.tangochoupdated.db.dao.BaseDao
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tbl_choice")
data class Choice(
    @PrimaryKey
    val id: Int,
    val belongingCardId: Int?,
    @ColumnInfo
    var text:String?,

    )
@Dao
abstract class ChoiceDao: BaseDao<Choice> {
    @Query("DELETE FROM tbl_choice")
    abstract suspend fun clearTblChoice()

    @Query("select * from tbl_choice where id = :choiceId ")
    abstract fun getChoicesById(choiceId: Int ): Flow<List<Choice>>
}
