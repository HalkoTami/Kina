package com.example.tangochoupdated.ui.listener
import android.app.Activity
import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.ItemSearchBarBinding
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel
import com.example.tangochoupdated.ui.viewmodel.SearchViewModel

class LibFragSearchBarCL(val context:Context, val searchIcon: ImageView,
                         val searchBindingFrame:FrameLayout,
                         val searchBinding:ItemSearchBarBinding,
                         private val searchViewModel: SearchViewModel,
                         private val inputMethodManager: InputMethodManager): View.OnClickListener{


    override fun onClick(v: View?) {
        searchBinding.apply {
            when(v){
                searchIcon -> {
                    searchBinding.edtLibrarySearch.requestFocus()
                    inputMethodManager.showSoftInput(searchBinding.edtLibrarySearch, 0 )
                    searchIcon.visibility = View.GONE
                    searchBindingFrame.visibility = View.VISIBLE
                }
                txvCancel -> {
                    inputMethodManager.hideSoftInputFromWindow(searchBinding.edtLibrarySearch.windowToken, 0 )
                    searchBinding.edtLibrarySearch.text= SpannableStringBuilder("")
                    searchViewModel.setSearchText("")
                    searchIcon.visibility = View.VISIBLE
                    searchBindingFrame.visibility = View.GONE
                }




            }
        }

    }

}