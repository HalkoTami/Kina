package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.DataAccessObject
import com.example.tangochoupdated.room.enumclass.ActivityStatus
import java.time.LocalDateTime

@Entity(tableName = "tbl_activity_data")
@TypeConverters(ActivityStatus::class)
data class ActivityData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activity_id")
    val id: Int,
    @ColumnInfo(name = "occurred_card_id")
    val cardId:Int?,
    @ColumnInfo(name = "occurred_file_id")
    val fileId:Int?,
    @ColumnInfo(name = "activity_status")
    var activityStatus: ActivityStatus,
    @ColumnInfo(name = "activity_daytime")
    val dateTime: LocalDateTime
)
@Dao
abstract class ActivityDataDao: DataAccessObject<ActivityData>{
    @Query("DELETE FROM tbl_activity_data")
    abstract suspend fun clearTblActivity()
}
