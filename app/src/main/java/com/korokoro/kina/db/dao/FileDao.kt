package com.korokoro.kina.db.dao

import androidx.room.*
import com.korokoro.kina.db.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FileDao: BaseDao<File> {
    @Query("DELETE FROM tbl_file")
    abstract suspend fun clearTblFile()
    @Query("SELECT * FROM tbl_file a " +
            " INNER JOIN ( SELECT  MAX(fileId) fileId FROM tbl_file  ) b ON a.fileId = b.fileId"
    )
    abstract fun getLastInsertedFileId():Flow<File>

    @Query("select * from tbl_file where not deleted AND parentFileId is null ")
    abstract fun getFileWithoutParent(): Flow<List<File>>

    @Query("select * from tbl_file where NOT deleted AND fileId = :lookingFileId ")
    abstract fun getFileByFileId(lookingFileId:Int?): Flow<File>

    @Query("select * from tbl_file a " +
            "JOIN  tbl_card b On b.belongingFlashCardCoverId " +
            "is :lookingFileId" +
            " where NOT a.deleted AND fileId = :lookingFileId " +
            " AND NOT b.deleted")
    abstract fun getFileChildrenCards(lookingFileId:Int?): Flow<Map<File,List<Card>>>


    @Query("select * from tbl_file a " +
            "JOIN tbl_file e ON e.parentFileId = a.fileId " +
            "where NOT a.deleted AND  " +
            "a.fileStatus = :fileStatusFolderAsInt  " +
            "and a.fileId not in (  " +
            "WITH  generation AS ( " +
            "select * from tbl_file b where b.fileId in (:movingFileIds)  " +
            "UNION ALL " +
            "SELECT c.* from tbl_file c Inner JOIN generation g ON g.parentFileId = c.fileId ) " +
            "SELECT fileId FROM generation d " +
            " ) " +
            "and ((a.parentFileId is :parentFileId " +
            "or a.fileId in ( " +
            "select fileId from tbl_file c where c.parentFileId in ( " +
            "select parentFileId from tbl_file d where d.fileId in (:movingFileIds) ) ) )" +
            "and a.fileId not in (:movingFileIds) )")
    abstract fun getFoldersMovableTo(movingFileIds: List<Int>, parentFileId: Int?, fileStatusFolderAsInt:Int): Flow<Map<File,List<File>>>

    @Query("select * from tbl_file a " +
            "JOIN tbl_card d ON d.belongingFlashCardCoverId = a.fileId " +
            "where NOT a.deleted AND  " +
            "a.fileStatus = :fileStatusFlashCardAsInt and " +
            "a.fileId not in (" +
            "select fileId from tbl_file b inner join tbl_card c on c.belongingFlashCardCoverId = b.fileId " +
            "where c.id in (:movingCardIds)" +
            " ) " )
    abstract fun getFlashCardsMovableTo(movingCardIds:List<Int>,fileStatusFlashCardAsInt:Int): Flow<Map<File,List<Card>>>


    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM tbl_file su WHERE su.parentFileId is :fileId and not su.fileStatus = 3")
    abstract fun myGetFileByParentFileId(fileId: Int?): Flow<List<File>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM tbl_file su WHERE su.parentFileId is :fileId and not su.fileStatus = 3 " +
            "and not ( " +
            "Select count(a.id) from tbl_card a " +
            "WHERE a.belongingFlashCardCoverId in (  " +
            "WITH  generation AS (" +
            "select * from tbl_file where fileId = su.fileId  " +
            "UNION ALL " +
            "SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId ) " +
            "SELECT fileId FROM generation b  ) " +
            ") = 0")
    abstract fun getLibraryItemsWithDescendantCards(fileId: Int?): Flow<List<File>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON g.parentFileId = a.fileId )" +
            "SELECT * FROM generation b ")
    abstract fun getAllAncestorsByChildFileId(fileId: Int?): Flow<List<File>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("WITH  generation AS (" +
            " select * from tbl_file where fileId in(:fileIdList)  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
            "SELECT * FROM generation b Where not b.fileId in(:fileIdList)")
    abstract  fun getAllDescendantsFilesByMultipleFileId(fileIdList: List<Int>):Flow<List<File>>
    @Query("UPDATE tbl_file SET parentFileId = :newParentFileId  WHERE parentFileId = :deletedFileId")
    abstract suspend fun upDateChildFilesOfDeletedFile(deletedFileId: Int,newParentFileId:Int?)


    @Query("DELETE FROM tbl_file  WHERE fileId in (" +
            " WITH generation AS (" +
            " select * from tbl_file where fileId = :fileId " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId ) " +
            "SELECT fileId FROM generation b )")
    abstract suspend fun deleteFileAndAllDescendants(fileId:Int)

    @Query("select count(id) from tbl_card where not deleted AND belongingFlashCardCoverId = :belongingFileId")
    abstract fun getCardAmountByFileId(belongingFileId: Int):Flow<Int>

    @Query("select * from tbl_file where NOT deleted AND " +
            "title  " +
            "LIKE '%' || :search || '%' ")
    abstract  fun searchFilesByWords(search:String):Flow<List<File>>
    @Query("select * from tbl_file b " +
            "where  NOT deleted AND " +
            "fileStatus = 0 and not (select count(id) from tbl_card a where a.belongingFlashCardCoverId = b.fileId ) = 0")
    abstract  fun getAllFlashCardCoverContainsCard():Flow<List<File>>
    @Query("select * from tbl_file where  " +
            "fileStatus = 3  ")
    abstract  fun getAllFavouriteAnkiBox():Flow<List<File>>
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
            "SELECT * FROM generation b Where not b.fileId = :fileId " +
            "and b.fileStatus = :filterFileStatusAsInt " +
            "and not (select count (id) from tbl_card c where c.belongingFlashCardCoverId in ( " +
            " with generation AS (" +
            "select * from tbl_file  where fileId = b.fileId " +
            "UNION ALL " +
            "SELECT e.* from tbl_file e Inner JOIN generation g ON e.parentFileId = g.fileId ) " +
            "SELECT fileId FROM generation f ) ) = 0")
    abstract fun getAnkiBoxRVDescendantsFiles(fileId:Int,filterFileStatusAsInt:Int):Flow<List<File>>
}

