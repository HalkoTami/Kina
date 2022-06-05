package com.example.tangochoupdated.room

import android.nfc.Tag
import android.util.Log
import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.Table
import com.example.tangochoupdated.room.rvclasses.*
import kotlinx.coroutines.flow.*

/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MyRoomRepository(
    private val myDao: MyDao) {




//cards





    @Suppress("RedundantSuspendModifier")
    @WorkerThread

    suspend  fun getLibRVCover(parentFileId:Int?):Flow<List<LibraryRV>> = flow{
        val finalList = mutableListOf<LibraryRV>()

        var containingFolder =0
        var containingFlashCardCover = 0
        var containingCard =0

        suspend fun getContainingItemsAmount(file: File?) {
            when(file?.fileStatus){
                FileStatus.FOLDER ->{
                    myDao.libraryDao.getFilesCountByFileId(file.fileId).collect { list ->
                        containingFolder += (
                        list.filter{ oneFile ->
                            oneFile.fileStatus == FileStatus.FOLDER
                        }.size)
                        containingFlashCardCover += (
                        list.filter { oneFile ->
                            oneFile.fileStatus == FileStatus.TANGO_CHO_COVER
                        }.size)
                        list.onEach { oneFile ->
                        getContainingItemsAmount(oneFile) } }
                }
                FileStatus.TANGO_CHO_COVER ->{
                    myDao.libraryDao.getCardIdsByFileId(file.fileId).collect { list ->
                        containingCard += (list.size) }
                }
                else -> Log.d("getLibRVCover","${file?.fileId} unknown file status")
            }





        }
        suspend fun getTagsList(file:List<File>):MutableList<TagData>{
            val tagList = mutableListOf<TagData>()
            file.filter { file ->  file.fileStatus== FileStatus.TAG}.onEach { file -> tagList.add(TagData(tagId = file.fileId,
            tagText = file.title!!)) }
            return tagList
        }




        myDao.libraryDao.getParentFileDataByFileId(parentFileId).collect { value
            -> value.onEach {
                file -> getContainingItemsAmount(file)
                        when(file.fileStatus){
                        FileStatus.FOLDER -> finalList.add(
                            LibraryRV.Folder(
                                file = file,
                                containingFolder = containingFolder,
                                containingFlashCardCover = containingFlashCardCover,
                                containingCard = containingCard
                            )
                        )
                        FileStatus.TANGO_CHO_COVER -> finalList.add(
                            LibraryRV.FlashCardCover(
                                file = file,
                                containingCard = containingCard
                            )
                        )
                        else -> Log.d("getLibRVCover","unknown file status")
                        }
                }
            }

        myDao.libraryDao.getCardsDataByFileId(parentFileId).collect {
            list -> list.filter { card -> card.card.cardStatus== CardStatus.STRING  }.onEach {
                        card -> finalList.add(LibraryRV.StringCard(card.card,getTagsList(card.tags))) }
                    list.filter { card -> card.card.cardStatus == CardStatus.CHOICE }.onEach {
                        card -> finalList.add(LibraryRV.ChoiceCard(card.card,getTagsList(card.tags))) }
                    list.filter { card -> card.card.cardStatus == CardStatus.MARKER  }.onEach {
                        card -> finalList.add(LibraryRV.MarkerCard(card.card,getTagsList(card.tags)))
                    }

        }
        finalList.sortWith(compareBy {it.position})
        while(true){
            emit(finalList)
        }

    }



    suspend fun insert(item: Any) {

        when (item ){
            is CardAndTagXRef -> myDao.cardAndTagXRefDao.insert(item)
            is Card -> myDao.cardDao.insert(item)
            is File -> myDao.fileDao.insert(item)
            is User -> myDao.userDao.insert(item)
            is MarkerData -> myDao.markerDataDao.insert(item)
            is Choice -> myDao.choiceDao.insert(item)
            is ActivityData -> myDao.activityDataDao.insert(item)

        }

    }
    suspend fun insertMultiple(item: List<*>) {

        myDao.cardAndTagXRefDao.insertList(item.filterIsInstance<CardAndTagXRef>())
        myDao.cardDao.insertList(item.filterIsInstance<Card>())
        myDao.fileDao.insertList(item.filterIsInstance<File>())
        myDao.activityDataDao.insertList(item.filterIsInstance<ActivityData>())
        myDao.markerDataDao.insertList(item.filterIsInstance<MarkerData>())
        myDao.choiceDao.insertList(item.filterIsInstance<Choice>())



    }



    suspend fun updateCard(item: Any) {
        when (item ){
            is CardAndTagXRef -> myDao.cardAndTagXRefDao.insert(item)
            is Card -> myDao.cardDao.update(item)
            is File -> myDao.fileDao.update(item)
            is User -> myDao.userDao.insert(item)
            is MarkerData -> myDao.markerDataDao.insert(item)
            is Choice -> myDao.choiceDao.insert(item)
            is ActivityData -> myDao.activityDataDao.insert(item)

        }

    }

    suspend fun updateCards(item: List<Any>) {
        myDao.cardAndTagXRefDao.updateMultiple(item.filterIsInstance<CardAndTagXRef>())
        myDao.cardDao.updateMultiple(item.filterIsInstance<Card>())
        myDao.fileDao.updateMultiple(item.filterIsInstance<File>())
        myDao.activityDataDao.updateMultiple(item.filterIsInstance<ActivityData>())
        myDao.markerDataDao.updateMultiple(item.filterIsInstance<MarkerData>())
        myDao.choiceDao.updateMultiple(item.filterIsInstance<Choice>())

    }

    suspend fun deleteCard(item: Any) {
        when (item){
            is CardAndTagXRef -> myDao.cardAndTagXRefDao.insert(item)
            is Card -> myDao.cardDao.delete(item)
            is File -> myDao.fileDao.delete(item)
            is User -> myDao.userDao.delete(item)
            is MarkerData -> myDao.markerDataDao.delete(item)
            is Choice -> myDao.choiceDao.delete(item)
            is ActivityData -> myDao.activityDataDao.delete(item)

        }
        }

    suspend fun deleteCards(item: List<Any>) {
        myDao.cardAndTagXRefDao.deleteMultiple(item.filterIsInstance<CardAndTagXRef>())
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