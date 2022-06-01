package com.example.tangochoupdated.room.rvclasses

import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.ColorStatus

enum class LibRVViewType{
    Folder,FlashCardCover,StringCard,MarkerCard,ChoiceCard
}
sealed class LibraryRV(){
    abstract val type:LibRVViewType
    data class Folder (val folder :FolderData):LibraryRV(){
        override val type: LibRVViewType
            get() = LibRVViewType.Folder

    }
    data class FlashCardCover(val flashCardCover: FlashCardCover):LibraryRV(){
        override val type: LibRVViewType
            get() = LibRVViewType.FlashCardCover
    }

}
data class FolderData(var id: Int,
                      val title:String,
                      val containingFolder:Int,
                      val containingFlashCardCover:Int,
                      val containingCard:Int,
                      val colorStatus: ColorStatus,
                      val position: Int){


}


    data class FlashCardCoverData(val id:Int,
                      val title:String,
                      val containingCard:Int,
                      val colorStatus: ColorStatus,
                              val position: Int)

    data class StringCard(val id:Int,
                          val frontTitle:String?,
                          val frontText:String,
                          val backTitle:String?,
                          val backText:String,
                          val colorStatus: ColorStatus,
                          val position: Int)

    data class MarkerCard(val id:Int,
                          val markedText:String?,
                          val colorStatus: ColorStatus,
                          val position: Int)

    data class QuizCard(val id:Int,
                          val question:String?,
                        val answerChoice:String?,
                          val colorStatus: ColorStatus,
                        val position: Int)


