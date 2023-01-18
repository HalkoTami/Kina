package com.koronnu.kina.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tbl_user")
data class User(
 @PrimaryKey(autoGenerate = true) val uid: Long,
 )
