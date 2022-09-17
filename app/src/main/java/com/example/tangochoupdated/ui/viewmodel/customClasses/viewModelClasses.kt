package com.example.tangochoupdated.ui.viewmodel.customClasses

import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.ColorStatus

enum class EditingMode{
    New, Edit
}
enum class AnkiOrder{
    Library, Random,MostMissed
}
class ColorPalletStatus(
    var colNow: ColorStatus,
    var before: ColorStatus?
)
class AnkiFilter(var correctAnswerTyped:Boolean = false,
                 var answerTypedFilterActive:Boolean = false,
                 var remembered:Boolean = false,
                 var rememberedFilterActive:Boolean = false,
                 var flag:Boolean = true,
                 var flagFilterActive :Boolean = false)


class AutoFlip(
    var active:Boolean = false,
    var seconds:Int = 20
)
class FragmentTree(
    var startFragment: MainFragment = MainFragment.Library,
    var libraryFragment: LibraryFragment = LibraryFragment.Home,
    var ankiFragment: AnkiFragments = AnkiFragments.AnkiBox,
    var flipFragment: FlipFragment = FlipFragment.StringLook
)
enum class MainFragment{
    Library,EditCard,Anki
}
enum class LibraryFragment{
    ChooseFileMoveTo,FlashCardCover,Folder,Home,InBox
}
enum class AnkiFragments{
    AnkiBox,Flip
}
enum class FlipFragment{
    StringLook,StringType
}
enum class FlipFragments{
    LookStringFront,LookStringBack,TypeAnswerString,CheckAnswerString
}
enum class Count{
    Start,End
}
enum class AnimationAttributes{
    StartAnim,EndAnim,Pause,Resume,Cancel
}
class CountFlip(
    var count: Count? = null,
    var countIfLongerThan:Int = 10,
    var flipSaved:Boolean = false,
    var countingCard:Card
)

class AnimationController(
    var attributes: AnimationAttributes? = null

)
class Progress(
    var now:Int,
    var all:Int
)
class MakeToastFromVM(
    var text:String,
    var show:Boolean
)
