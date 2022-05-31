package com.example.tangochoupdated.room

import androidx.room.*
import com.example.tangochoupdated.room.dataclass.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MyDao{
    abstract class CardDao: DataAccessObject<Card>{
        @Query("DELETE FROM tbl_card")
        abstract suspend fun clearTblCard()


        @Query("select * from tbl_card where NOT card_deleted AND belonging_file_id = :belongingFileId ")
        abstract fun getCardsByFileId(belongingFileId: Int ): Flow<List<Card>>

        @Query("select * from tbl_card where NOT card_deleted AND belonging_string_data OR " +
                "belonging_marker_text_preview OR belonging_quiz_cover_preview LIKE '%' || :search || '%' " +
                "ORDER BY card_id DESC")
        abstract fun searchCardsByWords(search:String):Flow<List<Card>>

        @Query("select * from tbl_card where NOT card_deleted AND belonging_file_id = NULL")
        abstract fun getCardsWithoutParent(): Flow<List<Card>>
    }
    abstract class ActivityDataDao: DataAccessObject<ActivityData>{
        @Query("DELETE FROM tbl_activity_data")
        abstract suspend fun clearTblActivity()
    }
    abstract class ChoiceDao: DataAccessObject<Choice>{
        @Query("DELETE FROM tbl_choice")
        abstract suspend fun clearTblChoice()

        @Query("select * from tbl_choice where choice_id = :choiceId ")
        abstract fun getChoicesById(choiceId: Int ): Flow<List<Choice>>
    }
    abstract class FileDao: DataAccessObject<File>{
        @Query("DELETE FROM tbl_file")
        abstract suspend fun clearTblFile()

        @Query("select * from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId ")
        abstract fun getLibCardsByFileId(belongingFileId: Int? ): Flow<List<File>>


        @Query("select * from tbl_file where NOT deleted AND file_belonging_file_id = NULL")
        abstract fun getFileWithoutParent(): Flow<List<File>>

    }
    abstract class MarkerDataDao: DataAccessObject<MarkerData>{
        @Query("DELETE FROM tbl_marker_data")
        abstract suspend fun clearTblMarkerData()

        @Query("select * from tbl_marker_data where marker_data_belonging_card_id = :cardId ")
        abstract fun getMarkerDataByCardId(cardId: Int ): Flow<List<MarkerData>>
    }
    abstract class UserDao: DataAccessObject<User>{

        @Query("DELETE FROM tbl_user")
        abstract suspend fun clearTblUser()
    }


    @Query("select * from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId ")
    abstract fun getLibFilesByFileId(belongingFileId: Int? ): Flow<List<File>>

    @Query("select count(file_id) from tbl_file where NOT deleted AND file_belonging_file_id = :belongingFileId")
    abstract fun getFilesCountByFileId(belongingFileId: Int?):Flow<Int>

    @Query("select count(card_id) from tbl_card where NOT card_deleted AND belonging_file_id = :belongingFileId")
    abstract fun getCardsCountByFileId(belongingFileId: Int?):Flow<Int>



    @Query("select * from tbl_card where NOT card_deleted AND belonging_file_id = :belongingFileId")
    abstract fun getLibCardsByFileId(belongingFileId: Int? ): Flow<List<File>>
}
//Todo すべてのテーブルにおいてのDao
