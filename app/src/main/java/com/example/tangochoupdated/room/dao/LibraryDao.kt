package com.example.tangochoupdated.room.dao

import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.example.tangochoupdated.room.dataclass.*
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

//    @Transaction
//    @Query("select * from tbl_file where NOT deleted AND fileId = :belongingFileId ")
//    fun getFileListByParentFileId(belongingFileId: Int? ): Flow<FileWithChild>

    @Query("select * from tbl_file where not deleted AND NOT hasParent ")
    fun getFileWithoutParent(): Flow<List<File>>

    @Query("select * from tbl_file where not deleted AND NOT hasParent ")
    fun deleteFileWithoutParent(): Flow<List<File>>


    @Query("select * from tbl_file where NOT deleted AND fileId = :lookingFileId ")
    fun getFileByFileId(lookingFileId:Int?): Flow<File>



    @Query("SELECT a.fileId FROM tbl_file a " +
            " INNER JOIN ( SELECT  MAX(fileId) fileId FROM tbl_file  ) b ON a.fileId = b.fileId"
    )
    fun getLastInsertedFile():Flow<Int>

//    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
//    @Query("""
//        SELECT * FROM tbl_file b INNER JOIN file_xref a ON
//        b.fileId = a.childFileId WHERE
//        a.parentFileId = :parentFileId
//        """)
//    fun myGetFileByParentFileId(parentFileId: Int?): Flow<List<File>>
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM tbl_file su WHERE su.parentFileId is :fileId")
     fun myGetFileByParentFileId(fileId: Int?): Flow<List<File>>

     @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON g.parentFileId = a.fileId )" +
            "SELECT * FROM generation b ")
    fun getAllAncestorsByChildFileId(fileId: Int?):Flow<List<File>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("WITH  generation AS (" +
            " select * from tbl_file where fileId in (:fileId) " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
            "SELECT * FROM generation b ")
    fun getAllDescendantsFilesByParentFileId(fileId: List<Int>?):Flow<List<File>>

    @Query("UPDATE tbl_file SET parentFileId = :newParentFileId  WHERE parentFileId = :deletedFileId")
    fun upDateChildFilesOfDeletedFile(deletedFileId: Int,newParentFileId:Int?)

    @Query("DELETE FROM tbl_file  WHERE fileId in (" +
            " WITH generation AS (" +
            " select * from tbl_file where fileId = :fileId " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId ) " +
            "SELECT fileId FROM generation b )")
    fun deleteFileAndAllDescendants(fileId:Int)







//    " JOIN tbl_file parent ON parent.fileId = a.parentFileId"
//
//    uery("SELECT *  FROM file_xref p_c " +
//    " INNER JOIN file_xref gp_p ON gp_p.childFileId = p_c.parentFileId " +
//    " INNER JOIN tbl_file gp ON gp.fileId = gp_p.parentFileId " +
//    "INNER JOIN tbl_file p ON p.fileId = p_c.parentFileId " +
//    "INNER JOIN tbl_file c ON c.fileId = p_c.childFileId " +
//    "WHERE p_c.childFileId ")
//
//    "with recursive generation as (SELECT *  FROM tbl_file f" +
//    "  JOIN  file_xref  " +
//    " a on a.childFileId is f.fileId"+
//    " and a.parentFileId is :fileId " +
//    "Union all " +
//    "select * from tbl_file child " +
//    "JOIN file_xref b on b.childFileId = child.fileId " +
//    "and b.parentFileId is :fileId" +
//    " Join generation g On g.fileId = b.parentFileId ) " +
//    "SELECT * from generation g JOIN tbl_file parent ON g.parentFileId = parent.fileId"
//    TODO　果たしてこれはできるのか？？
//@Query("SELECT *  FROM tbl_file f " +
//        "JOIN (SELECT * FROM file_xref  " +
//        " ) a on a.childFileId = f.fileId " +
//        " and a.parentFileId = :fileId  ")


    @Query("select count(id) from tbl_card where not card_deleted AND belongingFileId = :belongingFileId")
    fun getCardAmountByFileId(belongingFileId: Int):Flow<Int>


    @Query("select * FROM tbl_card " +
            "where not card_deleted AND belongingFileId = :belongingFileId Order by library_order asc")
    fun getCardsDataByFileId(belongingFileId: Int?):Flow<List<CardAndTags>>

    @Query("select * from tbl_card where NOT card_deleted AND " +
            "belonging_markerTextPreview OR " +
            "belonging_frontText OR " +
            "belonging_backText " +
            "LIKE '%' || :search || '%' " +
            "ORDER BY id DESC")
    fun searchCardsByWords(search:String):Flow<List<Card>>


}
