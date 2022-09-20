package com.example.tangochoupdated.ui.viewmodel.customClasses



enum class StringFragFocusedOn{
    FrontTitle,FrontContent,BackTitle,BackContent,None
}
enum class LibRVState{
    Selectable,LeftSwiped,Plane,LeftSwiping,SelectFileMoveTo,Selected
}

enum class AnkiBoxFragments{
    AllFlashCardCovers, Library, Favourites
}
class AnkiBoxTabData(
    var currentTab: AnkiBoxFragments,
    var before: AnkiBoxFragments?
)
enum class MyOrientation{
    TOP,BOTTOM,LEFT,RIGHT,MIDDLE
}

