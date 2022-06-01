package com.example.tangochoupdated

import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.MyDao
import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.Table
import com.example.tangochoupdated.room.rvclasses.Folder
import com.example.tangochoupdated.room.rvclasses.FolderData
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.flow.*
import java.util.*

/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MyRoomRepository(
    private val myDao: MyDao) {




//cards



     suspend  fun getLibRVCover(parentFileId:Int?):List<LibraryRV>{


         suspend fun initFolder (list:List<File>){
             var a = 0
             while(a<list.size){
                 val file = list[a]
                 var containingCard =0
                 suspend fun initContainingFolder(list: List<File>){
                     var b = 0

                     containingCard += list.size
                     while (b<list.size){
                         myDao.libraryDao.getFilesCountByFileId(list[b].fileId).onEach { value -> initContainingFolder(value) }.collect()

                     }


                 }
                 myDao.libraryDao.getFilesCountByFileId(file.fileId).onEach { value -> initFolder(value) }.collect()
                 var rVFolder:FolderData = FolderData(id = file.fileId,
                     title = file.title!!, colorStatus = file.colorStatus,
                 containingFolder = containingCard,
//                 TODO
                     )
                 ++a

             }

         }

        val list = mutableListOf<LibraryRV>()

         val folderData = mutableListOf<File>()
        myDao.libraryDao.getLibFilesByFileId(parentFileId).onEach { value -> initFolder(value) }.collect()
        return list

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