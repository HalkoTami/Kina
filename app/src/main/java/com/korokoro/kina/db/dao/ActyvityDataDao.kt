package com.korokoro.kina.db.dao

import androidx.room.*
import com.korokoro.kina.db.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ActivityDataDao: BaseDao<ActivityData>{
    @Query("SELECT * FROM tbl_activity_data  WHERE activityTokenId = :cardId")
    abstract fun getActivityDataByCard(cardId: Int):Flow<List<ActivityData>>
    @Query("DELETE FROM tbl_activity_data")
    abstract suspend fun clearTblActivity()
}