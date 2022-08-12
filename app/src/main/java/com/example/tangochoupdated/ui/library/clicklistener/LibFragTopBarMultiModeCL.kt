package com.example.tangochoupdated.ui.library.clicklistener

import android.content.Context
import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragTopBarMultiselectModeBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibFragTopBarMultiModeCL(val context: Context, val binding: LibraryFragTopBarMultiselectModeBinding, val libVM: LibraryViewModel,
                               val navCon: NavController
): View.OnClickListener{
    val multi = binding
    fun changeMenuVisibility(){
        multi.frameLayMultiModeMenu.apply{
            visibility =  if(this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        binding.apply {
            when(v){

                multi.imvCloseMultiMode -> libVM.setMultipleSelectMode(false)
                multi.imvSelectAll -> TODO()
                multi.imvChangeMenuVisibility -> changeMenuVisibility()

                multi.multiSelectMenuBinding.imvMoveSelectedItems -> libVM.chooseFileMoveTo()
                multi.multiSelectMenuBinding.imvDeleteSelectedItems -> TODO()
                multi.multiSelectMenuBinding.imvSetFlagToSelectedItems -> TODO()



            }
        }
    }

}