package com.example.tangochoupdated.db.dataclass

import androidx.room.*
import com.example.tangochoupdated.db.enumclass.ActivityStatus
import com.example.tangochoupdated.db.enumclass.ActivityStatusConverter

@Entity(tableName = "tbl_activity_data")
@TypeConverters(ActivityStatusConverter::class)
data class ActivityData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var cardId:Int?= null,
    var fileId:Int?= null,
    val activityStatus: ActivityStatus,
    val dateTime: String
)
