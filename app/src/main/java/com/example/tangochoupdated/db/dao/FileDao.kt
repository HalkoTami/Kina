package com.example.tangochoupdated.db.dao

import androidx.room.*
import com.example.tangochoupdated.db.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FileDao: BaseDao<File> {
    @Query("DELETE FROM tbl_file")
    abstract suspend fun clearTblFile()
    @Query("SELECT a.fileId FROM tbl_file a " +
            " INNER JOIN ( SELECT  MAX(fileId) fileId FROM tbl_file  ) b ON a.fileId = b.fileId"
    )
    abstract fun getLastInsertedFileId():Flow<Int>

    @Query("select * from tbl_file where not deleted AND NOT hasParent ")
    abstract fun getFileWithoutParent(): Flow<List<File>>

    @Query("select * from tbl_file where NOT deleted AND fileId = :lookingFileId ")
    abstract fun getFileByFileId(lookingFileId:Int?): Flow<File>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM tbl_file su WHERE su.parentFileId is :fileId and not su.fileStatus = 3")
    abstract fun myGetFileByParentFileId(fileId: Int?): Flow<List<File>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON g.parentFileId = a.fileId )" +
            "SELECT * FROM generation b ")
    abstract fun getAllAncestorsByChildFileId(fileId: Int?): Flow<List<File>>
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("UPDATE tbl_file SET descendantsCardsAmount = descendantsCardsAmount + :amount " +
            " WHERE fileId in ( WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a " +
            " Inner JOIN generation g ON g.parentFileId = a.fileId ) " +
            "SELECT fileId FROM generation b ) ")
    abstract suspend fun upDateAncestorsCardAmount(fileId: Int,amount: Int)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("UPDATE tbl_file SET descendantsFoldersAmount = descendantsFoldersAmount + :amount " +
            " WHERE fileId in ( WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a " +
            " Inner JOIN generation g ON g.parentFileId = a.fileId ) " +
            "SELECT fileId FROM generation b ) ")
    abstract suspend fun upDateAncestorsFolderAmount(fileId: Int,amount: Int)

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("UPDATE tbl_file SET descendantsFlashCardsCoversAmount = descendantsFlashCardsCoversAmount + :amount " +
            " WHERE fileId in ( WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a " +
            " Inner JOIN generation g ON g.parentFileId = a.fileId ) " +
            "SELECT fileId FROM generation b ) ")
    abstract suspend fun upDateAncestorsFlashCardCoverAmount(fileId: Int,amount:Int)

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("UPDATE tbl_file " +
            "SET flippedOnceAmount = (select  Count( id  ) from tbl_card where timesFlipped = 1 and belongingFlashCardCoverId = :fileId )" +
            "and  flippedTwiceAmount = (select  Count( id  ) from tbl_card where timesFlipped = 2 and belongingFlashCardCoverId = :fileId )" +
            "and flippedThreeTimesAmount = (select  Count( id  ) from tbl_card where timesFlipped = 3 and belongingFlashCardCoverId = :fileId )" +
            "and flippedFourTimesAmount = (select  Count( id  ) from tbl_card where timesFlipped >= 4 and belongingFlashCardCoverId = :fileId )" +
            " WHERE fileId in ( WITH  generation AS (" +
            " select c.* from tbl_file c Inner Join tbl_card_file_x_ref b " +
            " on b.cardFileXRefCardId = :upDatedCardId and b.cardFileXRefFileId = c.fileId " +
            " or c.fileId = :fileId " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a " +
            " Inner JOIN generation g ON g.parentFileId = a.fileId ) " +
            "SELECT fileId FROM generation b  )")
    abstract suspend fun upDateAncestorsFlipCount(upDatedCardId:Int,fileId: Int?)

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("WITH  generation AS (" +
            " select * from tbl_file where fileId in(:fileIdList)  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
            "SELECT * FROM generation b Where not b.fileId in(:fileIdList)")
    abstract  fun getAllDescendantsFilesByMultipleFileId(fileIdList: List<Int>):Flow<List<File>>
    @Query("UPDATE tbl_file SET parentFileId = :newParentFileId  WHERE parentFileId = :deletedFileId")
    abstract suspend fun upDateChildFilesOfDeletedFile(deletedFileId: Int,newParentFileId:Int?)

    @Query("UPDATE tbl_file SET childFoldersAmount = childFoldersAmount + :amount  WHERE fileId = :fileId")
    abstract suspend fun upDateFileChildFoldersAmount(fileId: Int,amount: Int)

    @Query("UPDATE tbl_file SET childFlashCardCoversAmount = childFlashCardCoversAmount + :amount  WHERE fileId = :fileId")
    abstract suspend fun upDateFileChildFlashCardCoversAmount(fileId: Int,amount: Int)

    @Query("UPDATE tbl_file SET childCardsAmount = childCardsAmount + :amount  WHERE fileId = :fileId")
    abstract suspend fun upDateFileChildCardsAmount(fileId: Int,amount: Int)

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
    @Query("select * from tbl_file where NOT deleted AND " +
            "fileStatus = 0  ")
    abstract  fun getAllFlashCardCover():Flow<List<File>>
    @Query("select * from tbl_file where  " +
            "fileStatus = 3  ")
    abstract  fun getAllFavouriteAnkiBox():Flow<List<File>>
}

