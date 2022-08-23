package com.example.tangochoupdated.db

import androidx.room.*
import com.example.tangochoupdated.db.dao.BaseDao
import com.example.tangochoupdated.db.dataclass.*
import kotlinx.coroutines.flow.Flow


abstract class MyDao{

    @Dao
    abstract class CardDao(): BaseDao<Card> {
        @Query("select * from tbl_card where not deleted AND id = :cardId ")
        abstract fun getCardByCardId(cardId:Int?): Flow<Card>

        @Query("select * from tbl_card where not deleted AND id in (:cardIds)  ")
        abstract fun getCardByMultipleCardIds(cardIds:List<Int>): Flow<List<Card>>

        @Query("select * from tbl_card where not deleted  ")
        abstract fun getAllCards(): Flow<List<Card>>
        @Transaction
//        @Query("select * from tbl_card where not deleted AND id = :cardId ")
//        abstract fun getCardAndTagsByCardId(cardId:Int?): Flow<CardAndTags>


        @Query("SELECT * FROM tbl_card a " +
                " INNER JOIN ( SELECT  MAX(id) id FROM tbl_card  ) b ON a.id = b.id"
        )
        abstract fun getLastInsertedCard():Flow<Card>

    }
    @Dao
    abstract class ActivityDataDao(): BaseDao<ActivityData>
    @Dao
    abstract class ChoiceDao: BaseDao<Choice>
    @Dao
    abstract class FileDao: BaseDao<File> {
    }
    @Dao
    abstract class MarkerDataDao: BaseDao<MarkerData>
    @Dao
    abstract class UserDao: BaseDao<User>
    @Dao
    abstract class ClearTable: com.example.tangochoupdated.db.dao.ClearTable
    @Dao
    abstract class LibraryDao: com.example.tangochoupdated.db.dao.LibraryDao
    @Dao
    abstract class CardAndTagXRefDao: BaseDao<CardAndTagXRef>
    @Dao
    abstract class FileXRefDao: BaseDao<FileXRef>




}
//Todo すべてのテーブルにおいてのDao
