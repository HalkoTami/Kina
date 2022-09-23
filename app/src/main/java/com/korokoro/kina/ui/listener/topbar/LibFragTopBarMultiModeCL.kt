package com.korokoro.kina.ui.listener.topbar

import android.view.View
import android.widget.FrameLayout
import com.korokoro.kina.databinding.LibItemTopBarMenuBinding
import com.korokoro.kina.databinding.LibraryFragTopBarMultiselectModeBinding
import com.korokoro.kina.ui.animation.makeToast
import com.korokoro.kina.ui.viewmodel.DeletePopUpViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel

class LibFragTopBarMultiModeCL(
    val binding: LibraryFragTopBarMultiselectModeBinding,
    val menuBinding: LibItemTopBarMenuBinding,
    val menuFrame:FrameLayout,
    val libVM: LibraryBaseViewModel,
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