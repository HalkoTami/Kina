package com.example.tangochoupdated.db.dao

import androidx.room.*
import com.example.tangochoupdated.db.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FileXRefDao: BaseDao<FileXRef>{
    @Query("DELETE FROM file_xref")
    abstract suspend fun clearTblFileXRef()
}

