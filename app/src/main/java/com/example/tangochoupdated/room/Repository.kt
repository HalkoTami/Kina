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


    val allFolder:Flow<File> = libraryDao.getFolder()
    fun a(id:Int):Flow<List<File>>{
        return  libraryDao.getFileWithoutParent()
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread

    suspend fun getLibRVCover(parentFileId: Int?): List<LibraryRV> {
        val finalList = mutableListOf<LibraryRV>()

        var containingFolder = 0
        var containingFlashCardCover = 0
        var containingCard = 0

        suspend fun getContainingItemsAmount(file: File?) {
            when (file?.fileStatus) {
                FileStatus.FOLDER -> {
                    libraryDao.getFilesCountByFileId(file.fileId).collect { list ->
                        containingFolder += (
                                list.filter { oneFile ->
                                    oneFile.fileStatus == FileStatus.FOLDER
                                }.size)
                        containingFlashCardCover += (
                                list.filter { oneFile ->
                                    oneFile.fileStatus == FileStatus.TANGO_CHO_COVER
                                }.size)
                        list.onEach { oneFile ->
                            getContainingItemsAmount(oneFile)
                        }
                    }
                }
                FileStatus.TANGO_CHO_COVER -> {
                    libraryDao.getCardIdsByFileId(file.fileId).collect { list ->
                        containingCard += (list.size)
                    }
                }
                else -> Log.d("getLibRVCover", "${file?.fileId} unknown file status")
            }


        }

        suspend fun getTagsList(file: List<File>): MutableList<TagData> {
            val tagList = mutableListOf<TagData>()
            file.filter { file -> file.fileStatus == FileStatus.TAG }.onEach { file ->
                tagList.add(
                    TagData(
                        tagId = file.fileId,
                        tagText = file.title!!
                    )
                )
            }
            return tagList
        }




        if (parentFileId == null) {
            libraryDao.getFileWithoutParent().collect { value
                ->
                value.onEach { file ->
                    getContainingItemsAmount(file)
                    when (file.fileStatus) {
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
                        else -> Log.d("getLibRVCover", "unknown file status")
                    }
                }
            }

        } else {
            libraryDao.getFileDataByFileId(parentFileId!!).collect { value
                ->
                value.onEach { file ->
                    getContainingItemsAmount(file)
                    when (file.fileStatus) {
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
                        else -> Log.d("getLibRVCover", "unknown file status")
                    }
                }
            }

            libraryDao.getCardsDataByFileId(parentFileId).collect { list ->
                list.filter { card -> card.card.cardStatus == CardStatus.STRING }.onEach { card ->
                    finalList.add(
                        LibraryRV.StringCard(
                            card.card,
                            getTagsList(card.tags)
                        )
                    )
                }
                list.filter { card -> card.card.cardStatus == CardStatus.CHOICE }.onEach { card ->
                    finalList.add(
                        LibraryRV.ChoiceCard(
                            card.card,
                            getTagsList(card.tags)
                        )
                    )
                }
                list.filter { card -> card.card.cardStatus == CardStatus.MARKER }.onEach { card ->
                    finalList.add(LibraryRV.MarkerCard(card.card, getTagsList(card.tags)))
                }

            }
        }

            finalList.sortWith(compareBy { it.position })
        return finalList
    }




    suspend fun insert(item: Any) {

        when (item ){
            is CardAndTagXRef -> cardAndTagXRefDao  .insert(item)
            is Card           -> cardDao            .insert(item)
            is File           -> fileDao            .insert(item)
            is User           -> userDao            .insert(item)
            is MarkerData     -> markerDataDao      .insert(item)
            is Choice         -> choiceDao          .insert(item)
            is ActivityData   -> activityDataDao    .insert(item)

        }

    }
    suspend fun insertMultiple(item: List<*>) {

        cardAndTagXRefDao      .insertList(item.filterIsInstance<CardAndTagXRef>())
        cardDao                .insertList(item.filterIsInstance<Card>())
        fileDao                .insertList(item.filterIsInstance<File>())
        activityDataDao        .insertList(item.filterIsInstance<ActivityData>())
        markerDataDao          .insertList(item.filterIsInstance<MarkerData>())
        choiceDao              .insertList(item.filterIsInstance<Choice>())



    }



    suspend fun updateCard(item: Any) {
        when (item ){
            is CardAndTagXRef -> cardAndTagXRefDao  .update(item)
            is Card           -> cardDao            .update(item)
            is File           -> fileDao            .update(item)
            is User           -> userDao            .update(item)
            is MarkerData     -> markerDataDao      .update(item)
            is Choice         -> choiceDao          .update(item)
            is ActivityData   -> activityDataDao    .update(item)

        }

    }

    suspend fun updateCards(item: List<Any>) {
        cardAndTagXRefDao      .updateMultiple(item.filterIsInstance<CardAndTagXRef>())
        cardDao                .updateMultiple(item.filterIsInstance<Card>())
        fileDao                .updateMultiple(item.filterIsInstance<File>())
        activityDataDao        .updateMultiple(item.filterIsInstance<ActivityData>())
        markerDataDao          .updateMultiple(item.filterIsInstance<MarkerData>())
        choiceDao              .updateMultiple(item.filterIsInstance<Choice>())

    }

    suspend fun deleteCard(item: Any) {
        when (item){
            is CardAndTagXRef -> cardAndTagXRefDao  .delete(item)
            is Card           -> cardDao            .delete(item)
            is File           -> fileDao            .delete(item)
            is User           -> userDao            .delete(item)
            is MarkerData     -> markerDataDao      .delete(item)
            is Choice         -> choiceDao          .delete(item)
            is ActivityData   -> activityDataDao    .delete(item)

        }
        }

    suspend fun deleteCards(item: List<Any>) {
        cardAndTagXRefDao      .deleteMultiple(item.filterIsInstance<CardAndTagXRef>())
        cardDao                .deleteMultiple(item.filterIsInstance<Card>())
        fileDao                .deleteMultiple(item.filterIsInstance<File>())
        activityDataDao        .deleteMultiple(item.filterIsInstance<ActivityData>())
        markerDataDao          .deleteMultiple(item.filterIsInstance<MarkerData>())
        choiceDao              .deleteMultiple(item.filterIsInstance<Choice>())
    }




//




//

}