package com.korokoro.kina.db.dao

import androidx.room.*
import com.korokoro.kina.db.dataclass.*
@Dao
abstract class XRefDao: BaseDao<XRef>{
    @Query("DELETE FROM tbl_x_ref")
    abstract suspend fun clearTblXRef()
}

