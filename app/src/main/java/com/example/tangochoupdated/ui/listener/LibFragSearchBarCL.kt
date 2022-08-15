package com.example.tangochoupdated.ui.listener
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.ItemSearchBarBinding
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragSearchBarCL(val context:Context, val searchIcon: ImageView,
                         val searchBindingFrame:FrameLayout,
                         val searchBinding:ItemSearchBarBinding, val libVM: LibraryViewModel, ): View.OnClickListener{


    override fun onClick(v: View?) {
        when(v){
            searchIcon -> {
                searchIcon.visibility = View.GONE
                searchBindingFrame.layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.searchbar_height)
            }



        }
    }

}