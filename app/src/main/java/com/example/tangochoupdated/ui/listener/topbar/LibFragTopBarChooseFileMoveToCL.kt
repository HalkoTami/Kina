package com.example.tangochoupdated.ui.listener.topbar
import android.content.Context
import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragTopBarChooseFileMoveToBinding
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragTopBarChooseFileMoveToCL(val binding: LibraryFragTopBarChooseFileMoveToBinding, val libVM: LibraryViewModel,): View.OnClickListener{

    val moveTo = binding

    override fun onClick(v: View?) {
        binding.apply {
            when(v){

                moveTo.imvCloseChooseFileMoveTo -> libVM.returnLibraryNavCon()?.popBackStack()


            }
        }
    }

}