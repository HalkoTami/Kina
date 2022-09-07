package com.example.tangochoupdated.ui.listener

import android.view.View
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel

class AnkiBoxFragBaseCL( val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel,
                         private val ankiBoxFrag:AnkiHomeFragBaseBinding,
                         private val ankiBaseViewModel:AnkiFragBaseViewModel,
                         private val ankiBoxViewModel:AnkiBoxFragViewModel,
                         private val createFileViewModel:CreateFileViewModel
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
                    createFileViewModel.onClickCreateFile(FileStatus.ANKI_BOX_FAVOURITE)
                }
            }
        }

    }
}