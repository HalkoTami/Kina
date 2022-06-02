package com.example.tangochoupdated.room.dao

import androidx.room.Query
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.File
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

    @Query("select file_id , title, fileStatus,colorStatus,library_order   from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId ")
    fun getParentFileDataByFileId(belongingFileId: Int? ): Flow<List<File>>

    @Query("select file_id, fileStatus from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId AND fileStatus = 1")
    fun getFilesCountByFileId(belongingFileId: Int?):Flow<List<File>>

    @Query("select file_id, fileStatus from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId AND fileStatus = 0")
    fun getFlashCardCoversCountByFileId(belongingFileId: Int?):Flow<List<File>>

    @Query("select card_id from tbl_card where not card_deleted AND belonging_file_id = :belongingFileId")
    fun getCardsByFileId(belongingFileId: Int?):Flow<List<Card>>


}
