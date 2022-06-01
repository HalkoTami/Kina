package com.example.tangochoupdated.room.dao

import androidx.room.Query
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.rvclasses.Folder
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

    @Query("select file_id , title, fileStatus,colorStatus,library_order   from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId ")
    fun getLibFilesByFileId(belongingFileId: Int? ): Flow<List<File>>

    @Query("select file_id, fileStatus from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId AND fileStatus = 1")
    fun getFilesCountByFileId(belongingFileId: Int?):Flow<List<File>>

    @Query("select file_id, fileStatus from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId AND fileStatus = 0")
    fun getFlashCardCoversCountByFileId(belongingFileId: Int?):Flow<List<File>>


}
