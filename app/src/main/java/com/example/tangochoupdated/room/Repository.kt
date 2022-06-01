package com.example.tangochoupdated

import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.MyDao
import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.Table
import com.example.tangochoupdated.room.rvclasses.Folder
import kotlinx.coroutines.flow.*
import java.util.*

/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MyRoomRepository(
    private val myDao: MyDao) {




//cards



    fun getLibRVCover(parentFileId:Int):Flow<List<File>>{

    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Any) {
        when (item ){
            is Card -> myDao.cardDao.insert(item)
            is File -> myDao.fileDao.insert(item)
            is User -> myDao.userDao.insert(item)
            is MarkerData -> myDao.markerDataDao.insert(item)
            is Choice -> myDao.choiceDao.insert(item)
            is ActivityData -> myDao.activityDataDao.insert(item)

        }

    }
    suspend fun insertMultiple(item: List<*>) {

        myDao.cardDao.insertList(item.filterIsInstance<Card>())
        myDao.fileDao.insertList(item.filterIsInstance<File>())
        myDao.activityDataDao.insertList(item.filterIsInstance<ActivityData>())
        myDao.markerDataDao.insertList(item.filterIsInstance<MarkerData>())
        myDao.choiceDao.insertList(item.filterIsInstance<Choice>())



    }



    suspend fun updateCard(item: Any) {
        when (item ){
            is Card -> myDao.cardDao.update(item)
            is File -> myDao.fileDao.update(item)
            is User -> myDao.userDao.insert(item)
            is MarkerData -> myDao.markerDataDao.insert(item)
            is Choice -> myDao.choiceDao.insert(item)
            is ActivityData -> myDao.activityDataDao.insert(item)

        }

    }

    suspend fun updateCards(item: List<Any>) {
        myDao.cardDao.updateMultiple(item.filterIsInstance<Card>())
        myDao.fileDao.updateMultiple(item.filterIsInstance<File>())
        myDao.activityDataDao.updateMultiple(item.filterIsInstance<ActivityData>())
        myDao.markerDataDao.updateMultiple(item.filterIsInstance<MarkerData>())
        myDao.choiceDao.updateMultiple(item.filterIsInstance<Choice>())

    }

    suspend fun deleteCard(item: Any) {
        when (item){
            is Card -> myDao.cardDao.delete(item)
            is File -> myDao.fileDao.delete(item)
            is User -> myDao.userDao.delete(item)
            is MarkerData -> myDao.markerDataDao.delete(item)
            is Choice -> myDao.choiceDao.delete(item)
            is ActivityData -> myDao.activityDataDao.delete(item)

        }
        }

    suspend fun deleteCards(item: List<Any>) {
        myDao.cardDao.deleteMultiple(item.filterIsInstance<Card>())
        myDao.fileDao.deleteMultiple(item.filterIsInstance<File>())
        myDao.activityDataDao.deleteMultiple(item.filterIsInstance<ActivityData>())
        myDao.markerDataDao.deleteMultiple(item.filterIsInstance<MarkerData>())
        myDao.choiceDao.deleteMultiple(item.filterIsInstance<Choice>())
    }

    suspend fun clearTable(table:Table){
        when(table.ordinal){
            Table.CARD.ordinal -> myDao.clearTable.clearTblCard()
//            TODO 使うかわからん

        }
    }

//

}