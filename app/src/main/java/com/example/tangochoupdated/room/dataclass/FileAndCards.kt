package com.example.tangochoupdated.room.dataclass

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Relation


class FileAndCards{
    @Embedded
    lateinit var file: File

    @Relation(parentColumn = "file_id",
        entityColumn = "belonging_file_id")
    lateinit var cards: List<Card>
}


@Dao
interface LibraryDao{
    @Query("SELECT *  FROM tbl_file JOIN tbl_card ON belonging_file_id = file_belonging_file_id = NULL")
    abstract fun getLibraryCoverWithoutParents():List<>
}