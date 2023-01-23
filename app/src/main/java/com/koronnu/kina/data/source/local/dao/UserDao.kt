package com.koronnu.kina.data.source.local.dao

import androidx.room.*
import com.koronnu.kina.data.source.local.entity.User
@Dao
abstract class UserDao: BaseDao<User> {
    @Query("DELETE FROM tbl_user")
    abstract suspend fun clearTblUser()
}
