package com.example.tangochoupdated.ui.listener.popUp

import android.view.View
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.ui.view_set_up.ConfirmMode
import com.example.tangochoupdated.ui.viewmodel.DeletePopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragPopUpConfirmDeleteCL(val binding: LibraryFragBinding, val libVM: LibraryViewModel,val deletePopUpViewModel: DeletePopUpViewModel): View.OnClickListener{
    val onlyP = binding.confirmDeletePopUpBinding
    val deleteAllC = binding.confirmDeleteChildrenPopUpBinding
    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                onlyP.btnCloseConfirmDeleteOnlyParentPopup -> deletePopUpViewModel.setConfirmDeleteVisible(false)
                onlyP.btnCommitDeleteOnlyParent -> {
                    deletePopUpViewModel.apply {
                        if(checkDeletingItemsHasChildren()) {
                            setConfirmDeleteVisible(false)
                            setConfirmDeleteWithChildrenVisible(true)
                        } else deleteOnlyFile()
                    }

                }
                onlyP.btnDenyDeleteOnlyParent -> deletePopUpViewModel.setConfirmDeleteVisible(false,)
                deleteAllC.btnCloseConfirmDeleteOnlyParentPopup -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
                deleteAllC.btnDeleteAllChildren -> deletePopUpViewModel.deleteWithChildren()
                deleteAllC.deleteOnlyFile -> deletePopUpViewModel.deleteOnlyFile()
                deleteAllC.btnCancel -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)


            }
        }
    }
}