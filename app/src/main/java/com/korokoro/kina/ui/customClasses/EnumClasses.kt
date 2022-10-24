package com.korokoro.kina.ui.customClasses

enum class StringFragFocusedOn{
    FrontTitle,FrontContent,BackTitle,BackContent,None
}
enum class LibRVState{
    Selectable,LeftSwiped,Plane,LeftSwiping,SelectFileMoveTo,Selected
}
enum class NeighbourCardSide{
    NEXT,PREVIOUS
}
enum class EditingMode{
    New, Edit
}
enum class AnkiOrder{
    Library, Random,MostMissed
}
enum class MainFragment{
    Library,EditCard,Anki
}
enum class LibraryFragment{
    ChooseFileMoveTo,FlashCardCover,Folder,Home,InBox
}
enum class AnkiFragments{
    AnkiBox,Flip,FlipItems,FlipCompleted
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
enum class AnkiBoxFragments{
    AllFlashCardCovers, Library, Favourites
}

enum class MyOrientation{
    TOP,BOTTOM,LEFT,RIGHT,MIDDLE
}

enum class LibraryParentFolder{
    Home,Folder,FlashCard
}
enum class SelectedItemAction{
    Delete,ChooseFileMoveTo
}
enum class LibraryTopBarMode{
    Home,File,Multiselect,InBox,ChooseFileMoveTo
}
enum class ConfirmDeletePopUpMode{
    AskDeleteParent,AskDeleteAllChildren
}


