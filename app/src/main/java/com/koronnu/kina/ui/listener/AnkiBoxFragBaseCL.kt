package com.koronnu.kina.ui.listener

import android.view.View
import com.koronnu.kina.databinding.AnkiHomeFragBaseBinding
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.viewmodel.AnkiBoxViewModel
import com.koronnu.kina.ui.viewmodel.AnkiBaseViewModel
import com.koronnu.kina.ui.viewmodel.AnkiSettingPopUpViewModel
import com.koronnu.kina.ui.viewmodel.EditFileViewModel

class AnkiBoxFragBaseCL(val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel,
                        private val ankiBoxFrag:AnkiHomeFragBaseBinding,
                        private val ankiBaseViewModel:AnkiBaseViewModel,
                        private val ankiBoxViewModel:AnkiBoxViewModel,
                        private val createFileViewModel:EditFileViewModel
                         ): View.OnClickListener {

    override fun onClick(v: View?) {
        ankiBoxFrag.apply {
            when(v){
                btnStartAnki -> {
                    ankiBaseViewModel.setSettingVisible(true)
                    ankiBoxViewModel.apply {
                        if(returnAnkiBoxItems().isEmpty()) ankiBoxViewModel.setModeCardsNotSelected(true)
                    }
                }
                btnAddToFavouriteAnkiBox -> {
                    if(btnAddToFavouriteAnkiBox.isSelected.not()&&ankiBoxViewModel.returnAnkiBoxItems().isEmpty().not()){
                            createFileViewModel.onClickCreateFile(FileStatus.ANKI_BOX_FAVOURITE)
                    } else return

                }
            }
        }

    }
}