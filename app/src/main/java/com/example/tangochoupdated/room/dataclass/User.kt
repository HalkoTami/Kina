package com.example.tangochoupdated.room.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tbl_user")
data class User(
 @PrimaryKey(autoGenerate = true) val uid: Long,



 )
