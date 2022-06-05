package com.example.tangochoupdated.room

import androidx.room.*
import com.example.tangochoupdated.room.dao.ClearTable
import com.example.tangochoupdated.room.dao.LibraryDao
import com.example.tangochoupdated.room.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MyDao{
    abstract val cardDao: BaseDao<Card>
    abstract val activityDataDao: BaseDao<ActivityData>
    abstract val choiceDao: BaseDao<Choice>
    abstract val fileDao: BaseDao<File>
    abstract val markerDataDao: BaseDao<MarkerData>
    abstract val userDao: BaseDao<User>
    abstract val clearTable: ClearTable
    abstract val libraryDao: LibraryDao
    abstract val cardAndTagXRefDao: BaseDao<CardAndTagXRef>

    @Query("select * from tbl_marker_data where marker_data_belonging_card_id = :cardId ")
    abstract fun getMarkerDataByCardId(cardId: Int ): Flow<List<MarkerData>>



    @Query("select * from tbl_file where NOT deleted AND file_belonging_file_id = NULL")
    abstract fun getFileWithoutParent(): Flow<List<File>>



    @Query("select * from tbl_choice where choice_id = :choiceId ")
    abstract fun getChoicesById(choiceId: Int ): Flow<List<Choice>>

    @Query("select * from tbl_card where NOT card_deleted AND belonging_file_id = :belongingFileId ")
    abstract fun getCardsByFileId(belongingFileId: Int ): Flow<List<Card>>

    @Query("select * from tbl_card where NOT card_deleted AND belonging_string_data OR " +
            "belonging_marker_text_preview OR belonging_quiz_cover_preview LIKE '%' || :search || '%' " +
            "ORDER BY card_id DESC")
    abstract fun searchCardsByWords(search:String):Flow<List<Card>>

    @Query("select * from tbl_card where NOT card_deleted AND belonging_file_id = NULL")
    abstract fun getCardsWithoutParent(): Flow<List<Card>>





    @Query("select count(card_id) from tbl_card where NOT card_deleted AND belonging_file_id = :belongingFileId")
    abstract fun getCardsCountByFileId(belongingFileId: Int?):Flow<Int>



    @Query("select * from tbl_card where NOT card_deleted AND belonging_file_id = :belongingFileId")
    abstract fun getLibCardsByFileId(belongingFileId: Int? ): Flow<List<File>>
}
//Todo すべてのテーブルにおいてのDao
