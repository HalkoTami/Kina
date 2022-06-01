package com.example.tangochoupdated.room.rvclasses

import com.example.tangochoupdated.room.enumclass.ColorStatus

enum class LibRVViewType{
    Folder,
}
sealed class LibraryRV(){
    abstract val type:LibRVViewType
    data class Folder (var vararg :FolderData):LibraryRV(){
        override val type: LibRVViewType
            get() = LibRVViewType.Folder

    }

}
data class FolderData(val id: Int,
                      val title:String,
                      val containingFolder:Int,
    val containingFlashCardCover:Int,
    val containingCard:Int,
    val colorStatus: ColorStatus,
    val position: Int)


    data class FlashCardCover(val id:Int,
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


