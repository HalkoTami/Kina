package com.example.tangochoupdated.db.dataclass

import androidx.room.*

@Entity(tableName = "tbl_choice")
data class Choice(
    @PrimaryKey
    val id: Int,
    @ColumnInfo
    var text:String?,

    )

