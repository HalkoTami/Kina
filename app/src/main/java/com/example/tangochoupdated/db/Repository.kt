package com.example.tangochoupdated.db

import androidx.annotation.WorkerThread
import com.example.tangochoupdated.db.dao.*
import com.example.tangochoupdated.db.dataclass.*
import com.example.tangochoupdated.db.dataclass.ChoiceDao
import com.example.tangochoupdated.db.enumclass.FileStatus
import kotlinx.coroutines.flow.*

/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MyRoomRepository(
    private val cardDao            : CardDao,
    private val activityDataDao    : ActivityDataDao,
    private val choiceDao          : ChoiceDao,
    private val fileDao            : FileDao,
    private val markerDataDao      : MarkerDataDao,
    private val userDao            : UserDao,
    private val cardAndTagXRefDao  : CardAndTagXRefDao,
    private val fileXRefDao        : FileXRefDao,) {


//cards

    fun getCardDataByFileId(parentFileId: Int?):Flow<List<Card>>  = cardDao.getCardsDataByFileId(parentFileId)
    fun getAllDescendantsCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>> = cardDao.getAllDescendantsCardsByMultipleFileId(fileIdList)
    fun getDescendantsCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>> = cardDao.getDescendantsCardsByMultipleFileId(fileIdList)
    fun getCardsByMultipleCardId(cardIds:List<Int>):Flow<List<Card>> = cardDao.getCardByMultipleCardIds(cardIds)
    fun getCardByCardId(cardId:Int?):Flow<Card> = cardDao.getCardByCardId(cardId)
    fun lastInsertedCard(flashCardId: Int?):Flow<Card> = cardDao.getLastInsertedCard(flashCardId)
    fun searchCardsByWords(search:String):Flow<List<Card>> = cardDao.searchCardsByWords(search)
    fun getAnkiBoxRVCards(fileId:Int):Flow<List<Card>> = cardDao.getAnkiBoxRVCards(fileId)
    fun getDescendantsCardsIsByMultipleFileId(fileIdList: List<Int>):Flow<List<Int>> = cardDao.getDescendantsCardsIdsByMultipleFileId(fileIdList)
    fun getAnkiBoxFavouriteRVCards(fileId:Int):Flow<List<Card>> = cardDao.getAnkiBoxFavouriteRVCards(fileId)
    val allCards:Flow<List<Card>> = cardDao.getAllCards()
//    files
    fun getFileByFileId(fileId:Int?):Flow<File> = fileDao.getFileByFileId(fileId)
    val lastInsertedFile:Flow<Int> = fileDao.getLastInsertedFileId()
    fun getFileDataByParentFileId(parentFileId:Int?):Flow<List<File>> = fileDao.myGetFileByParentFileId(parentFileId)
    fun getAllAncestorsByFileId(fileId: Int?):Flow<List<File>> = fileDao.getAllAncestorsByChildFileId(fileId)
    fun getAllDescendantsFilesByMultipleFileId(fileIdList: List<Int>):Flow<List<File>> = fileDao.getAllDescendantsFilesByMultipleFileId(fileIdList)
    fun searchFilesByWords(search:String):Flow<List<File>> = fileDao.searchFilesByWords(search)
    val allFlashCardCover:Flow<List<File>> = fileDao.getAllFlashCardCover()
    val allFavouriteAnkiBox:Flow<List<File>> = fileDao.getAllFavouriteAnkiBox()
//    activity
    fun getCardActivity(cardId:Int):Flow<List<ActivityData>> = activityDataDao.getActivityDataByCard(cardId)


    suspend fun upDateChildFilesOfDeletedFile(deletedFileId: Int,newParentFileId:Int?) {
        fileDao.upDateChildFilesOfDeletedFile(deletedFileId,newParentFileId)
    }
    suspend fun deleteFileAndAllDescendants(fileId:Int){
        fileDao.deleteFileAndAllDescendants(fileId)
    }

    private suspend fun insertCard(card: Card){
        cardDao.upDateCardsPositionBeforeInsert(card.belongingFlashCardCoverId,card.libOrder)
        cardDao.insert(card)
        val flashCardCoverId = card.belongingFlashCardCoverId
        if(flashCardCoverId!=null){
            fileDao.upDateFileChildCardsAmount(flashCardCoverId,1)
            fileDao.upDateAncestorsCardAmount(flashCardCoverId,1)
        }
    }
    private suspend fun insertFile(file: File){
        fileDao.insert(file)
        val parentFileId = file.parentFileId
        if(parentFileId!=null){
            when (file.fileStatus){
                FileStatus.FOLDER -> {
                    fileDao.upDateAncestorsFolderAmount(parentFileId,1)
                    fileDao.upDateFileChildFoldersAmount(parentFileId,1)
                }
                FileStatus.TANGO_CHO_COVER ->{
                    fileDao.upDateAncestorsFlashCardCoverAmount(parentFileId,1)
                    fileDao.upDateFileChildFlashCardCoversAmount(parentFileId,1)
                }
                else -> return
            }
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Any){

        when (item) {
            is CardAndTagXRef -> cardAndTagXRefDao.insert(item)
            is Card -> insertCard(item)
            is File -> insertFile(item)
            is User -> userDao.insert(item)
            is MarkerData -> markerDataDao.insert(item)
            is Choice -> choiceDao.insert(item)
            is ActivityData -> activityDataDao.insert(item)
            is FileXRef -> fileXRefDao.insert(item)
            else -> throw  IllegalArgumentException("no such table")

        }

    }


    suspend fun insertMultiple(item: List<*>) {

        cardAndTagXRefDao.insertList(item.filterIsInstance<CardAndTagXRef>())
        cardDao.insertList(item.filterIsInstance<Card>())
        fileDao.insertList(item.filterIsInstance<File>())
        activityDataDao.insertList(item.filterIsInstance<ActivityData>())
        markerDataDao.insertList(item.filterIsInstance<MarkerData>())
        choiceDao.insertList(item.filterIsInstance<Choice>())


    }

    suspend fun upDateAncestorsAndChild(item:Card, newParentFileId: Int?){
        val oldParent = item.belongingFlashCardCoverId
        if(oldParent!=null){
            fileDao.upDateAncestorsCardAmount(oldParent,-1)
            fileDao.upDateFileChildCardsAmount(oldParent,-1)
        }
        if(newParentFileId!=null){
            fileDao.upDateAncestorsCardAmount(newParentFileId,1)
            fileDao.upDateFileChildCardsAmount(newParentFileId,1)
        }

    }
    suspend fun updateCardFlippedTime(card:Card){
        cardDao.upDateCardFlippedTimes(card.id)
        fileDao.upDateAncestorsFlipCount(card.id,card.belongingFlashCardCoverId)
    }

    suspend fun update(item: Any) {
        when (item) {
            is CardAndTagXRef -> cardAndTagXRefDao.update(item)
            is Card -> cardDao.update(item)
            is File -> fileDao.update(item)
            is User -> userDao.update(item)
            is MarkerData -> markerDataDao.update(item)
            is Choice -> choiceDao.update(item)
            is ActivityData -> activityDataDao.update(item)

        }

    }

    suspend fun updateMultiple(item: List<Any>) {
        cardAndTagXRefDao.updateMultiple(item.filterIsInstance<CardAndTagXRef>())
        cardDao.updateMultiple(item.filterIsInstance<Card>())
        fileDao.updateMultiple(item.filterIsInstance<File>())
        activityDataDao.updateMultiple(item.filterIsInstance<ActivityData>())
        markerDataDao.updateMultiple(item.filterIsInstance<MarkerData>())
        choiceDao.updateMultiple(item.filterIsInstance<Choice>())

    }

    suspend fun delete(item: Any) {
        when (item) {
            is CardAndTagXRef -> cardAndTagXRefDao.delete(item)
            is Card -> cardDao.delete(item)
            is File -> fileDao.delete(item)
            is User -> userDao.delete(item)
            is MarkerData -> markerDataDao.delete(item)
            is Choice -> choiceDao.delete(item)
            is ActivityData -> activityDataDao.delete(item)

        }
    }

    suspend fun deleteMultiple(item: List<Any>) {
        cardAndTagXRefDao.deleteMultiple(item.filterIsInstance<CardAndTagXRef>())
        cardDao.deleteMultiple(item.filterIsInstance<Card>())
        fileDao.deleteMultiple(item.filterIsInstance<File>())
        activityDataDao.deleteMultiple(item.filterIsInstance<ActivityData>())
        markerDataDao.deleteMultiple(item.filterIsInstance<MarkerData>())
        choiceDao.deleteMultiple(item.filterIsInstance<Choice>())
    }

}








//




//

