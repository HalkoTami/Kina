package com.example.tangochoupdated.ui.library.clicklistener
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.ItemSearchBarBinding
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragSelectFileMoveToBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragTopBarChooseFileMoveToBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibFragSearchBarCL(val context:Context, val searchIcon: ImageView,val searchBinding:ItemSearchBarBinding, val libVM: LibraryViewModel, ): View.OnClickListener{


    override fun onClick(v: View?) {
        when(v){
            searchIcon -> {
                searchIcon.visibility = View.GONE
                searchBinding.root.visibility = View.VISIBLE
            }



        }
    }

}