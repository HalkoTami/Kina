package com.korokoro.kina.db.dao

import androidx.room.*
import com.korokoro.kina.db.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CardDao: BaseDao<Card> {

    @Query(
        "Select distinct a.* from tbl_card a " +
                "left outer join tbl_x_ref b on a.id = b.id1 where" +
                " (not a.deleted and belongingFlashCardCoverId in ( " +
                "with generation AS (" +
                " select * from tbl_file where fileId in(:fileIdList)  " +
                "UNION ALL" +
                " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
                "SELECT fileId FROM generation b )) or " +
                "(b.id1TokenTable = :cardTableInt and " +
                "b.id2TokenTable = :fileTableInt and  b.id2  in(:fileIdList))  "
    )
    abstract fun getDescendantsCardsByMultipleFileId(fileIdList: List<Int>,cardTableInt:Int,fileTableInt:Int):Flow<List<Card>>

    @Query("UPDATE tbl_card SET  libOrder = libOrder + 1 WHERE " +
            "belongingFlashCardCoverId is :fileId and libOrder >= :insertingPosition")
    abstract suspend fun upDateCardsPositionBeforeInsert(fileId: Int?,insertingPosition: Int)


    @Query(
        "Select a.id from tbl_card a " +
                "left outer join tbl_x_ref b on a.id = b.id1 where" +
                " (not a.deleted and belongingFlashCardCoverId in ( " +
                "with generation AS (" +
                " select * from tbl_file where fileId in(:fileIdList)  " +
                "UNION ALL" +
                " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
                "SELECT fileId FROM generation b )) or " +
                "(b.id1TokenTable = :cardTableInt and " +
                "b.id2TokenTable = :fileTableInt and  b.id2  in(:fileIdList))  "
    )
    abstract   fun getDescendantsCardsIdsByMultipleFileId(fileIdList: List<Int>,cardTableInt:Int,fileTableInt:Int):Flow<List<Int>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("Select a.* from tbl_card a " +
            "left outer join tbl_x_ref b on " +
            "a.id = b.id1"+
            " WHERE a.belongingFlashCardCoverId in ( WITH  generation AS (" +
            " select * from tbl_file where fileId in(:fileIdList) " +
            "Union ALL" +
            " SELECT d.* from tbl_file d " +
            "Inner JOIN generation g ON g.fileId = d.parentFileId ) " +
            "SELECT fileId FROM generation b  ) or " +
            "(b.id1TokenTable = :cardTableInt and " +
            "b.id2TokenTable = :fileTableInt and  b.id2  in(:fileIdList))  "

    )
    abstract fun getAllDescendantsCardsByMultipleFileId(fileIdList: List<Int>,cardTableInt:Int,fileTableInt:Int):Flow<List<Card>>


    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("Select distinct a.* from tbl_card a " +
            "left outer join tbl_x_ref b on " +
            "a.id = b.id1"+
            " WHERE a.belongingFlashCardCoverId in ( WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId " +
            "Union ALL" +
            " SELECT d.* from tbl_file d " +
            "Inner JOIN generation g ON g.fileId = d.parentFileId ) " +
            "SELECT fileId FROM generation b  ) " +
            "or ( b.id2 = :fileId and b.id1TokenTable = :xRefTypeCardAsInt and b.id2TokenTable = :xRefTypeAnkiBoxFavouriteAsInt )"
    )
    abstract fun getAnkiBoxRVCards(fileId:Int,xRefTypeCardAsInt:Int,xRefTypeAnkiBoxFavouriteAsInt:Int):Flow<List<Card>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from tbl_card a " +
            "inner join tbl_x_ref b " +
            "on a.id = b.id1 " +
            "where b.id1TokenTable = :cardTableInt " +
            "and b.id2 = :fileId " +
            "and b.id2TokenTable = :fileTableInt"
    )
    abstract fun getAnkiBoxFavouriteRVCards(fileId:Int,cardTableInt:Int,fileTableInt:Int):Flow<List<Card>>

    @Query("UPDATE tbl_card SET timesFlipped = timesFlipped +1   WHERE id = :cardId")
    abstract suspend fun upDateCardFlippedTimes(cardId: Int)

    @Query("select * from tbl_card where not deleted AND id = :cardId ")
    abstract fun getCardByCardId(cardId:Int?): Flow<Card>

    @Query("select * from tbl_card where not deleted AND id in (:cardIds)  ")
    abstract fun getCardByMultipleCardIds(cardIds:List<Int>): Flow<List<Card>>

    @Query("select * from tbl_card where not deleted  ")
    abstract fun getAllCards(): Flow<List<Card>>

    @Query("select * FROM tbl_card " +
            "where not deleted AND belongingFlashCardCoverId is :belongingFileId "
    )
    abstract fun getCardsDataByFileId(belongingFileId: Int?):Flow<List<Card>>

    @Query("select * FROM tbl_card " +
            "where not deleted AND belongingFlashCardCoverId in(:fileIdList) "
    )
    abstract  fun getCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>>

    @Query("select * from tbl_card where NOT deleted AND " +
            "frontTitle OR " +
            "frontText OR " +
            "backTitle OR " +
            "backText " +
            "LIKE '%' || :search || '%' ")
    abstract fun searchCardsByWords(search:String):Flow<List<Card>>


    @Query("SELECT * FROM tbl_card a " +
            " INNER JOIN ( SELECT  MAX(id) id FROM tbl_card c where c.belongingFlashCardCoverId is :flashCardCoverId ) b ON a.id = b.id "
    )
    abstract fun getLastInsertedCard(flashCardCoverId:Int?):Flow<Card>
    @Query("DELETE FROM tbl_card")
    abstract suspend fun clearTblCard()

}

