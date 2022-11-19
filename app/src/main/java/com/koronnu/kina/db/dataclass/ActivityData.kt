package com.koronnu.kina.db.dataclass

import androidx.room.*
import com.koronnu.kina.db.enumclass.ActivityStatus
import com.koronnu.kina.db.enumclass.DBTable
import com.koronnu.kina.db.typeConverters.ActivityStatusConverter
import com.koronnu.kina.db.typeConverters.DBTableConverter

@Entity(tableName = "tbl_activity_data")
@TypeConverters(
    ActivityStatusConverter::class,
    DBTableConverter::class)
data class ActivityData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var activityTokenId:Int?= null,
    var idTokenTable:DBTable? = DBTable.TABLE_ACTIVITY_DATA,
    val activityStatus: ActivityStatus,
    val dateTime: String
)
