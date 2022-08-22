package com.example.tangochoupdated.db.enumclass
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
    var ankiFragment: AnkiFragment = AnkiFragment.AnkiBox,
    var flipFragment: FlipFragment = FlipFragment.StringLook
)
enum class StartFragment{
    Library,EditCard,Anki
}
enum class LibraryFragment{
    ChooseFileMoveTo,FlashCardCover,Folder,Home,InBox
}
enum class AnkiFragment{
    AnkiBox,Flip
}
enum class FlipFragment{
    StringLook,StringType
}