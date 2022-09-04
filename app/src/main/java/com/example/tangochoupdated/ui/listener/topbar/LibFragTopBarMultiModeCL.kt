package com.example.tangochoupdated.ui.listener.topbar

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibItemTopBarMenuBinding
import com.example.tangochoupdated.databinding.LibraryFragTopBarMultiselectModeBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.toastToDo
import com.example.tangochoupdated.ui.viewmodel.DeletePopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragTopBarMultiModeCL(val context: Context,
                               val binding: LibraryFragTopBarMultiselectModeBinding,
                               val menuBinding: LibItemTopBarMenuBinding,
                               val menuFrame:FrameLayout,
                               val libVM: LibraryViewModel,
                               val navCon: NavController, val deletePopUpViewModel:DeletePopUpViewModel
): View.OnClickListener{
    val multi = binding
    fun toastPleaseSelectItems(){
        Toast.makeText(context,"アイテムを選択してください", Toast.LENGTH_SHORT).show()
    }
    fun changeMenuVisibility(){
        menuFrame.apply{
            visibility =  if(this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    override fun onClick(v: View?) {

        val notSelected = libVM.returnSelectedItems().isEmpty()
        binding.apply {
            when(v){

                multi.imvCloseMultiMode -> libVM.setMultipleSelectMode(false)
                multi.imvSelectAll -> {
                    libVM.changeAllRVSelectedStatus(v.isSelected.not())
                    v.isSelected = v.isSelected.not()
                }
                multi.imvChangeMenuVisibility -> changeMenuVisibility()

                menuBinding.linLayMoveSelectedItems -> {
                    if(notSelected)toastPleaseSelectItems()
                        else libVM.openChooseFileMoveTo(null,navCon)
                }
                menuBinding.linLayDeleteSelectedItems -> if(notSelected)toastPleaseSelectItems()
                else {
                    deletePopUpViewModel.setDeletingItem(libVM.returnSelectedItems())
                    var card:Boolean = false
                    deletePopUpViewModel.returnDeletingItems().onEach { card = it is Card }
                    if(card){
                        deletePopUpViewModel.deleteCard()
                    } else deletePopUpViewModel.setConfirmDeleteVisible(true)

                }
                menuBinding.linLaySetFlagToSelectedItems -> if(notSelected)toastPleaseSelectItems()
                else toastToDo(context)



            }
        }
    }

}