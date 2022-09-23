package com.korokoro.kina.ui.listener.popUp

import android.view.View
import com.korokoro.kina.databinding.MainActivityPopupEditFileBinding
import com.korokoro.kina.db.enumclass.ColorStatus
import com.korokoro.kina.ui.viewmodel.EditFileViewModel

class EditFilePopUpCL(val bindingEditFile:MainActivityPopupEditFileBinding,
                      val createFileViewModel:EditFileViewModel): View.OnClickListener{
    override fun onClick(v: View?) {
        createFileViewModel.apply {
            bindingEditFile. apply {
                when(v){
                    root -> return
                    btnClose -> {
                        createFileViewModel.setEditFilePopUpVisible(false)

                    }
                    btnFinish ->{
                        if(edtCreatefile.text.toString() == "") {
                            edtCreatefile.hint = "タイトルが必要です"
                            return
                        } else {
                            createFileViewModel.setEditFilePopUpVisible(false)
                            createFileViewModel.onClickFinish(edtCreatefile.text!!.toString(),)
                        }
                    }
                }
                colPaletBinding.apply {
                    when(v){
                        imvColYellow -> onClickColorPallet(ColorStatus.YELLOW)
                        imvColGray -> onClickColorPallet(ColorStatus.GRAY)
                        imvColBlue -> onClickColorPallet(ColorStatus.BLUE)
                        imvColRed -> onClickColorPallet(ColorStatus.RED)
                        imvIconPalet -> arrayOf(imvColYellow,
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