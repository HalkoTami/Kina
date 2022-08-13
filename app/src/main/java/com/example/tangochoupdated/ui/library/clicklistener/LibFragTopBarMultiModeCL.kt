package com.example.tangochoupdated.ui.library.clicklistener

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragTopBarMultiselectModeBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.toastToDo
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.library.fragment.LibraryFragChooseFileMoveToDirections

class LibFragTopBarMultiModeCL(val context: Context, val binding: LibraryFragTopBarMultiselectModeBinding, val libVM: LibraryViewModel,
                               val navCon: NavController
): View.OnClickListener{
    val multi = binding
    fun toastPleaseSelectItems(){
        Toast.makeText(context,"アイテムを選択してください", Toast.LENGTH_SHORT).show()
    }
    fun changeMenuVisibility(){
        multi.frameLayMultiModeMenu.apply{
            visibility =  if(this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        val notSelected = libVM.returnSelectedCards().isNullOrEmpty()&&libVM.returnSelectedFiles().isNullOrEmpty()

        binding.apply {
            when(v){

                multi.imvCloseMultiMode -> libVM.setMultipleSelectMode(false)
                multi.imvSelectAll -> {
                    libVM.changeAllRVSelectedStatus(v.isSelected.not())
                    v.isSelected = v.isSelected.not()
                }
                multi.imvChangeMenuVisibility -> changeMenuVisibility()

                multi.multiSelectMenuBinding.linLayMoveSelectedItems -> {
                    if(notSelected)toastPleaseSelectItems()
                        else libVM.openChooseFileMoveTo(null)
                }
                multi.multiSelectMenuBinding.linLayDeleteSelectedItems -> if(notSelected)toastPleaseSelectItems()
                else libVM.onClickDeleteSelectedItems()
                multi.multiSelectMenuBinding.linLaySetFlagToSelectedItems -> if(notSelected)toastPleaseSelectItems()
                else toastToDo(context)



            }
        }
    }

}