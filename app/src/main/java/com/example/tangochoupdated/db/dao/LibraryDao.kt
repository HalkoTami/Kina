package com.example.tangochoupdated.db.dao

import androidx.room.Query
import androidx.room.RoomWarnings
import com.example.tangochoupdated.db.dataclass.*
import io.reactivex.Single
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

    @Query("select * from tbl_file where NOT deleted AND fileId = :lookingFileId ")
    fun getFileByFileId2(lookingFileId:Int?): Single<List<File>>



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
    fun getAllAncestorsByChildFileId(fileId: Int?): Flow<List<File>>
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("UPDATE tbl_file SET descendantsCardsAmount = descendantsCardsAmount + 1 " +
            " WHERE fileId in ( WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a " +
            " Inner JOIN generation g ON g.parentFileId = a.fileId ) " +
            "SELECT fileId FROM generation b ) ")
    fun upDateAncestorsCardAmount(fileId: Int)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("UPDATE tbl_file SET descendantsFoldersAmount = descendantsFoldersAmount + 1 " +
            " WHERE fileId in ( WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a " +
            " Inner JOIN generation g ON g.parentFileId = a.fileId ) " +
            "SELECT fileId FROM generation b ) ")
    fun upDateAncestorsFolderAmount(fileId: Int)
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("UPDATE tbl_file SET descendantsFlashCardsCoversAmount = descendantsFlashCardsCoversAmount + 1 " +
            " WHERE fileId in ( WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a " +
            " Inner JOIN generation g ON g.parentFileId = a.fileId ) " +
            "SELECT fileId FROM generation b ) ")
    fun upDateAncestorsFlashCardCoverAmount(fileId: Int)

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("UPDATE tbl_file " +
            "SET flippedOnceAmount = (select  Count( id  ) from tbl_card where timesFlipped = 1 and belongingFlashCardCoverId = :fileId )" +
            "and  flippedTwiceAmount = (select  Count( id  ) from tbl_card where timesFlipped = 2 and belongingFlashCardCoverId = :fileId )" +
            "and flippedThreeTimesAmount = (select  Count( id  ) from tbl_card where timesFlipped = 3 and belongingFlashCardCoverId = :fileId )" +
            "and flippedFourTimesAmount = (select  Count( id  ) from tbl_card where timesFlipped >= 4 and belongingFlashCardCoverId = :fileId )" +
            " WHERE fileId in ( WITH  generation AS (" +
            " select c.* from tbl_file c Inner Join tbl_card_file_x_ref b " +
            " on b.cardId = :upDatedCardId and b.tagId = c.fileId " +
            " or c.fileId = :fileId " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a " +
            " Inner JOIN generation g ON g.parentFileId = a.fileId ) " +
            "SELECT fileId FROM generation b  )")
    fun upDateAncestorsFlipCount(upDatedCardId:Int,fileId: Int?)

    @Query("UPDATE tbl_card SET timesFlipped = timesFlipped +1   WHERE id = :cardId")
    fun upDateCardFlippedTimes(cardId: Int)



    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("WITH  generation AS (" +
            " select * from tbl_file where fileId in(:fileIdList)  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
            "SELECT * FROM generation b ")
    fun getAllDescendantsFilesByMultipleFileId(fileIdList: List<Int>):Flow<List<File>>


    @Query(
        "Select * from tbl_card where not deleted and belongingFlashCardCoverId in ( " +
            "with generation AS (" +
            " select * from tbl_file where fileId in(:fileIdList)  " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
            "SELECT fileId FROM generation b )")
    fun getAllDescendantsCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>>

    @Query(
        "Select id from tbl_card where not deleted and belongingFlashCardCoverId in ( " +
                "with generation AS (" +
                " select * from tbl_file where fileId in(:fileIdList)  " +
                "UNION ALL" +
                " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
                "SELECT fileId FROM generation b )")
    fun getDescendantsCardsIdsByMultipleFileId(fileIdList: List<Int>):Flow<List<Int>>

    @Query("UPDATE tbl_file SET parentFileId = :newParentFileId  WHERE parentFileId = :deletedFileId")
    fun upDateChildFilesOfDeletedFile(deletedFileId: Int,newParentFileId:Int?)

    @Query("UPDATE tbl_file SET childFoldersAmount = childFoldersAmount + 1  WHERE fileId = :fileId")
    fun upDateFileChildFoldersAmount(fileId: Int)

    @Query("UPDATE tbl_file SET childFlashCardCoversAmount = childFlashCardCoversAmount + 1  WHERE fileId = :fileId")
    fun upDateFileChildFlashCardCoversAmount(fileId: Int)

    @Query("UPDATE tbl_file SET childCardsAmount = childCardsAmount + 1  WHERE fileId = :fileId")
    fun upDateFileChildCardsAmount(fileId: Int)




    @Query("DELETE FROM tbl_file  WHERE fileId in (" +
            " WITH generation AS (" +
            " select * from tbl_file where fileId = :fileId " +
            "UNION ALL" +
            " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId ) " +
            "SELECT fileId FROM generation b )")
    fun deleteFileAndAllDescendants(fileId:Int)

    @Query("SELECT * FROM tbl_activity_data  WHERE cardId = :cardId")
    fun getActivityDataByCard(cardId: Int):Flow<List<ActivityData>>




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


    @Query("select count(id) from tbl_card where not deleted AND belongingFlashCardCoverId = :belongingFileId")
    fun getCardAmountByFileId(belongingFileId: Int):Flow<Int>

//    Order by library_order asc
    @Query("select * FROM tbl_card " +
            "where not deleted AND belongingFlashCardCoverId is :belongingFileId "
    )
    fun getCardsDataByFileId(belongingFileId: Int?):Flow<List<Card>>

    @Query("select * FROM tbl_card " +
            "where not deleted AND belongingFlashCardCoverId in(:fileIdList) "
    )
    fun getCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>>

    @Query("select * from tbl_card where NOT deleted AND " +
            "frontTitle OR " +
            "frontText OR " +
            "backTitle OR " +
            "backText " +
            "LIKE '%' || :search || '%' ")
    fun searchCardsByWords(search:String):Flow<List<Card>>

    @Query("select * from tbl_file where NOT deleted AND " +
            "title  " +
            "LIKE '%' || :search || '%' ")
    fun searchFilesByWords(search:String):Flow<List<File>>

    @Query("select * from tbl_file where NOT deleted AND " +
            "fileStatus = 0  ")
    fun getAllFlashCardCover():Flow<List<File>>




}
