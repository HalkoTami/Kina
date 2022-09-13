package com.example.tangochoupdated.db.dao

import androidx.room.*
import com.example.tangochoupdated.db.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CardDao(): BaseDao<Card> {

    @Query(
        "Select a.* from tbl_card a " +
                "left outer join tbl_card_file_x_ref b on a.id = b.cardFileXRefCardId where" +
                " (not a.deleted and belongingFlashCardCoverId in ( " +
                "with generation AS (" +
                " select * from tbl_file where fileId in(:fileIdList)  " +
                "UNION ALL" +
                " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
                "SELECT fileId FROM generation b )) or b.cardFileXRefFileId  in(:fileIdList) "
    )
    abstract fun getDescendantsCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>>

    @Query("UPDATE tbl_card SET  libOrder = libOrder + 1 WHERE " +
            "belongingFlashCardCoverId is :fileId and libOrder >= :insertingPosition")
    abstract suspend fun upDateCardsPositionBeforeInsert(fileId: Int?,insertingPosition: Int)


    @Query(
        "Select a.id from tbl_card a " +
                "left outer join tbl_card_file_x_ref b on a.id = b.cardFileXRefCardId where" +
                " (not a.deleted and belongingFlashCardCoverId in ( " +
                "with generation AS (" +
                " select * from tbl_file where fileId in(:fileIdList)  " +
                "UNION ALL" +
                " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
                "SELECT fileId FROM generation b )) or b.cardFileXRefFileId  in(:fileIdList) "
    )
    abstract   fun getDescendantsCardsIdsByMultipleFileId(fileIdList: List<Int>):Flow<List<Int>>

    @Query(
        "Select * from tbl_card where not deleted and belongingFlashCardCoverId in ( " +
                "with generation AS (" +
                " select * from tbl_file where fileId in(:fileIdList)  " +
                "UNION ALL" +
                " SELECT a.* from tbl_file a Inner JOIN generation g ON a.parentFileId = g.fileId )" +
                "SELECT fileId FROM generation b where not b.fileId in (:fileIdList))")
    abstract fun getAllDescendantsCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("Select a.* from tbl_card a " +
            "left outer join tbl_card_file_x_ref x on " +
            "a.id = x.cardFileXRefCardId"+
            " WHERE a.belongingFlashCardCoverId in ( WITH  generation AS (" +
            " select * from tbl_file where fileId = :fileId " +
            "Union ALL" +
            " SELECT d.* from tbl_file d " +
            "Inner JOIN generation g ON g.fileId = d.parentFileId ) " +
            "SELECT fileId FROM generation b  ) " +
            "or x.cardFileXRefFileId = :fileId"

    )
    abstract fun getAnkiBoxRVCards(fileId:Int):Flow<List<Card>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from tbl_card a " +
            "inner join tbl_card_file_x_ref b " +
            "on a.id = b.cardFileXRefCardId " +
            "where b.cardFileXRefFileId = :fileId"
    )
    abstract fun getAnkiBoxFavouriteRVCards(fileId:Int):Flow<List<Card>>

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
            " INNER JOIN ( SELECT  MAX(id) id FROM tbl_card  ) b ON a.id = b.id where a.belongingFlashCardCoverId is :flashCardCoverId"
    )
    abstract fun getLastInsertedCard(flashCardCoverId:Int?):Flow<Card>
    @Query("DELETE FROM tbl_card")
    abstract suspend fun clearTblCard()

}

