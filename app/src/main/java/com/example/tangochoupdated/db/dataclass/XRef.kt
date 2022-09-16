package com.example.tangochoupdated.db.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.tangochoupdated.db.enumclass.XRefType
import com.example.tangochoupdated.db.typeConverters.XRefTypeConverter

@Entity(
    tableName = "tbl_x_ref",
)
@TypeConverters(XRefTypeConverter::class)
data class XRef(
    @PrimaryKey(autoGenerate = true) val xRefId:Int,
    var id1: Int,
    var id1TokenTable:XRefType,
    var id2: Int,
    var id2TokenTable:XRefType
)