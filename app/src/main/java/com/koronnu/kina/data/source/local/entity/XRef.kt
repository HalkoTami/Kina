package com.koronnu.kina.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.koronnu.kina.data.source.local.entity.enumclass.XRefType
import com.koronnu.kina.data.source.local.typeConverters.XRefTypeConverter

@Entity(
    tableName = "tbl_x_ref",
)
@TypeConverters(XRefTypeConverter::class)
data class XRef(
    @PrimaryKey(autoGenerate = true) val xRefId:Int,
    var id1: Int,
    var id1TokenXRefType: XRefType,
    var id2: Int,
    var id2TokenXRefType: XRefType
)