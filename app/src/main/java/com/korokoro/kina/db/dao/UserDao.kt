package com.korokoro.kina.db.dao

import androidx.room.*
import com.korokoro.kina.db.dataclass.*
@Dao
abstract class UserDao: BaseDao<User>{
    @Query("DELETE FROM tbl_user")
    abstract suspend fun clearTblUser()
}
