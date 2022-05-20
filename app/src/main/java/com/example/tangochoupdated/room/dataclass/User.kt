package com.example.tangochoupdated.room.dataclass

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.tangochoupdated.room.DataAccessObject


@Entity(tableName = "tbl_user")
data class User(
 @PrimaryKey(autoGenerate = true) val uid: Long,



 )

@Dao
abstract class UserDao: DataAccessObject<User>{

 @Query("DELETE FROM tbl_user")
  abstract suspend fun clearTblUser()
}