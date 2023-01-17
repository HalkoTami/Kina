package com.koronnu.kina.db.dao

import androidx.room.*
import com.koronnu.kina.db.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ActivityDataDao: BaseDao<ActivityData>{
    @Query("SELECT * FROM tbl_activity_data  WHERE activityTokenId = :cardId")
    abstract fun getActivityDataByCard(cardId: Int):Flow<List<ActivityData>>
    @Query("SELECT * FROM tbl_activity_data  ")
    abstract fun getAllActivityData():Flow<List<ActivityData>>
    @Query("DELETE FROM tbl_activity_data")
    abstract suspend fun clearTblActivity()

    @Query("SELECT * FROM tbl_activity_data a " +
            " INNER JOIN ( SELECT  MAX(id) id FROM tbl_activity_data c " +
            "where c.activityStatus = :statusFlipRoundStarted ) b " +
            "ON a.id = b.id ")
    abstract fun getLastActivityStartedData(statusFlipRoundStarted:Int):Flow<ActivityData>


}