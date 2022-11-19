package com.koronnu.kina.ui.listener.popUp

import android.view.View
import com.koronnu.kina.databinding.MainActivityPopupEditFileBinding
import com.koronnu.kina.db.enumclass.ColorStatus
import com.koronnu.kina.ui.viewmodel.EditFileViewModel

class EditFilePopUpCL(private val bindingEditFile:MainActivityPopupEditFileBinding,
                      private val createFileViewModel:EditFileViewModel): View.OnClickListener{
    override fun onClick(v: View?) {
        createFileViewModel.apply {
            bindingEditFile. apply {
                when(v){
                    root -> {
                        return
                    }
                    btnClose -> {
                        createFileViewModel.setEditFilePopUpVisible(false)

                    }
                    btnFinish ->{
                        if(edtFileTitle.text.toString() == "") {
                            edtFileTitle.hint = "タイトルが必要です"
                            return
                        } else {
                            createFileViewModel.setEditFilePopUpVisible(false)
                            createFileViewModel.onClickFinish(edtFileTitle.text!!.toString(),)
                        }
                    }
                }
                colPaletBinding.apply {
                    when(v){
                        imvColYellow -> onClickColorPallet(ColorStatus.YELLOW)
                        imvColGray -> onClickColorPallet(ColorStatus.GRAY)
                        imvColBlue -> onClickColorPallet(ColorStatus.BLUE)
                        imvColRed -> onClickColorPallet(ColorStatus.RED)
                        imvIconPallet -> arrayOf(imvColYellow,
                            imvColGray,
                            imvColBlue,
                            imvColRed,).onEach { if(it.visibility==View.INVISIBLE)it.visibility = View.VISIBLE else it.visibility= View.INVISIBLE }
                        else -> {
                            createFileViewModel.setEditFilePopUpVisible(false)
                        }
                    }

                }
            }
        }


    }
}