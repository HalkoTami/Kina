package com.example.tangochoupdated.ui.listener.recyclerview

import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.fragment.base_frag_con.CreateCardFragmentBaseDirections
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibraryRVSearchCL(
    val item: Any,
    private val navController: NavController,
    private val lVM: LibraryViewModel,
    private val createCardViewModel: CreateCardViewModel,
    private val rvBinding: LibraryFragRvItemBaseBinding,
    private val mainNavCon:NavController,
): View.OnClickListener{
    override fun onClick(v: View?) {
        rvBinding.apply {
            when(v){
                baseContainer       ->  {
                    if(item is File)lVM.openNextFile(item,navController)
                    if(item is Card)mainNavCon.navigate(CreateCardFragmentBaseDirections.openCreateCard())
                }
            }
        }
    }


}