package com.example.tangochoupdated.ui.listener.popUp

import android.view.View
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.ui.view_set_up.ConfirmMode
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragPopUpConfirmDeleteCL(val binding: LibraryFragBinding, val libVM: LibraryViewModel): View.OnClickListener{
    val onlyP = binding.confirmDeletePopUpBinding
    val deleteAllC = binding.confirmDeleteChildrenPopUpBinding
    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                onlyP.btnCloseConfirmDeleteOnlyParentPopup -> libVM.setConfirmPopUpVisible(false,ConfirmMode.DeleteOnlyParent)
                onlyP.btnCommitDeleteOnlyParent -> libVM.onClickBtnCommitConfirm(ConfirmMode.DeleteOnlyParent)
                onlyP.btnDenyDeleteOnlyParent -> libVM.onClickBtnDenial(ConfirmMode.DeleteOnlyParent)
                deleteAllC.btnCloseConfirmDeleteOnlyParentPopup -> binding.frameLayConfirmDelete.visibility = View.GONE
                deleteAllC.btnCommitDeleteAllChildren -> libVM.onClickBtnCommitConfirm(ConfirmMode.DeleteWithChildren)
                deleteAllC.btnDenyDeleteAllChildren -> libVM.onClickBtnDenial(ConfirmMode.DeleteWithChildren)


            }
        }
    }
}