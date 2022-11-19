package com.koronnu.kina.ui.observer

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.lifecycle.Observer
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.LibraryChildFragWithMulModeBaseBinding
import com.koronnu.kina.databinding.MainActivityPopupEditFileBinding
import com.koronnu.kina.ui.view_set_up.GetCustomDrawables
import com.koronnu.kina.ui.viewmodel.EditFileViewModel
import com.koronnu.kina.ui.viewmodel.SearchViewModel

class LibraryOb {
    fun observeEditFilePopUp(binding:MainActivityPopupEditFileBinding,
                             data:EditFileViewModel.PopUpUI,
                             context:Context){
        binding.apply {
            if(txvFileTitle.text != data.txvLeftTopText) txvFileTitle.text = data.txvLeftTopText
            if(txvHint.text!=data.txvHintText) txvHint.text=data.txvHintText
            imvFileType.setImageDrawable(GetCustomDrawables(context).getFileIconByFile(data.parentTokenFile))
            edtFileTitle.apply {
                if(text.toString()!=data.edtTitleText) text= SpannableStringBuilder(data.edtTitleText)
                if(hint!= data.edtTitleHint) hint = data.edtTitleHint
            }
            btnFinish.text = data.finishBtnText
        }

    }
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