package com.example.tangochoupdated.room.dataclass

import androidx.room.*
import com.example.tangochoupdated.room.enumclass.ActivityStatus
import com.example.tangochoupdated.room.enumclass.ActivityStatusConverter
import java.time.LocalDateTime

@Entity(tableName = "tbl_activity_data")
@TypeConverters(ActivityStatusConverter::class)
data class ActivityData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activity_id")
    val id: Int,
    @ColumnInfo(name = "occurred_card_id")
    var cardId:Int?,
    @ColumnInfo(name = "occurred_file_id")
    var fileId:Int?,
    @ColumnInfo(name = "activity_status")
    var activityStatus: ActivityStatus,
    @ColumnInfo(name = "activity_daytime")
    var dateTime: Int
)
