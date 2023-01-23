package com.koronnu.kina.data.source.local.dao

import androidx.room.*
import com.koronnu.kina.data.source.local.entity.Choice
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ChoiceDao: BaseDao<Choice> {
    @Query("DELETE FROM tbl_choice")
    abstract suspend fun clearTblChoice()

    @Query("select * from tbl_choice where id = :choiceId ")
    abstract fun getChoicesById(choiceId: Int ): Flow<List<Choice>>
}

