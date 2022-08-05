package com.example.tangochoupdated.db.dataclass

import androidx.room.*
import com.example.tangochoupdated.db.enumclass.ActivityStatus
import com.example.tangochoupdated.db.enumclass.ActivityStatusConverter

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
