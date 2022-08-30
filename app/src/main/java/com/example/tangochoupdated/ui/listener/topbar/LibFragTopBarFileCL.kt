package com.example.tangochoupdated.ui.listener.topbar
import android.content.Context
import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragTopBarAncestorsBinding
import com.example.tangochoupdated.databinding.LibraryFragTopBarFileBinding
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragTopBarFileCL(val context:Context,
                          val binding: LibraryFragTopBarFileBinding,
                          val ancestorsBinding: LibraryFragTopBarAncestorsBinding,
                          val libVM: LibraryViewModel,
                          val navCon:NavController): View.OnClickListener{

    val file = binding


    override fun onClick(v: View?) {
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