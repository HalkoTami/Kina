package com.example.tangochoupdated.db.dataclass

import androidx.room.*
import com.example.tangochoupdated.db.enumclass.ActivityStatus
import com.example.tangochoupdated.db.enumclass.DBTable
import com.example.tangochoupdated.db.enumclass.XRefType
import com.example.tangochoupdated.db.typeConverters.ActivityStatusConverter
import com.example.tangochoupdated.db.typeConverters.DBTableConverter
import com.example.tangochoupdated.db.typeConverters.XRefTypeConverter

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
