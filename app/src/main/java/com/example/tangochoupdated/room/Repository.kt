package com.example.tangochoupdated.room

import androidx.annotation.WorkerThread
import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.Table
import com.example.tangochoupdated.room.rvclasses.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

/// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MyRoomRepository(
    private val myDao: MyDao) {




//cards





    @Suppress("RedundantSuspendModifier")
    @WorkerThread

    suspend  fun getLibRVCover(parentFileId:Int?):Flow<List<LibraryRV>> = flow{
        val finalList = mutableListOf<LibraryRV>()


        suspend fun initFile (parentList:List<File>){
            parentList.onEach {  }
            var a = 0
            while(a<parentList.size){
                val file = parentList[a]
                var containingFolder =0
                var containingFlashCardCover = 0
                var containingCard =0

                fun initContainingCard(cardAmount:Int?){
                    containingCard += cardAmount!!
                }
                suspend fun initFlashCardCover(flashCardCoverList:List<File>?){
                    containingFlashCardCover += flashCardCoverList!!.size
                    var c = 0
                    while(c< flashCardCoverList.size){
                        myDao.libraryDao.getCardIdsByFileId(flashCardCoverList[c].fileId).onEach { value -> initContainingCard(value.size) }.collect()
                        ++c
                    }

                }
                suspend fun getContainingFolder(folder: File?):Int{
                    var b = 0
                    containingFolder += folderList!!.size


                    while (b< folderList.size){
                        myDao.libraryDao.getFilesCountByFileId(folderList[b].fileId).onEach { value -> initContainingFolder(value) }.collect()
                        myDao.libraryDao.getFlashCardCoversCountByFileId(folderList[b].fileId).onEach { value -> initFlashCardCover(value) }
                        ++b
                    }



                }

                myDao.libraryDao.getFilesCountByFileId(file.fileId).onEach { value -> initContainingFolder(value) }.collect()
                val rVFolderData:FolderData
                val rvFlashCardCover:FlashCardCoverData
                when (file.fileStatus){

                    FileStatus.FOLDER-> {
                        rVFolderData = FolderData(
                            id = file.fileId,
                            title = file.title!!,
                            colorStatus = file.colorStatus,
                            containingFolder = containingFolder,
                            containingFlashCardCover = containingFlashCardCover,
                            containingCard = containingCard,
                            position = file.libOrder
                        )
                        finalList . add (LibraryRV.Folder(rVFolderData))
                    }
                    FileStatus.TANGO_CHO_COVER-> {
                        rvFlashCardCover = FlashCardCoverData(
                            id = file.fileId,
                            title = file.title!!,
                            colorStatus = file.colorStatus,
                            containingCard = containingCard,
                            position = file.libOrder
                        )
                        finalList . add (LibraryRV.FlashCardCover(rvFlashCardCover))
                    }
                    else -> return
                }


                ++a

            }

        }

        fun initCard(cardList:List<Card>){
            var a = 0
            while (a<cardList.size){
                val card = cardList[a]
                val stringCardData: StringCardData
                val choiceCardData: ChoiceCardData
                val markerCardData: MarkerCardData

                when (card.cardStatus){
                    CardStatus.MARKER->{
                        markerCardData = MarkerCardData(
                            id = card.id,
                            colorStatus = card.colorStatus!!,
                            position = card.libOrder,
                            markedText = card.markerData?.markerTextPreview
                        )
                        finalList.add(LibraryRV.MarkerCard(markerCardData))

                    }

                    CardStatus.STRING -> {
                        stringCardData = StringCardData(
                            id = card.id,
                            frontTitle = card.stringData?.frontTitle,
                            frontText = card.stringData?.frontText!!,
                            backTitle = card.stringData.backTitle,
                            backText = card.stringData.backText!!,
                            colorStatus = card.colorStatus!!,
                            position = card.libOrder
                        )
                        finalList.add(LibraryRV.StringCard(stringCardData))

                    }

                    CardStatus.CHOICE->{
                        choiceCardData = ChoiceCardData(
                            id = card.id,
                            colorStatus = card.colorStatus!!,
                            position = card.libOrder,
                            question = card.quizData?.question,
                            answerChoice = card.quizData?.answerPreview
                        )
                        finalList.add(LibraryRV.ChoiceCard(choiceCardData))

                    }

                }
                a++
            }
        }




        myDao.libraryDao.getParentFileDataByFileId(parentFileId).onEach {
                value -> value.onEach {
                    finalList.add(LibraryRV.Folder(file = it,
                        containingFolder = myDao.libraryDao.getFolderAmount(it.fileId),
                    ))
                } }.collect()
        myDao.libraryDao.getCardsDataByFileId(parentFileId).onEach { value -> initCard(value) }.collect()
        finalList.sortWith(compareBy {it.position})
        while(true){
            emit(finalList)
        }

    }



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

    suspend fun updateCards(item: List<Any>) {
        myDao.cardDao.updateMultiple(item.filterIsInstance<Card>())
        myDao.fileDao.updateMultiple(item.filterIsInstance<File>())
        myDao.activityDataDao.updateMultiple(item.filterIsInstance<ActivityData>())
        myDao.markerDataDao.updateMultiple(item.filterIsInstance<MarkerData>())
        myDao.choiceDao.updateMultiple(item.filterIsInstance<Choice>())

    }

    suspend fun deleteCard(item: Any) {
        when (item){
            is Card -> myDao.cardDao.delete(item)
            is File -> myDao.fileDao.delete(item)
            is User -> myDao.userDao.delete(item)
            is MarkerData -> myDao.markerDataDao.delete(item)
            is Choice -> myDao.choiceDao.delete(item)
            is ActivityData -> myDao.activityDataDao.delete(item)

        }
        }

    suspend fun deleteCards(item: List<Any>) {
        myDao.cardDao.deleteMultiple(item.filterIsInstance<Card>())
        myDao.fileDao.deleteMultiple(item.filterIsInstance<File>())
        myDao.activityDataDao.deleteMultiple(item.filterIsInstance<ActivityData>())
        myDao.markerDataDao.deleteMultiple(item.filterIsInstance<MarkerData>())
        myDao.choiceDao.deleteMultiple(item.filterIsInstance<Choice>())
    }

    suspend fun clearTable(table:Table){
        when(table.ordinal){
            Table.CARD.ordinal -> myDao.clearTable.clearTblCard()
//            TODO 使うかわからん

        }
    }

//

}