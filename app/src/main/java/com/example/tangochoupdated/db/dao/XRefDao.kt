package com.example.tangochoupdated.db.dao

import androidx.room.*
import com.example.tangochoupdated.db.dataclass.*
@Dao
abstract class XRefDao: BaseDao<XRef>{
    @Query("DELETE FROM tbl_x_ref")
    abstract suspend fun clearTblXRef()
}

