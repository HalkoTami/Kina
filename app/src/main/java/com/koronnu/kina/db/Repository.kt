package com.koronnu.kina.db

import androidx.annotation.WorkerThread
import com.koronnu.kina.actions.DateTimeActions
import com.koronnu.kina.db.dao.*
import com.koronnu.kina.db.dataclass.*
import com.koronnu.kina.db.enumclass.ActivityStatus
import com.koronnu.kina.db.enumclass.XRefType
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.db.typeConverters.ActivityStatusConverter
import com.koronnu.kina.db.typeConverters.FileStatusConverter
import com.koronnu.kina.db.typeConverters.XRefTypeConverter
import kotlinx.coroutines.flow.*
import java.util.*

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
//lastInsertedItems
    val lastInsertedCard                                                    :Flow<Card>         = cardDao.getLastInsertedCard()
    val lastInsertedFile                                                    :Flow<File>         = fileDao.getLastInsertedFileId()


    fun getCardDataByFileId                    (parentFileId: Int?)         :Flow<List<Card>>   = cardDao.getCardsDataByFileId(parentFileId)
    fun getCardDataByFileIdSorted              (parentFileId: Int?)         :Flow<List<Card>>   = cardDao.getCardsDataByFileIdSorted(parentFileId)

    fun getAllDescendantsCardsByMultipleFileId (fileIdList: List<Int>)      :Flow<List<Card>>   = cardDao.getAllDescendantsCardsByMultipleFileId(fileIdList,xRefTypeCardAsInt,xRefTypeFileAsInt)
    fun getCardsByMultipleCardId               (cardIds:List<Int>)          :Flow<List<Card>>   = cardDao.getCardByMultipleCardIds(cardIds)
    fun getCardByCardId                        (cardId:Int?)                :Flow<Card>         = cardDao.getCardByCardId(cardId)
    fun searchCardsByWords                     (search:String)              :Flow<List<Card>>   = cardDao.searchCardsByWords(search)
    fun getAnkiBoxRVCards                      (fileId:Int)                 :Flow<List<Card>>   = cardDao.getAnkiBoxRVCards(fileId,xRefTypeCardAsInt,xRefTypeFileAsInt)
    fun getDescendantsCardsIsByMultipleFileId  (fileIdList: List<Int>)      :Flow<List<Int>>    = cardDao.getDescendantsCardsIdsByMultipleFileId(fileIdList,xRefTypeCardAsInt,xRefTypeFileAsInt)
    val allCards                                                            :Flow<List<Card>>   = cardDao.getAllCards()
    val cardExists                                                          :Flow<Boolean>      = cardDao.checkCardExists().map { it!=null }
//    files
    fun getFileByFileId                        (fileId:Int?)                :Flow<File>         = fileDao.getFileByFileId(fileId)
    fun getFileAndChildrenCards                (fileId:Int?)                :Flow<Map<File,List<Card>>> = fileDao.getFileChildrenCards(fileId)
    fun getFileDataByParentFileId              (parentFileId:Int?)          :Flow<List<File>?>           = fileDao.myGetFileByParentFileId(parentFileId)
    fun getLibraryItemsWithDescendantCards     (parentFileId:Int?)          :Flow<List<File>>           = fileDao.getLibraryItemsWithDescendantCards(parentFileId)
    fun getAnkiBoxRVDescendantsFolders         (fileId:Int)                 :Flow<List<File>>           = fileDao.getAnkiBoxRVDescendantsFiles(fileId,statusFolderAsInt)
    fun getAnkiBoxRVDescendantsFlashCards      (fileId:Int)                 :Flow<List<File>>           = fileDao.getAnkiBoxRVDescendantsFiles(fileId,statusFlashCardAsInt)
    fun getAllAncestorsByFileId                (fileId: Int?)               :Flow<List<File>>           = fileDao.getAllAncestorsByChildFileId(fileId)
    fun getAllDescendantsFilesByMultipleFileId (fileIdList: List<Int>)      :Flow<List<File>>           = fileDao.getAllDescendantsFilesByMultipleFileId(fileIdList)
    fun searchFilesByWords                     (search:String)              :Flow<List<File>>           = fileDao.searchFilesByWords(search)
    val allFlashCardCoverContainsCard                                       :Flow<List<File>>           = fileDao.getAllFlashCardCoverContainsCard()
    fun getMovableFlashCards                   (movingCardsIds: List<Int>)  :Flow<Map<File, List<Card>>> = fileDao.getFlashCardsMovableTo(movingCardsIds,statusFlashCardAsInt)
    val allFavouriteAnkiBox                                                 :Flow<List<File>>           = fileDao.getAllFavouriteAnkiBox()
//    activity
    fun getCardActivity                        (cardId:Int)                 :Flow<List<ActivityData>>       = activityDataDao.getActivityDataByCard(cardId)
    val allActivity                                                         :Flow<List<ActivityData>>       = activityDataDao.getAllActivityData()
    val lastFlipRoundDuration:Flow<Int>                         =
        activityDataDao.getLastActivityStartedData(
            ActivityStatusConverter().fromActivityStatus(ActivityStatus.FLIP_ROUND_STARTED)).map {
                val startedDate:Date = DateTimeActions().fromStringToDate(it.dateTime)!!
                val now = Date()
                DateTimeActions().getTimeDifference(startedDate, now,DateTimeActions.TimeUnit.MINUTES)
        }

    suspend fun upDateChildFilesOfDeletedFile(deletedFileId: Int,newParentFileId:Int?) {
        fileDao.upDateChildFilesOfDeletedFile(deletedFileId,newParentFileId)
    }
    suspend fun deleteFileAndAllDescendants(fileId:Int){
        fileDao.deleteFileAndAllDescendants(fileId)
        cardDao.deleteAllDescendantsCardsByFileId(fileId,xRefTypeCardAsInt,xRefTypeFileAsInt)

    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Any){

        when (item) {
            is XRef -> xRefDao.insert(item)
            is Card -> cardDao.insert(item)
            is File -> fileDao.insert(item)
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
            id1TokenXRefType = XRefType.CARD,
            id2 = favouriteFileId,
            id2TokenXRefType = XRefType.ANKI_BOX_FAVOURITE)
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

//    suspend fun updateCardFlippedTime(card:Card){
//        cardDao.upDateCardFlippedTimes(card.id)
//    }

    suspend fun updateCardRememberedStatus(card:Card,remembered:Boolean){
        if(card.remembered==remembered) return
        card.remembered = remembered
        cardDao.update(card)
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

