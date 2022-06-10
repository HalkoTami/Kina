package com.example.tangochoupdated.room

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.*
import kotlinx.coroutines.flow.*

/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MyRoomRepository(
private val cardDao            : MyDao.CardDao,
private val activityDataDao    : MyDao.ActivityDataDao,
private val choiceDao          : MyDao.ChoiceDao,
private val fileDao            : MyDao.FileDao,
private val markerDataDao      : MyDao.MarkerDataDao,
private val userDao            : MyDao.UserDao,
private val clearTable         : MyDao.ClearTable,
private val libraryDao         : MyDao.LibraryDao,
private val cardAndTagXRefDao  : MyDao.CardAndTagXRefDao) {


//cards
    fun getFileWithoutParent():Flow<List<File>>{
        return libraryDao.getFileWithoutParent()
    }
    fun getFileDataByFileId(parentFileId:Int):Flow<List<File>>{
        return libraryDao.getFileDataByFileId(parentFileId)
    }
    fun getCardDataByFileId(parentFileId: Int):Flow<List<CardAndTags>>{
        return  libraryDao.getCardsDataByFileId(parentFileId)
    }


    suspend fun getLibRVCover(parentFileId: Int?): ArrayList<LibraryRV> {


        fun convertFileToLibraryRV(file: File?): LibraryRV {

            when (file!!.fileStatus) {

                FileStatus.FOLDER -> {
                    return LibraryRV(
                        type = LibRVViewType.Folder,
                        file = file,
                        tag = null,
                        card = null,
                        position = file.libOrder
                    )

                }

                FileStatus.TANGO_CHO_COVER ->
                    return LibraryRV(
                        type = LibRVViewType.FlashCardCover,
                        file = file,
                        tag = null,
                        card = null,
                        position = file.libOrder
                    )


                else -> return LibraryRV(LibRVViewType.Folder, 0, null, null, null)
            }

        }

        fun convertCardToLibraryRV(card: CardAndTags?): LibraryRV {
            when (card?.card?.cardStatus) {
                CardStatus.STRING -> return LibraryRV(
                    type = LibRVViewType.StringCard,
                    file = null,
                    tag = card.tags,
                    card = card.card,
                    position = card.card.libOrder
                )
                CardStatus.CHOICE -> return LibraryRV(
                    type = LibRVViewType.ChoiceCard,
                    file = null,
                    tag = card.tags,
                    card = card.card,
                    position = card.card.libOrder
                )

                CardStatus.MARKER -> return LibraryRV(
                    type = LibRVViewType.MarkerCard,
                    file = null,
                    tag = card.tags,
                    card = card.card,
                    position = card.card.libOrder
                )

                else -> return LibraryRV(LibRVViewType.Folder, 0, null, null, null)
            }
        }






        if (parentFileId == null) {
            val finalList = arrayListOf<LibraryRV>()
            libraryDao.getFileWithoutParent().collect {
                it.onEach { finalList.add(convertFileToLibraryRV(it)) }
            }
            val a = finalList[0].file?.title
            return finalList


        } else {
            val finalList = arrayListOf<LibraryRV>()
            libraryDao.getFileDataByFileId(parentFileId).collect { file ->
                file.onEach { finalList.add(convertFileToLibraryRV(it)) }

            }
            libraryDao.getCardsDataByFileId(parentFileId).collect() { card ->
                card.onEach { finalList.add(convertCardToLibraryRV(it)) }
            }
            val a = finalList[0].file?.title
            return finalList
        }
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Any) {

        when (item) {
            is CardAndTagXRef -> cardAndTagXRefDao.insert(item)
            is Card -> cardDao.insert(item)
            is File -> fileDao.insert(item)
            is User -> userDao.insert(item)
            is MarkerData -> markerDataDao.insert(item)
            is Choice -> choiceDao.insert(item)
            is ActivityData -> activityDataDao.insert(item)

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


    suspend fun updateCard(item: Any) {
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

    suspend fun updateCards(item: List<Any>) {
        cardAndTagXRefDao.updateMultiple(item.filterIsInstance<CardAndTagXRef>())
        cardDao.updateMultiple(item.filterIsInstance<Card>())
        fileDao.updateMultiple(item.filterIsInstance<File>())
        activityDataDao.updateMultiple(item.filterIsInstance<ActivityData>())
        markerDataDao.updateMultiple(item.filterIsInstance<MarkerData>())
        choiceDao.updateMultiple(item.filterIsInstance<Choice>())

    }

    suspend fun deleteCard(item: Any) {
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

    suspend fun deleteCards(item: List<Any>) {
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

