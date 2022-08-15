package com.example.tangochoupdated.ui.view_set_up

import com.example.tangochoupdated.db.dataclass.File

class LibraryFragmentStatus(
    var modeInBox:Boolean = false,
    var parentFolder:LibraryParentFolder = LibraryParentFolder.Home,
    var multiselectMode:Boolean = false,
    var selectedItemAction: SelectedItemAction? = null,
    var rvItemsEmpty:Boolean = true
    )
class ConfirmDeletePopUpStatus(
    var confirmMode: ConfirmDeletePopUpMode = ConfirmDeletePopUpMode.AskDeleteParent
)
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
class ParentFileAncestors(
    val gGrandPFile:File?,
    val gParentFile: File?,
    val ParentFile:File?,
)
enum class ConfirmMode{
    DeleteOnlyParent,DeleteWithChildren
}
enum class RVMode{
    FILE,CARD,SEARCH,CHOOSE_FILE
}