package com.example.tangochoupdated.room

import androidx.room.*
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.CardDao
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MyDao{
    abstract class  CardDao:DataAccessObject<Card>{

    }

    @Query("select * from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId ")
    abstract fun getLibFilesByFileId(belongingFileId: Int? ): Flow<List<File>>

    @Query("select count(file_id) from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId")
    abstract fun getFilesCountByFileId(belongingFileId: Int?):Flow<Int>

    @Query("select count(card_id) from tbl_card where NOT card_deleted AND belonging_file_id = :belongingFileId")
    abstract fun getCardsCountByFileId(belongingFileId: Int?):Flow<Int>



    @Query("select * from tbl_card where NOT card_deleted AND belonging_file_id = :belongingFileId")
    abstract fun getLibCardsByFileId(belongingFileId: Int? ): Flow<List<File>>
}
//Todo すべてのテーブルにおいてのDao
