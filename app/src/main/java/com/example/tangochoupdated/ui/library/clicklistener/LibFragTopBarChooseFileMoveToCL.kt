package com.example.tangochoupdated.ui.library.clicklistener
import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragSelectFileMoveToBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragTopBarChooseFileMoveToBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

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