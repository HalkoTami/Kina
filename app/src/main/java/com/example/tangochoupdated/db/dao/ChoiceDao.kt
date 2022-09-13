package com.example.tangochoupdated.db.dao

import androidx.room.*
import com.example.tangochoupdated.db.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ChoiceDao(): BaseDao<Choice> {

    @Query("DELETE FROM tbl_choice")
    abstract suspend fun clearTblChoice()
}

