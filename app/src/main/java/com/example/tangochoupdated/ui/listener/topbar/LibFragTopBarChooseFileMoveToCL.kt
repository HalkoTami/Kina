package com.example.tangochoupdated.ui.listener.topbar
import android.content.Context
import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragTopBarChooseFileMoveToBinding
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragTopBarChooseFileMoveToCL(val context:Context, val binding: LibraryFragTopBarChooseFileMoveToBinding, val libVM: LibraryViewModel,
                                      val navCon:NavController): View.OnClickListener{

    val moveTo = binding

    override fun onClick(v: View?) {
        binding.apply {
            when(v){

                moveTo.imvCloseChooseFileMoveTo -> navCon.popBackStack()


            }
        }
    }

}