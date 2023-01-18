package com.koronnu.kina.util

import android.text.SpannableStringBuilder
import androidx.lifecycle.Observer
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.LibraryChildFragWithMulModeBaseBinding
import com.koronnu.kina.ui.viewmodel.SearchViewModel

class LibraryOb {

    fun searchModeObserver(binding: LibraryChildFragWithMulModeBaseBinding,
                           searchViewModel: SearchViewModel) = Observer<Boolean>{
        binding.apply {
            if(it) {
                changeViewVisibility(frameLaySearchBar,true)
                bindingSearch.edtLibrarySearch.text = SpannableStringBuilder(searchViewModel.returnSearchText())
            } else {
                changeViewVisibility(frameLaySearchBar,false)
                bindingSearch.edtLibrarySearch.text = SpannableStringBuilder("")
            }
        }
    }
    fun multiMenuVisibilityObserver(binding: LibraryChildFragWithMulModeBaseBinding) = Observer<Boolean>{ visible->
        arrayOf(binding.frameLayMultiModeMenu,binding.libChildFragBackground).onEach {
            changeViewVisibility(it,visible)
        }

    }

}