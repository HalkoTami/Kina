package com.koronnu.kina.db.dao

import androidx.room.*
import com.koronnu.kina.db.dataclass.*
@Dao
abstract class XRefDao: BaseDao<XRef>{
    @Query("DELETE FROM tbl_x_ref")
    abstract suspend fun clearTblXRef()
}

