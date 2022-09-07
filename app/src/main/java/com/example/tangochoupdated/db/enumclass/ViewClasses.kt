package com.example.tangochoupdated.db.enumclass

enum class Tab{
    TabLibrary,TabAnki,CreateFile,CreateCard
}
enum class TabStatus{
    Focused, UnFocused
}
enum class ActionStatus{
    Able, UnAble
}


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
    var currentTab:AnkiBoxFragments,
    var before:AnkiBoxFragments?
)
