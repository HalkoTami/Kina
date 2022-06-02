package com.example.tangochoupdated.room.rvclasses

import com.example.tangochoupdated.room.enumclass.ColorStatus

enum class LibRVViewType{
    Folder,FlashCardCover,StringCard,MarkerCard,ChoiceCard
}
sealed class LibraryRV(){
    abstract val type:LibRVViewType
    abstract val position:Int
//    tODO make accessible from ListAdapter
    data class Folder (val folder :FolderData):LibraryRV(){
        override val type: LibRVViewType
            get() = LibRVViewType.Folder
        override val position: Int
            get() = folder.position

    }
    data class FlashCardCover(val flashCardCover: FlashCardCoverData):LibraryRV(){
        override val type: LibRVViewType
            get() = LibRVViewType.FlashCardCover
        override val position: Int
            get() = flashCardCover.position
    }
    data class StringCard(val stringCardData: StringCardData):LibraryRV(){
        override val type: LibRVViewType
            get() = LibRVViewType.StringCard
        override val position: Int
            get() = stringCardData.position
    }
    data class ChoiceCard(val choiceCardData: ChoiceCardData):LibraryRV(){
        override val type: LibRVViewType
            get() = LibRVViewType.ChoiceCard
        override val position: Int
            get() = choiceCardData.position
    }
    data class MarkerCard(val markerCardData: MarkerCardData):LibraryRV(){
        override val type: LibRVViewType
            get() = LibRVViewType.MarkerCard
        override val position: Int
            get() = markerCardData.position
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

    data class StringCardData(val id:Int,
                          val frontTitle:String?,
                          val frontText:String,
                          val backTitle:String?,
                          val backText:String,
                          val colorStatus: ColorStatus,
                          val position: Int)

    data class MarkerCardData(val id:Int,
                          val markedText:String?,
                          val colorStatus: ColorStatus,
                          val position: Int)

    data class ChoiceCardData(val id:Int,
                              val question:String?,
                              val answerChoice:String?,
                              val colorStatus: ColorStatus,
                              val position: Int)


