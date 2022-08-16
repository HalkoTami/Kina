package com.example.tangochoupdated.db

import androidx.annotation.WorkerThread
import com.example.tangochoupdated.db.dataclass.*
import com.example.tangochoupdated.db.enumclass.FileStatus
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
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
private val cardAndTagXRefDao  : MyDao.CardAndTagXRefDao,
private val fileXRefDao        : MyDao.FileXRefDao,) {


//cards


    val  getFileWithoutParent:Flow<List<File>> = libraryDao.getFileWithoutParent()
//    fun getFileDataByParentFileId(parentFileId:Int?):Flow<FileWithChild> = libraryDao.getFileListByParentFileId(parentFileId)

    fun getCardDataByFileId(parentFileId: Int?):Flow<List<Card>>  = libraryDao.getCardsDataByFileId(parentFileId)
    fun getCardsByMultipleFileId(fileIdList: List<Int>): Flow<List<Card>> = libraryDao.getCardsByMultipleFileId(fileIdList)
    fun getFileByFileId(fileId:Int?):Flow<File> = libraryDao.getFileByFileId(fileId)
    
        val lastInsertedFile:Flow<Int> = libraryDao.getLastInsertedFile()
        fun mygetFileDataByParentFileId(parentFileId:Int?):Flow<List<File>> = libraryDao.myGetFileByParentFileId(parentFileId)

        fun getAllAncestorsByFileId(fileId: Int?):Flow<List<File>> = libraryDao.getAllAncestorsByChildFileId(fileId)
        fun getAllDescendantsFilesByMultipleFileId(fileIdList: List<Int>):Flow<List<File>> = libraryDao.getAllDescendantsFilesByMultipleFileId(fileIdList)
        fun getAllDescendantsCardsByMultipleFileId(fileIdList: List<Int>):Flow<List<Card>> = libraryDao.getAllDescendantsCardsByMultipleFileId(fileIdList)
//    fun getAllDescendantsByFileIdWithCard(fileId: Int?):Flow<List<Any>> = libraryDao.getAllDescendantsByParentFileId2(fileId)

        fun getCardByCardId(cardId:Int?):Flow<Card> = cardDao.getCardByCardId(cardId)
        val lastInsertedCard:Flow<Card?> = cardDao.getLastInsertedCard()
        fun searchCardsByWords(search:String):Flow<List<Card>> = libraryDao.searchCardsByWords(search)
        fun searchFilesByWords(search:String):Flow<List<File>> = libraryDao.searchFilesByWords(search)

    enum class UpdateFileAmount{
        Folder,FlashCardCover,Card
    }
        fun upDateChild(fileId: Int ,which:UpdateFileAmount){
            Completable.fromAction{when(which){
                UpdateFileAmount.FlashCardCover -> libraryDao.upDateFileChildFlashCardCoversAmount(fileId)
                UpdateFileAmount.Folder -> libraryDao.upDateFileChildFlashCardCoversAmount(fileId)
                UpdateFileAmount.Card -> libraryDao.upDateFileChildCardsAmount(fileId)
            } }
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    fun upDateAncestors(childFileId: Int ,which:UpdateFileAmount){
        Completable.fromAction{when(which){
            UpdateFileAmount.FlashCardCover -> libraryDao.upDateAncestorsFlashCardCoverAmount(childFileId)
            UpdateFileAmount.Folder -> libraryDao.upDateAncestorsFolderAmount(childFileId)
            UpdateFileAmount.Card -> libraryDao.upDateAncestorsCardAmount(childFileId)
        } }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }


    fun upDateChildFilesOfDeletedFile(deletedFileId: Int,newParentFileId:Int?) {
            Completable.fromAction { libraryDao.upDateChildFilesOfDeletedFile(deletedFileId,newParentFileId) }
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
        fun deleteFileAndAllDescendants(fileId:Int){
            Completable.fromAction { libraryDao.deleteFileAndAllDescendants(fileId) }
                .subscribeOn(Schedulers.io())
                .subscribe()
        }

//    anki box
    val allFlashCardCover:Flow<List<File>> = libraryDao.getAllFlashCardCover()



        @Suppress("RedundantSuspendModifier")
        @WorkerThread
        suspend fun insert(item: Any){

            when (item) {
                is CardAndTagXRef -> cardAndTagXRefDao.insert(item)
                is Card -> {
                    cardDao.insert(item)
                    val flashCardCoverId = item.belongingFlashCardCoverId
                    if(flashCardCoverId!=null){
                        upDateChild(flashCardCoverId,UpdateFileAmount.Card)
                        upDateAncestors(flashCardCoverId,UpdateFileAmount.Card)
                    }
                }
                is File -> { fileDao.insert(item)
                    val parentFileId = item.parentFileId
                if(parentFileId!=null){
                    when (item.fileStatus){
                        FileStatus.FOLDER -> {
                            upDateAncestors(parentFileId,UpdateFileAmount.Folder)
                            upDateChild(parentFileId,UpdateFileAmount.Folder)
                        }
                        FileStatus.TANGO_CHO_COVER ->{
                            upDateAncestors(parentFileId,UpdateFileAmount.FlashCardCover)
                            upDateChild(parentFileId,UpdateFileAmount.Folder)
                        }
                    }
                }

                }
                is User -> userDao.insert(item)
                is MarkerData -> markerDataDao.insert(item)
                is Choice -> choiceDao.insert(item)
                is ActivityData -> activityDataDao.insert(item)
                is FileXRef -> { fileXRefDao.insert(item)



                }
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

