package com.example.tangochoupdated.room.enumclass

import android.opengl.Visibility
import android.view.View
import android.view.View.INVISIBLE

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
    Selectable,LeftSwiped,Plane
}
