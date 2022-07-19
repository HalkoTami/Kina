package com.example.tangochoupdated.room

import androidx.room.*
import com.example.tangochoupdated.room.dataclass.*
import kotlinx.coroutines.flow.Flow


abstract class MyDao{

    @Dao
    abstract class CardDao(): BaseDao<Card>{
        @Query("select * from tbl_card where not card_deleted AND id = :cardId ")
        abstract fun getCardByCardId(cardId:Int?): Flow<Card>

        @Transaction
        @Query("select * from tbl_card where not card_deleted AND id = :cardId ")
        abstract fun getCardAndTagsByCardId(cardId:Int?): Flow<CardAndTags>

    }
    @Dao
    abstract class ActivityDataDao(): BaseDao<ActivityData>
    @Dao
    abstract class ChoiceDao: BaseDao<Choice>
    @Dao
    abstract class FileDao: BaseDao<File>{
    }
    @Dao
    abstract class MarkerDataDao: BaseDao<MarkerData>
    @Dao
    abstract class UserDao: BaseDao<User>
    @Dao
    abstract class ClearTable: com.example.tangochoupdated.room.dao.ClearTable
    @Dao
    abstract class LibraryDao: com.example.tangochoupdated.room.dao.LibraryDao
    @Dao
    abstract class CardAndTagXRefDao: BaseDao<CardAndTagXRef>
    @Dao
    abstract class FileXRefDao:BaseDao<FileXRef>




}
//Todo すべてのテーブルにおいてのDao
