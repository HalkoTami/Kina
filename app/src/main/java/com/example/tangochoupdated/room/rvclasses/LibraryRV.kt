package com.example.tangochoupdated.room.rvclasses

import com.example.tangochoupdated.room.dataclass.*
import com.example.tangochoupdated.room.enumclass.ColorStatus

enum class LibRVViewType{
    Folder,FlashCardCover,StringCard,MarkerCard,ChoiceCard
}
sealed class LibraryRV(){
    abstract val type:LibRVViewType
    abstract val position:Int
    abstract val id:Int
    abstract val colorStatus:ColorStatus?
    abstract val markerCardData:List<MarkerCardData>?
    abstract val stringCardData: StringCardData?
    abstract val choiceCardData: ChoiceCardData?
    abstract val folderData:FolderData?
    abstract val flashCardCoverData:FlashCardCoverData?
    abstract val tagData:List<TagData>

    abstract val tag: List<String>
//    tODO make accessible from ListAdapter
    data class Folder (val file: File, val containingCard: Int, val containingFolder: Int, val containingFlashCardCover: Int):LibraryRV(){

    override val type: LibRVViewType
        get() = LibRVViewType.Folder

    override val position: Int
        get() = file.libOrder

    override val id: Int
        get() = file.fileId

    override val colorStatus: ColorStatus?
        get() = file.colorStatus

    override val markerCardData: List<MarkerCardData>?
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
    override val tagData: List<TagData>
        get() = TODO("Not yet implemented")
    override val tag: List<String>
        get() = TODO("Not yet implemented")
}
    data class FlashCardCover(val file: File, val containingCard: Int):LibraryRV(){
        override val type: LibRVViewType
            get() = TODO("Not yet implemented")
        override val position: Int
            get() = TODO("Not yet implemented")
        override val id: Int
            get() = TODO("Not yet implemented")
        override val colorStatus: ColorStatus?
            get() = TODO("Not yet implemented")
        override val markerCardData: List<MarkerCardData>?
            get() = TODO("Not yet implemented")
        override val stringCardData: StringCardData?
            get() = TODO("Not yet implemented")
        override val choiceCardData: ChoiceCardData?
            get() = TODO("Not yet implemented")
        override val folderData: FolderData?
            get() = TODO("Not yet implemented")
        override val flashCardCoverData: FlashCardCoverData?
            get() = TODO("Not yet implemented")
        override val tagData: List<TagData>
            get() = TODO("Not yet implemented")
        override val tag: List<String>
            get() = TODO("Not yet implemented")
    }
    data class StringCard(val card: Card):LibraryRV(){
        override val stringCardData: StringCardData?
            get() = TODO("Not yet implemented")
        override val type: LibRVViewType
            get() = TODO("Not yet implemented")
        override val position: Int
            get() = TODO("Not yet implemented")
        override val id: Int
            get() = TODO("Not yet implemented")
        override val colorStatus: ColorStatus?
            get() = TODO("Not yet implemented")
        override val markerCardData: List<MarkerCardData>?
            get() = TODO("Not yet implemented")
        override val choiceCardData: ChoiceCardData?
            get() = TODO("Not yet implemented")
        override val folderData: FolderData?
            get() = TODO("Not yet implemented")
        override val flashCardCoverData: FlashCardCoverData?
            get() = TODO("Not yet implemented")
        override val tagData: List<TagData>
            get() = TODO("Not yet implemented")
        override val tag: List<String>
            get() = TODO("Not yet implemented")
    }
    data class ChoiceCard(val card: Card):LibraryRV(){
        override val choiceCardData: ChoiceCardData?
            get() = TODO("Not yet implemented")
        override val type: LibRVViewType
            get() = TODO("Not yet implemented")
        override val position: Int
            get() = TODO("Not yet implemented")
        override val id: Int
            get() = TODO("Not yet implemented")
        override val colorStatus: ColorStatus?
            get() = TODO("Not yet implemented")
        override val markerCardData: List<MarkerCardData>?
            get() = TODO("Not yet implemented")
        override val stringCardData: StringCardData?
            get() = TODO("Not yet implemented")
        override val folderData: FolderData?
            get() = TODO("Not yet implemented")
        override val flashCardCoverData: FlashCardCoverData?
            get() = TODO("Not yet implemented")
        override val tagData: List<TagData>
            get() = TODO("Not yet implemented")
        override val tag: List<String>
            get() = TODO("Not yet implemented")
    }
    data class MarkerCard(val card: Card):LibraryRV(){
        override val markerCardData: List<MarkerCardData>?
            get() = TODO("Not yet implemented")
        override val type: LibRVViewType
            get() = TODO("Not yet implemented")
        override val position: Int
            get() = TODO("Not yet implemented")
        override val id: Int
            get() = TODO("Not yet implemented")
        override val colorStatus: ColorStatus?
            get() = TODO("Not yet implemented")
        override val stringCardData: StringCardData?
            get() = TODO("Not yet implemented")
        override val choiceCardData: ChoiceCardData?
            get() = TODO("Not yet implemented")
        override val folderData: FolderData?
            get() = TODO("Not yet implemented")
        override val flashCardCoverData: FlashCardCoverData?
            get() = TODO("Not yet implemented")
        override val tagData: List<TagData>
            get() = TODO("Not yet implemented")
        override val tag: List<String>
            get() = TODO("Not yet implemented")
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
                          val frontText:String,
                          val backTitle:String?,
                          val backText:String,
                          )

    data class MarkerCardData(val markedText:MarkerData)

    data class TagData(val tagId:Int,
                    val tagText:String)

    data class ChoiceCardData(val question:String?,
                              val answerChoice:String?)


