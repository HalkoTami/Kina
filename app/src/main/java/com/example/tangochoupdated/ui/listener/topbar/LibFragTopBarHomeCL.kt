package com.example.tangochoupdated.ui.listener.topbar
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragTopBarHomeBinding
import com.example.tangochoupdated.makeToast
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragTopBarHomeCL(private val binding: LibraryFragTopBarHomeBinding,
                          private val libVM: LibraryViewModel,
                          ): View.OnClickListener{
    val home = binding


    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                home.frameLayInBox-> libVM.onClickInBox()
                home.imvBookMark -> makeToast(v.context,"todo")
            }
        }
    }

}