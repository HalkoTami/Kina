package com.example.tangochoupdated.room.rvclasses

import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.ColorStatus

enum class LibRVViewType{
    Folder,FlashCardCover,StringCard,MarkerCard,ChoiceCard
}
sealed class LibraryRV(
    val type:LibRVViewType,
    val position:Int,
    val id:Int,
    val colorStatus:ColorStatus?
){

    abstract val markerCardData:MarkerCardData?
    abstract val stringCardData: StringCardData?
    abstract val choiceCardData: ChoiceCardData?
    abstract val folderData:FolderData?
    abstract val flashCardCoverData:FlashCardCoverData?
    abstract val tagData:MutableList<TagData>?
//    tODO make accessible from ListAdapter
    data class Folder (val file: File,
                       val containingCard: Int,
                       val containingFolder: Int,
                       val containingFlashCardCover: Int):LibraryRV(
    type = LibRVViewType.Folder,
    position = file.libOrder,
    colorStatus = file.colorStatus,
    id = file.fileId
                       ){

    override val markerCardData: MarkerCardData?
        get() = null

    override val stringCardData: StringCardData?
        get() = null

    override val choiceCardData: ChoiceCardData?
        get() = null

    override val folderData: FolderData
        get() = FolderData(
            title = file.title,
            containingFolder = containingFolder,
            containingFlashCardCover = containingFlashCardCover,
            containingCard=containingCard)

    override val flashCardCoverData: FlashCardCoverData?
        get() = null

    override val tagData: MutableList<TagData>?
        get() = null

}

    data class FlashCardCover(
        val file: File,
        val containingCard: Int):LibraryRV(
        type = LibRVViewType.FlashCardCover,
        position = file.libOrder,
        colorStatus = file.colorStatus,
        id = file.fileId
    ){
        override val markerCardData: MarkerCardData?
            get() = null
        override val stringCardData: StringCardData?
            get() = null
        override val choiceCardData: ChoiceCardData?
            get() = null
        override val folderData: FolderData?
            get() = null
        override val flashCardCoverData: FlashCardCoverData?
            get() = FlashCardCoverData(
                title = file.title,
                containingCard = containingCard
            )
        override val tagData: MutableList<TagData>?
            get() = null

    }

    data class StringCard(
        val card: Card,
        val tags: MutableList<TagData>?):LibraryRV(
        type = LibRVViewType.StringCard,
        position = card.libOrder,
        colorStatus = card.colorStatus,
        id = card.id
    ){
        override val stringCardData: StringCardData
            get() = StringCardData(
                frontText = card.stringData?.frontText!!,
                frontTitle = card.stringData.frontTitle,
                backTitle = card.stringData.backTitle,
                backText = card.stringData.backText!!
            )

        override val markerCardData: MarkerCardData?
            get() = null
        override val choiceCardData: ChoiceCardData?
            get() = null
        override val folderData: FolderData?
            get() = null
        override val flashCardCoverData: FlashCardCoverData?
            get() = null
        override val tagData: MutableList<TagData>?
            get() = tags

    }
    data class ChoiceCard(val card: Card,
                          val tags: MutableList<TagData>?):LibraryRV(
        type = LibRVViewType.ChoiceCard,
        position = card.libOrder,
        colorStatus = card.colorStatus,
        id = card.id
                          ){
        override val choiceCardData: ChoiceCardData?
            get() = ChoiceCardData(card.quizData?.question, card.quizData?.answerPreview)
        override val markerCardData: MarkerCardData?
            get() = null
        override val stringCardData: StringCardData?
            get() = null
        override val folderData: FolderData?
            get() = null
        override val flashCardCoverData: FlashCardCoverData?
            get() = null
        override val tagData: MutableList<TagData>?
            get() = tags

    }


    data class MarkerCard(val card: Card,
                          val tags: MutableList<TagData>?):LibraryRV(
        type = LibRVViewType.MarkerCard,
        position = card.libOrder,
        colorStatus = card.colorStatus,
        id = card.id
                          ){
        override val markerCardData: MarkerCardData
            get() = MarkerCardData(card.markerData?.markerTextPreview!!)

        override val stringCardData: StringCardData?
            get() = TODO("Not yet implemented")
        override val choiceCardData: ChoiceCardData?
            get() = TODO("Not yet implemented")
        override val folderData: FolderData?
            get() = TODO("Not yet implemented")
        override val flashCardCoverData: FlashCardCoverData?
            get() = TODO("Not yet implemented")
        override val tagData: MutableList<TagData>?
            get() = tags
    }



}
data class FolderData(val title:String?,
                      val containingFolder:Int,
                      val containingFlashCardCover:Int,
                      val containingCard:Int, ){


}

    data class FlashCardCoverData(
                      val title:String?,
                      val containingCard:Int, )

    data class StringCardData(
                          val frontTitle:String?,
                          val frontText:String?,
                          val backTitle:String?,
                          val backText:String?,
                          )

    data class MarkerCardData(val markedText:String?)

    data class TagData(val tagId:Int,
                    val tagText:String)

    data class ChoiceCardData(val question:String?,
                              val answerChoice:String?)


