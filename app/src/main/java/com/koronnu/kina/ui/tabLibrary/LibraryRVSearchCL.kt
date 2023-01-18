package com.koronnu.kina.ui.tabLibrary

import android.view.View
import com.koronnu.kina.databinding.LibraryFragRvItemBaseBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.ui.viewmodel.CreateCardViewModel

class LibraryRVSearchCL(
    val item: Any,
    private val lVM: LibraryBaseViewModel,
    private val rvBinding: LibraryFragRvItemBaseBinding,
    private val createCardViewModel: CreateCardViewModel
): View.OnClickListener{
    override fun onClick(v: View?) {
        rvBinding.apply {
            when(v){
                libRvBaseContainer       ->  {
                    when(item){
                        is File -> lVM.openNextFile(item,)
                        is Card -> createCardViewModel.onClickEditCardFromRV(item)
                    }
                }
            }
        }
    }


}