package com.example.tangochoupdated

import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.MyDao
import com.example.tangochoupdated.room.dataclass.*
import kotlinx.coroutines.flow.Flow
import java.util.*

/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MyRoomRepository(
    private val myDao: MyDao) {

    sealed class Table(){
        abstract val daoItem:Any


        data class TypeCard(val item: Card):Table(){
            override val daoItem: Any
                get() = item
        }
        data class TypeFile(val item: File):Table(){
            override val daoItem: Any
            get() = item
        }
        data class TypeChoice(val item: Choice):Table(){
            override val daoItem: Any
                get() = item
        }
//        TODO 残り
    }


//cards

    val getFileWithNoParents: Flow<List<File>> = fileDao.getFileWithoutParent()

    val cardsWithNoParents: Flow<List<Card>> = cardDao.getCardsWithoutParent()
    fun getCardByFileId(fileId: Int): Flow<List<Card>> {
        return cardDao.getCardsByFileId(fileId)

    }
    fun getLibFilesByFileId(belongingFileId: Int? ): Flow<List<File>>{
        return myDao.getLibCardsByFileId(belongingFileId)
    }


    fun getCardsBySearch(search: String): Flow<List<Card>> {
        return cardDao.searchCardsByWords(search)
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

    suspend fun updateCards(cards: List<Card>) {
        myDao.cardDao.updateMultiple(cards)

    }

    suspend fun deleteCard(card: Card) {
        myDao.cardDao.delete(card)
    }

    suspend fun deleteCards(cards: List<Card>) {
        myDao.cardDao.deleteMultiple(cards)
    }

//

    //files
//    val fileWithNoParents: Flow<List<File>> = fileDao.getFileWithoutParent()
//    fun getFileByFileId(fileId: Int): Flow<List<File>> {
//        return fileDao.getCardsByFileId(fileId)
//
//    }
//
//    @Suppress("RedundantSuspendModifier")
//    @WorkerThread
//    suspend fun insertFile(file: File) {
//        fileDao.insert(file)
//    }
//
//    suspend fun insertFiles(files: List<File>) {
//        fileDao.insertList(files)
//    }
//
//    suspend fun updateFile(file: File) {
//        fileDao.update(file)
//
//    }
//
//    suspend fun updateFiles(files: List<File>) {
//        fileDao.updateMultiple(files)
//
//    }
//
//    suspend fun deleteFile(file: File) {
//        fileDao.delete(file)
//    }
//
//    suspend fun deleteFiles(files: List<File>) {
//        fileDao.deleteMultiple(files)
//    }

//    marker
//    fun getMarkerByCardId(cardId: Int): Flow<List<MarkerData>> {
//        return markerDao.getMarkerDataByCardId(cardId)
//
//    }
//
//    @Suppress("RedundantSuspendModifier")
//    @WorkerThread
//
//    suspend fun insertMarkerData(markerData: List<MarkerData>) {
//        markerDao.insertList(markerData)
//    }
//
//
//    suspend fun updateMarkerData(markerData: List<MarkerData>) {
//        markerDao.updateMultiple(markerData)
//
//    }
//
//    suspend fun deleteMarkerData(markerData: List<MarkerData>) {
//        markerDao.deleteMultiple(markerData)
//    }
// choice
    fun getMarkerByCardId(choiceId: Int): Flow<List<Choice>> {
        return choiceDao.getChoicesById(choiceId)

    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread



    suspend fun updateChoice(choice: Choice) {
        choiceDao.update(choice)

    }

    suspend fun deleteMarkerData(choices: List<Choice>) {
        choiceDao.deleteMultiple(choices)
    }


}