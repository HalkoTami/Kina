package com.korokoro.kina.ui.listener.recyclerview

import android.view.View
import androidx.navigation.NavController
import com.korokoro.kina.databinding.LibraryFragRvItemBaseBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.fragment.base_frag_con.CreateCardFragmentBaseDirections
import com.korokoro.kina.ui.viewmodel.CreateCardViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel

class LibraryRVSearchCL(
    val item: Any,
    private val navController: NavController,
    private val lVM: LibraryBaseViewModel,
    private val createCardViewModel: CreateCardViewModel,
    private val rvBinding: LibraryFragRvItemBaseBinding,
    private val mainNavCon:NavController,
): View.OnClickListener{
    override fun onClick(v: View?) {
        rvBinding.apply {
            when(v){
                libRvBaseContainer       ->  {
                    if(item is File)lVM.openNextFile(item,)
                    if(item is Card)mainNavCon.navigate(CreateCardFragmentBaseDirections.openCreateCard())
                }
            }
        }
    }


}