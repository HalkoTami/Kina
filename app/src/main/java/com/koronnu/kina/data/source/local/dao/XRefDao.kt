package com.koronnu.kina.data.source.local.dao

import androidx.room.*
import com.koronnu.kina.data.source.local.entity.XRef
@Dao
abstract class XRefDao: BaseDao<XRef> {
    @Query("DELETE FROM tbl_x_ref")
    abstract suspend fun clearTblXRef()
}

