package com.example.tangochoupdated.db.enumclass

import android.animation.ValueAnimator
import android.view.animation.Animation
import com.example.tangochoupdated.db.dataclass.Card

enum class Mode{
    New, Edit
}
enum class AnkiOrder{
    Library, Random,MostMissed
}
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
    var startFragment:StartFragment = StartFragment.Library,
    var libraryFragment:LibraryFragment = LibraryFragment.Home,
    var ankiFragment: AnkiFragments = AnkiFragments.AnkiBox,
    var flipFragment: FlipFragment = FlipFragment.StringLook
)
enum class StartFragment{
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
enum class FlipAction{
    LookStringFront,LookStringBack,TypeAnswerString,CheckAnswerString
}
enum class Count{
    Start,End
}
enum class AnimationAttributes{
    StartAnim,EndAnim,Pause,Resume
}
class CountFlip(
    var count:Count? = null,
    var countIfLongerThan:Int = 10,
    var flipSaved:Boolean = false,
    var countingCard:Card
)

class AnimationController(
    val animation:ValueAnimator,
    var attributes: AnimationAttributes? = null

)