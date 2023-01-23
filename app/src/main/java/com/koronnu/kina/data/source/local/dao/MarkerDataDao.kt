package com.koronnu.kina.data.source.local.dao

import androidx.room.*
import com.koronnu.kina.data.source.local.entity.MarkerData
@Dao
abstract class MarkerDataDao: BaseDao<MarkerData> {
    @Query("DELETE FROM tbl_marker_data")
    abstract suspend fun clearTblMarkerData()
}

