package com.example.tangochoupdated

import android.provider.SyncStateContract.Helpers.insert
import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.LibraryDao
import com.example.tangochoupdated.room.MyDao
import com.example.tangochoupdated.room.dataclass.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MyRoomRepository(
    private val fileDao: FileDao,
    private val cardDao: CardDao,
    private val userDao: UserDao,
    private val markerDao: MarkerDataDao,
    private val activityDao: ActivityDataDao,
    private val choiceDao: ChoiceDao,
    private val libraryDao: MyDao) {


//cards

    val getFileWithNoParents: Flow<List<File>> = fileDao.getFileWithoutParent()

    val cardsWithNoParents: Flow<List<Card>> = cardDao.getCardsWithoutParent()
    fun getCardByFileId(fileId: Int): Flow<List<Card>> {
        return cardDao.getCardsByFileId(fileId)

    }
    fun getLibFilesByFileId(belongingFileId: Int? ): Flow<List<File>>{
        return libraryDao.getLibCardsByFileId(belongingFileId)
    }


    fun getCardsBySearch(search: String): Flow<List<Card>> {
        return cardDao.searchCardsByWords(search)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCard(card: Card) {
        libraryDao.cardDao.insert(card)
    }

    suspend fun insertCards(cards: List<Card>) {
        cardDao.insertList(cards)
    }

    suspend fun updateCard(card: Card) {
        cardDao.update(card)

    }

    suspend fun updateCards(cards: List<Card>) {
        cardDao.updateMultiple(cards)

    }

    suspend fun deleteCard(card: Card) {
        cardDao.delete(card)
    }

    suspend fun deleteCards(cards: List<Card>) {
        cardDao.deleteMultiple(cards)
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