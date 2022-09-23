package com.korokoro.kina.db.dataclass

import androidx.room.*
import com.korokoro.kina.db.enumclass.ActivityStatus
import com.korokoro.kina.db.enumclass.DBTable
import com.korokoro.kina.db.typeConverters.ActivityStatusConverter
import com.korokoro.kina.db.typeConverters.DBTableConverter

@Entity(tableName = "tbl_activity_data")
@TypeConverters(
    ActivityStatusConverter::class,
    DBTableConverter::class)
data class ActivityData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var activityTokenId:Int?= null,
    var idTokenTable:DBTable? = null,
    val activityStatus: ActivityStatus,
    val dateTime: String
)
