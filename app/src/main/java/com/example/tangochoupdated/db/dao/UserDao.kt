package com.example.tangochoupdated.db.dao

import androidx.room.*
import com.example.tangochoupdated.db.dataclass.*
@Dao
abstract class UserDao: BaseDao<User>{
    @Query("DELETE FROM tbl_user")
    abstract suspend fun clearTblUser()
}
