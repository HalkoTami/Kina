package com.example.tangochoupdated.room.dataclass

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tangochoupdated.CardAndData

class FileAndCards{
    @Embedded
    lateinit var file: File

    @Relation(parentColumn = "file_id",
        entityColumn = "belonging_file_id")
    lateinit var cards: List<CardAndData>
}