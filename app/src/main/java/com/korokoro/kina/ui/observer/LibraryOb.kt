package com.korokoro.kina.ui.observer

import android.content.Context
import android.text.SpannableStringBuilder
import com.korokoro.kina.databinding.MainActivityPopupEditFileBinding
import com.korokoro.kina.ui.view_set_up.GetCustomDrawables
import com.korokoro.kina.ui.viewmodel.EditFileViewModel

class LibraryOb {
    fun observeEditFilePopUp(binding:MainActivityPopupEditFileBinding,
                             data:EditFileViewModel.PopUpUI,
                             context:Context){
        binding.apply {
            if(txvFileTitle.text != data.txvLeftTopText) txvFileTitle.text = data.txvLeftTopText
            if(txvHint.text!=data.txvHintText) txvHint.text=data.txvHintText
            imvFileType.setImageDrawable(GetCustomDrawables(context).getFileIconByFile(data.parentTokenFile))
            edtCreatefile.apply {
                if(text.toString()!=data.edtTitleText) text= SpannableStringBuilder(data.edtTitleText)
                if(hint!= data.edtTitleHint) hint = data.edtTitleHint
            }
        }

    }

}