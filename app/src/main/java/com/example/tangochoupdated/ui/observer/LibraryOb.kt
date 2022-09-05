package com.example.tangochoupdated.ui.observer

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.appcompat.content.res.AppCompatResources
import com.example.tangochoupdated.databinding.MainActivityPopupEditFileBinding
import com.example.tangochoupdated.ui.view_set_up.ColorPalletViewSetUp
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel

class LibraryOb {
    fun observeEditFilePopUp(binding:MainActivityPopupEditFileBinding,
                             data:CreateFileViewModel.PopUpUI,
                             context:Context){
        binding.apply {
            if(txvFileTitle.text != data.txvLeftTopText) txvFileTitle.text = data.txvLeftTopText
            if(txvHint.text!=data.txvHintText) txvHint.text=data.txvHintText

            if(data.previousColor!=data.colorStatus){
                ColorPalletViewSetUp().apply {
                    changeColPalletCol(context, data.previousColor,false,colPaletBinding)
                    changeColPalletCol(context, data.colorStatus,true,colPaletBinding)
                }
//                        previousColor = it.colorStatus
            }
            if(imvFileType.tag!= data.drawableId){
                imvFileType.setImageDrawable(AppCompatResources.getDrawable(context,data.drawableId))
                imvFileType.tag = data.drawableId
            }
            edtCreatefile.apply {
                if(text.toString()!=data.edtTitleText) text= SpannableStringBuilder(data.edtTitleText)
                if(hint!= data.edtTitleHint) hint = data.edtTitleHint
            }
        }

    }

}