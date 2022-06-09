package com.example.tangochoupdated.room

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import org.w3c.dom.Document

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



     suspend fun getLibRVCover(parentFileId: Int?): List<LibraryRV> {
        val finalList = mutableListOf<LibraryRV>()

        var containingFolder = 0
        var containingFlashCardCover = 0
        var containingCard = 0

         fun getContainingItemsAmount(file: File?) {
            when (file?.fileStatus) {
                FileStatus.FOLDER -> {
                    var containingFiles:List<File> = libraryDao.getFileDataByFileId(file.fileId)
                     val folders= containingFiles.filter { file ->
                        file.fileStatus== FileStatus.FOLDER}
                    containingFolder +=folders.size
                    val flashCardCovers = containingFiles.filter { file->
                        file.fileStatus == FileStatus.TANGO_CHO_COVER }
                    containingFlashCardCover += flashCardCovers.size
//                    containingFiles.onEach { getContainingItemsAmount(it) }
                    }
                FileStatus.TANGO_CHO_COVER -> {
                 containingCard +=  libraryDao.getCardAmountByFileId(file.fileId)
                }
                else -> Log.d("getLibRVCover", "${file?.fileId} unknown file status")
            }






        }
        fun addToFinalList(file:File?,card:CardAndTags?){
            when (file!!.fileStatus) {
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
            val mytags = mutableListOf<TagData>()
            card?.tags?.onEach { mytags.add(TagData(it.fileId,it.title.orEmpty())) }
            when (card?.card?.cardStatus) {
                CardStatus.STRING->finalList.add(
                    LibraryRV.StringCard(
                        card = card.card,
                        tags = mytags
                    ))
                CardStatus.CHOICE ->finalList.add(
                    LibraryRV.ChoiceCard(
                        card = card.card,
                        tags = mytags
                    )
                )
                CardStatus.MARKER ->finalList.add(
                    LibraryRV.MarkerCard(
                        card = card.card,
                        tags = mytags
                    )
                )
                else -> return
            }
        }






        if (parentFileId == null) {
            libraryDao.getFileWithoutParent().collect { file ->
                file.onEach { file ->  getContainingItemsAmount(file)
                    addToFinalList(file,null)}

                }
            } else {
            libraryDao.getFileDataByFileId(parentFileId).onEach { file ->
                    getContainingItemsAmount(file)
                    addToFinalList(file,null)

                }
            libraryDao.getCardsDataByFileId(parentFileId).onEach { addToFinalList(null,it) }
            }

        finalList.sortWith(compareBy { it.position })
        return finalList
    }



    @Suppress("RedundantSuspendModifier")
    @WorkerThread
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