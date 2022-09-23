package com.korokoro.kina.ui.listener

import android.view.View
import com.korokoro.kina.databinding.AnkiHomeFragBaseBinding
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.viewmodel.AnkiBoxViewModel
import com.korokoro.kina.ui.viewmodel.AnkiBaseViewModel
import com.korokoro.kina.ui.viewmodel.AnkiSettingPopUpViewModel
import com.korokoro.kina.ui.viewmodel.EditFileViewModel

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
                        if(returnAnkiBoxItems().isNullOrEmpty()) ankiBoxViewModel.setModeCardsNotSelected(true)
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