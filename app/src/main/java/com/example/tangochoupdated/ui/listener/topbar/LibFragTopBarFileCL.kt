package com.example.tangochoupdated.ui.listener.topbar
import android.content.Context
import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragTopBarAncestorsBinding
import com.example.tangochoupdated.databinding.LibraryFragTopBarFileBinding
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragTopBarFileCL(val binding: LibraryFragTopBarFileBinding,
                          val ancestorsBinding: LibraryFragTopBarAncestorsBinding,
                          val libVM: LibraryViewModel, ): View.OnClickListener{

    val file = binding


    override fun onClick(v: View?) {
        val navCon = libVM.returnLibraryNavCon() ?:return
        binding.apply {
            when(v){

                imvGoBack -> navCon.popBackStack()
                ancestorsBinding.lineLayGGFile -> {
                    navCon.popBackStack()
                    navCon.popBackStack()
                }
                ancestorsBinding.lineLayGPFile ->{
                    navCon.popBackStack()
                }
            }
        }
    }

}