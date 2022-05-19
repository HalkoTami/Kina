package com.example.tangochoupdated.room.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class User(
 @PrimaryKey(autoGenerate = true) val uid: Long,



 )