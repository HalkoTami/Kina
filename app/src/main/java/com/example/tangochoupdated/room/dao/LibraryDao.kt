package com.example.tangochoupdated.room.dao

import androidx.room.Query
import androidx.room.Transaction
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

    @Query("select * from tbl_file where NOT deleted AND parentFile = :belongingFileId ")
    fun getFileDataByFileId(belongingFileId: Int ): Flow<List<File>>

    @Query("select * from tbl_file where NOT deleted AND parentFile = fileId ")
    fun getFileWithoutParent(): Flow<List<File>>





//    TODO　果たしてこれはできるのか？？

    @Query("select * from tbl_file where NOT deleted AND parentFile = :belongingFileId AND fileStatus = 0")
    fun getFlashCardCoversCountByFileId(belongingFileId: Int):Flow<List<File>>


    @Query("select count(id) from tbl_card where not card_deleted AND belongingFileId = :belongingFileId")
    fun getCardAmountByFileId(belongingFileId: Int):Flow<Int>

    @Transaction
    @Query("select * FROM tbl_card " +
            "where not card_deleted AND belongingFileId = :belongingFileId")
    fun getCardsDataByFileId(belongingFileId: Int):Flow<List<CardAndTags>>

    @Query("select * from tbl_card where NOT card_deleted AND " +
            "belonging_markerTextPreview OR " +
            "belonging_frontText OR " +
            "belonging_backText " +
            "LIKE '%' || :search || '%' " +
            "ORDER BY id DESC")
    fun searchCardsByWords(search:String):Flow<List<Card>>


}
