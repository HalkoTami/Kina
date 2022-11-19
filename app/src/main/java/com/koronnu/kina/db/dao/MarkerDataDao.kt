package com.koronnu.kina.db.dao

import androidx.room.*
import com.koronnu.kina.db.dataclass.*
@Dao
abstract class MarkerDataDao: BaseDao<MarkerData>{
    @Query("DELETE FROM tbl_marker_data")
    abstract suspend fun clearTblMarkerData()
}

