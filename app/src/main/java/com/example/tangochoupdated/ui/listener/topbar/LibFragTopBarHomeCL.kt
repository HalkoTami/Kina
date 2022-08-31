package com.example.tangochoupdated.ui.listener.topbar
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragTopBarHomeBinding
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragTopBarHomeCL(val context:Context, val binding: LibraryFragTopBarHomeBinding, val libVM: LibraryViewModel,
                          val navCon:NavController): View.OnClickListener{
    val home = binding


    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                home.frameLayInBox-> libVM.onClickInBox(navCon)
                home.imvBookMark -> Toast.makeText(context,"todo",Toast.LENGTH_SHORT).show()
            }
        }
    }

}