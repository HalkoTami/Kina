package com.koronnu.kina.data.source.local.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.koronnu.kina.actions.DateTimeActions
import com.koronnu.kina.data.source.local.entity.ActivityData
import com.koronnu.kina.data.source.local.entity.enumclass.ActivityStatus
import com.koronnu.kina.data.source.local.typeConverters.ActivityStatusConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

@Dao
abstract class ActivityDataDao: BaseDao<ActivityData> {
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

    @RawQuery(observedEntities = [ActivityData::class])
    abstract fun calculateSingleActivityDataInternal(query: SupportSQLiteQuery) : Flow<ActivityData>

    fun getLastFlipDuration(): Flow<Int> {
        val statusFlipRoundStarted = ActivityStatusConverter().fromActivityStatus(ActivityStatus.FLIP_ROUND_STARTED)
        val query = "SELECT * FROM tbl_activity_data a " +
                " INNER JOIN ( SELECT  MAX(id) id FROM tbl_activity_data c " +
                "where c.activityStatus = "+ statusFlipRoundStarted +
                " ) b " +
                "ON a.id = b.id "
        val simpleSQLiteQuery = SimpleSQLiteQuery(query, arrayOf<ActivityData>())
        return calculateSingleActivityDataInternal(simpleSQLiteQuery).map {
            val startedDate: Date = DateTimeActions().fromStringToDate(it.dateTime)!!
            val now = Date()
            DateTimeActions().getTimeDifference(startedDate, now, DateTimeActions.TimeUnit.SECONDS)
        }
    }

}