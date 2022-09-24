package com.korokoro.kina.db

import androidx.annotation.WorkerThread
import com.korokoro.kina.db.dao.*
import com.korokoro.kina.db.dataclass.*
import com.korokoro.kina.db.enumclass.XRefType
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.db.typeConverters.FileStatusConverter
import com.korokoro.kina.db.typeConverters.XRefTypeConverter
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
    private val xRefDao            : XRefDao, ) {

    private val xRefTypeConverter = XRefTypeConverter()
    private val xRefTypeCardAsInt = xRefTypeConverter.fromXRefType(XRefType.CARD)
    private val xRefTypeFileAsInt = xRefTypeConverter.fromXRefType(XRefType.ANKI_BOX_FAVOURITE)

    private val fileStatusConverter = FileStatusConverter()
    private val statusFolderAsInt = fileStatusConverter.fromFileStatus(FileStatus.FOLDER)
    private val statusFlashCardAsInt = fileStatusConverter.fromFileStatus(FileStatus.FLASHCARD_COVER)
//cards

    fun getCardDataByFileId(parentFileId: Int?):Flow<List<Card>>  = cardDao.getCardsDataByFileId(parentFileId)
    fun getAllDescendantsCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>> =
        cardDao.getAllDescendantsCardsByMultipleFileId(fileIdList,xRefTypeCardAsInt,xRefTypeFileAsInt)
    fun getDescendantsCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>> =
        cardDao.getDescendantsCardsByMultipleFileId(fileIdList,xRefTypeCardAsInt,xRefTypeFileAsInt)
    fun getCardsByMultipleCardId(cardIds:List<Int>):Flow<List<Card>> = cardDao.getCardByMultipleCardIds(cardIds)
    fun getCardByCardId(cardId:Int?):Flow<Card> = cardDao.getCardByCardId(cardId)
    fun lastInsertedCard(flashCardId: Int?):Flow<Card> = cardDao.getLastInsertedCard(flashCardId)
    fun searchCardsByWords(search:String):Flow<List<Card>> = cardDao.searchCardsByWords(search)
    fun getAnkiBoxRVCards(fileId:Int):Flow<List<Card>> =
        cardDao.getAnkiBoxRVCards(fileId,xRefTypeCardAsInt,xRefTypeFileAsInt)
    fun getDescendantsCardsIsByMultipleFileId(fileIdList: List<Int>):Flow<List<Int>> =
        cardDao.getDescendantsCardsIdsByMultipleFileId(fileIdList,xRefTypeCardAsInt,xRefTypeFileAsInt)
    fun getAnkiBoxFavouriteRVCards(fileId:Int):Flow<List<Card>> =
        cardDao.getAnkiBoxFavouriteRVCards(fileId,xRefTypeCardAsInt,xRefTypeFileAsInt)
    val allCards:Flow<List<Card>> = cardDao.getAllCards()
//    files
    fun getFileByFileId(fileId:Int?):Flow<File> = fileDao.getFileByFileId(fileId)
    fun getFoldersMovableTo(movingFilesId:List<Int>,parentFileId:Int?):Flow<List<File>> = fileDao.getFoldersMovableTo(movingFilesId, parentFileId,1)
    val lastInsertedFile:Flow<File> = fileDao.getLastInsertedFileId()
    fun getFileDataByParentFileId(parentFileId:Int?):Flow<List<File>> = fileDao.myGetFileByParentFileId(parentFileId)
    fun getLibraryItemsWithDescendantCards(parentFileId:Int?):Flow<List<File>> = fileDao.getLibraryItemsWithDescendantCards(parentFileId)

    fun getAllAncestorsByFileId(fileId: Int?):Flow<List<File>> = fileDao.getAllAncestorsByChildFileId(fileId)
    fun getAllDescendantsFilesByMultipleFileId(fileIdList: List<Int>):Flow<List<File>> = fileDao.getAllDescendantsFilesByMultipleFileId(fileIdList)
    fun searchFilesByWords(search:String):Flow<List<File>> = fileDao.searchFilesByWords(search)
    val allFlashCardCoverContainsCard:Flow<List<File>> = fileDao.getAllFlashCardCoverContainsCard()
    fun getMovableFlashCards(movingCardsIds: List<Int>) = fileDao.getFlashCardsMovableTo(movingCardsIds,statusFlashCardAsInt)
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
                FileStatus.FLASHCARD_COVER ->{
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
            is XRef -> xRefDao.insert(item)
            is Card -> insertCard(item)
            is File -> insertFile(item)
            is User -> userDao.insert(item)
            is MarkerData -> markerDataDao.insert(item)
            is Choice -> choiceDao.insert(item)
            is ActivityData -> activityDataDao.insert(item)
            else -> throw  IllegalArgumentException("no such table")

        }

    }
    private fun createCardAndAnkiBoxFavouriteXRef(cardId: Int,favouriteFileId:Int):XRef{
        return XRef(xRefId = 0,
            id1 = cardId,
            id1TokenTable = XRefType.CARD,
            id2 = favouriteFileId,
            id2TokenTable = XRefType.ANKI_BOX_FAVOURITE)
    }

    suspend fun saveCardsToFavouriteAnkiBox(list: List<Card>,lastInsertedFileId:Int,newFile:File){
        insert(newFile)
        val newFileId = lastInsertedFileId + 1
        val xRefList = mutableListOf<XRef>()
        list.onEach {
            xRefList.add(createCardAndAnkiBoxFavouriteXRef(it.id,newFileId)
            ) }
        insertMultiple(xRefList)
    }
    suspend fun insertMultiple(item: List<*>) {

        xRefDao.insertList(item.filterIsInstance<XRef>())
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
        fileDao.upDateAncestorsFlipCount(card.id,card.belongingFlashCardCoverId,xRefTypeCardAsInt,xRefTypeFileAsInt)
    }

    suspend fun update(item: Any) {
        when (item) {
            is XRef -> xRefDao.update(item)
            is Card -> cardDao.update(item)
            is File -> fileDao.update(item)
            is User -> userDao.update(item)
            is MarkerData -> markerDataDao.update(item)
            is Choice -> choiceDao.update(item)
            is ActivityData -> activityDataDao.update(item)

        }

    }

    suspend fun updateMultiple(item: List<Any>) {
        xRefDao.updateMultiple(item.filterIsInstance<XRef>())
        cardDao.updateMultiple(item.filterIsInstance<Card>())
        fileDao.updateMultiple(item.filterIsInstance<File>())
        activityDataDao.updateMultiple(item.filterIsInstance<ActivityData>())
        markerDataDao.updateMultiple(item.filterIsInstance<MarkerData>())
        choiceDao.updateMultiple(item.filterIsInstance<Choice>())

    }

    suspend fun delete(item: Any) {
        when (item) {
            is XRef -> xRefDao.delete(item)
            is Card -> cardDao.delete(item)
            is File -> fileDao.delete(item)
            is User -> userDao.delete(item)
            is MarkerData -> markerDataDao.delete(item)
            is Choice -> choiceDao.delete(item)
            is ActivityData -> activityDataDao.delete(item)

        }
    }

    suspend fun deleteMultiple(item: List<Any>) {
        xRefDao.deleteMultiple(item.filterIsInstance<XRef>())
        cardDao.deleteMultiple(item.filterIsInstance<Card>())
        fileDao.deleteMultiple(item.filterIsInstance<File>())
        activityDataDao.deleteMultiple(item.filterIsInstance<ActivityData>())
        markerDataDao.deleteMultiple(item.filterIsInstance<MarkerData>())
        choiceDao.deleteMultiple(item.filterIsInstance<Choice>())
    }

}








//




//

