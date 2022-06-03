package com.example.tangochoupdated.room.dao

import androidx.room.Query
import androidx.room.Transaction
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.File
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

    @Query("select file_id , title, fileStatus,colorStatus,library_order   from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId ")
    fun getParentFileDataByFileId(belongingFileId: Int? ): Flow<List<File>>

    @Query("select file_id, fileStatus from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId AND fileStatus = 1")
    fun getFilesCountByFileId(belongingFileId: Int?):Flow<List<File>>

    @Query("WITH RECURSIVE generation AS (" +
            "    SELECT file_id," +
            "        file_belonging_file_id," +
            "fileStatus" +
            "    FROM tbl_file" +
            "    WHERE file_belonging_file_id IS :folderId AND NOT deleted " +
            " UNION ALL " +
            "    SELECT child.file_id, " +
            "        child.file_belonging_file_id, " +
            " child.fileStatus " +
            "    FROM tbl_file child" +
            "    JOIN generation g" +
            "      ON g.file_id = child.file_belonging_file_id )  " +
            "SELECT *" +
            "FROM generation ")
    fun getFolderAmount(folderId:Int?): Flow<List<File>>
//    TODO　果たしてこれはできるのか？？

    @Query("select file_id, fileStatus from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId AND fileStatus = 0")
    fun getFlashCardCoversCountByFileId(belongingFileId: Int?):Flow<List<File>>


    @Query("select card_id from tbl_card where not card_deleted AND belonging_file_id = :belongingFileId")
    fun getCardIdsByFileId(belongingFileId: Int?):Flow<List<Card>>
    @Transaction
    @Query("select card_id, belonging_string_data, belonging_marker_text_preview, " +
            "belonging_quiz_cover_preview, card_type, card_color, library_order from tbl_card " +
            "where not card_deleted AND belonging_file_id = :belongingFileId")
    fun getCardsDataByFileId(belongingFileId: Int?):Flow<List<Card>>


}
