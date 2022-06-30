package com.example.tangochoupdated.room.dao

import androidx.room.Query
import androidx.room.Transaction
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileWithChild
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

    @Transaction
    @Query("select * from tbl_file where NOT deleted AND fileId = :belongingFileId ")
    fun getFileListByParentFileId(belongingFileId: Int? ): Flow<FileWithChild>

    @Query("select * from tbl_file where not deleted AND NOT hasParent ")
    fun getFileWithoutParent(): Flow<List<File>>



    @Query("select * from tbl_file where NOT deleted AND fileId = :lookingFileId ")
    fun getFileByFileId(lookingFileId:Int?): Flow<List<File>>








//    TODO　果たしてこれはできるのか？？



    @Query("select count(id) from tbl_card where not card_deleted AND belongingFileId = :belongingFileId")
    fun getCardAmountByFileId(belongingFileId: Int):Flow<Int>

    @Transaction
    @Query("select * FROM tbl_card " +
            "where not card_deleted AND belongingFileId = :belongingFileId")
    fun getCardsDataByFileId(belongingFileId: Int?):Flow<List<CardAndTags>>

    @Query("select * from tbl_card where NOT card_deleted AND " +
            "belonging_markerTextPreview OR " +
            "belonging_frontText OR " +
            "belonging_backText " +
            "LIKE '%' || :search || '%' " +
            "ORDER BY id DESC")
    fun searchCardsByWords(search:String):Flow<List<Card>>


}
