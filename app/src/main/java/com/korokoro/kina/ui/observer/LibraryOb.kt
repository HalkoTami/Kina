package com.korokoro.kina.ui.observer

import android.content.Context
import android.text.SpannableStringBuilder
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.korokoro.kina.actions.changeViewVisibility
import com.korokoro.kina.databinding.LibraryChildFragWithMulModeBaseBinding
import com.korokoro.kina.databinding.MainActivityPopupEditFileBinding
import com.korokoro.kina.ui.view_set_up.ColorPalletViewSetUp
import com.korokoro.kina.ui.view_set_up.GetCustomDrawables
import com.korokoro.kina.ui.viewmodel.EditFileViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel
import com.korokoro.kina.ui.viewmodel.MainViewModel
import com.korokoro.kina.ui.viewmodel.SearchViewModel

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