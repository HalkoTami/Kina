package com.example.tangochoupdated.db.dao

import androidx.room.*
import com.example.tangochoupdated.db.dataclass.*
@Dao
abstract class MarkerDataDao: BaseDao<MarkerData>{
    @Query("DELETE FROM tbl_marker_data")
    abstract suspend fun clearTblMarkerData()
}

