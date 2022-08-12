package com.example.tangochoupdated.ui.library.clicklistener
import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragInboxBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragTopBarInboxBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibFragTopBarInBoxCL(val context:Context, val binding: LibraryFragTopBarInboxBinding, val libVM: LibraryViewModel,
                           val navCon:NavController): View.OnClickListener{
    val inBox = binding


    override fun onClick(v: View?) {
        binding.apply {
            when(v){

                inBox.imvMoveToFlashCard -> {
                    libVM.onClickMoveInBoxCardToFlashCard()
                }
                inBox.imvCloseInbox -> {
                    navCon.popBackStack()
                }




            }
        }
    }

}