package com.example.tangochoupdated.room

import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.FileStatus
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
    fun getFileDataByParentFileId(parentFileId:Int?):Flow<List<FileWithChild>>{
        return libraryDao.getFileListByParentFileId(parentFileId)
    }
    fun getCardDataByFileId(parentFileId: Int):Flow<List<CardAndTags>>{
        return  libraryDao.getCardsDataByFileId(parentFileId)
    }
    fun getFileByFileId(fileId:Int):Flow<File>{
        return libraryDao.getFileByFileId(fileId)
    }





    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Any) {

        when (item) {
            is CardAndTagXRef -> cardAndTagXRefDao.insert(item)
            is Card -> cardDao.insert(item)
            is File -> {
//
//                suspend fun getOverGrandParent(file:File){
//                    if(file.hasParent){
//                        var overGrandparentFile = mutableListOf<File>()
//                        libraryDao.getFileByFileId(file.parentFile).collect{
//                            overGrandparentFile.add(it)
//                        }
//                        when(file.fileStatus){
//                            FileStatus.FOLDER ->
//                                overGrandparentFile[0].childFoldersAmount = overGrandparentFile[0].childFoldersAmount?.plus(1)
//                            FileStatus.TANGO_CHO_COVER ->
//                                overGrandparentFile[0].childFlashCardCoversAmount = overGrandparentFile[0].childFlashCardCoversAmount?.plus(1)
//                        }
//                        overGrandparentFile[0].hasChild = true
//                        update(overGrandparentFile[0])
//                        getOverGrandParent(overGrandparentFile[0])
//
//                    } else return
//
//                }
//                getOverGrandParent(item)
                fileDao.insert(item)
            }
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

