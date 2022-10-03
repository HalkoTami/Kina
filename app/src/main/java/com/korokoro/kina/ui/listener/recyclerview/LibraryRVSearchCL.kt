package com.korokoro.kina.ui.listener.recyclerview

import android.view.View
import com.korokoro.kina.databinding.LibraryFragRvItemBaseBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.fragment.base_frag_con.EditCardBaseFragDirections
import com.korokoro.kina.ui.viewmodel.CreateCardViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel
import com.korokoro.kina.ui.viewmodel.MainViewModel

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