package com.example.tangochoupdated.ui.listener.popUp

import android.view.View
import android.widget.FrameLayout
import com.example.tangochoupdated.databinding.MainActivityPopupEditFileBinding
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel

class EditFilePopUpCL(val bindingEditFile:MainActivityPopupEditFileBinding,
                      val container:FrameLayout,val background:View,
                      val createFileViewModel:CreateFileViewModel): View.OnClickListener{
    override fun onClick(v: View?) {
        createFileViewModel.apply {
            bindingEditFile. apply {
                when(v){
                    root -> return
                    btnClose -> {
                        container.visibility = View.GONE
                        background.visibility = View.GONE
                    }
                    btnFinish ->{
                        if(edtCreatefile.text.toString() == "") {
                            edtCreatefile.hint = "タイトルが必要です"
                            return
                        } else {
                            container.visibility = View.GONE
                            background.visibility = View.GONE
                            createFileViewModel.onClickFinish(edtCreatefile.text!!.toString())
                        }
                    }
                }
                colPaletBinding.apply {
                    when(v){
                        imvColYellow -> onClickColorPalet(ColorStatus.YELLOW)
                        imvColGray -> onClickColorPalet(ColorStatus.GRAY)
                        imvColBlue -> onClickColorPalet(ColorStatus.BLUE)
                        imvColRed -> onClickColorPalet(ColorStatus.RED)
                        imvIconPalet -> arrayOf(imvColYellow,
                            imvColGray,
                            imvColBlue,
                            imvColRed,).onEach { if(it.visibility==View.INVISIBLE)it.visibility = View.VISIBLE else it.visibility= View.INVISIBLE }
                        else -> {
                            container.visibility = View.GONE
                            background.visibility = View.GONE
                        }
                    }

                }
            }
        }


    }
}