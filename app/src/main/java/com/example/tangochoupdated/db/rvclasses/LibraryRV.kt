package com.example.tangochoupdated.db.rvclasses

import com.example.tangochoupdated.db.dataclass.*

enum class LibRVViewType{
    Folder,FlashCardCover,StringCard,MarkerCard,ChoiceCard
}
 class LibraryRV(
     val type: LibRVViewType,
     var position: Int,
     val file: File?,
     val card: Card?,
     val tag: List<File>?,
     val id: Int,
     var selectable:Boolean = false,
     var selected:Boolean = false,
     var leftSwiped:Boolean = false
 )



















































































































































































