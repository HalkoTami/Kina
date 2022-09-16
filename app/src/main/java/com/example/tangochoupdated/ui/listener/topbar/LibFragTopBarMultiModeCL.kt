package com.example.tangochoupdated.ui.listener.topbar

import android.view.View
import android.widget.FrameLayout
import com.example.tangochoupdated.databinding.LibItemTopBarMenuBinding
import com.example.tangochoupdated.databinding.LibraryFragTopBarMultiselectModeBinding
import com.example.tangochoupdated.ui.animation.makeToast
import com.example.tangochoupdated.ui.viewmodel.DeletePopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragTopBarMultiModeCL(
                               val binding: LibraryFragTopBarMultiselectModeBinding,
                               val menuBinding: LibItemTopBarMenuBinding,
                               val menuFrame:FrameLayout,
                               val libVM: LibraryViewModel,
                               val deletePopUpViewModel:DeletePopUpViewModel
): View.OnClickListener{
    val multi = binding
    private fun changeMenuVisibility(){
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
                    if(notSelected)deletePopUpViewModel.makeToastVisible()
                        else {
                            libVM.openChooseFileMoveTo(null)
                    }
                }
                menuBinding.linLayDeleteSelectedItems -> if(notSelected) makeToast(v.context,"todo")
                else {
                    deletePopUpViewModel.setDeletingItem(libVM.returnSelectedItems())
                    deletePopUpViewModel.setConfirmDeleteVisible(true)

                }
                menuBinding.linLaySetFlagToSelectedItems -> if(notSelected) makeToast(v.context,"todo")
                 else TODO()



            }
        }
    }

}