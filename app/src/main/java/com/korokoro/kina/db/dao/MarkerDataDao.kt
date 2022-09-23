package com.korokoro.kina.db.dao

import androidx.room.*
import com.korokoro.kina.db.dataclass.*
@Dao
abstract class MarkerDataDao: BaseDao<MarkerData>{
    @Query("DELETE FROM tbl_marker_data")
    abstract suspend fun clearTblMarkerData()
}

