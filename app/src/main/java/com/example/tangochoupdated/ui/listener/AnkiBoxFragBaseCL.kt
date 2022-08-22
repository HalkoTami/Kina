package com.example.tangochoupdated.ui.listener

import android.view.View
import android.widget.TextView
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.AllFlashCardCoversFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.FavouriteAnkiBoxFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.LibraryItemsFragmentDirections
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel

class AnkiBoxFragBaseCL( val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel,
                         private val ankiBoxFrag:AnkiHomeFragBaseBinding,
                         private val ankiBaseViewModel:AnkiFragBaseViewModel,
                         private val ankiBoxViewModel:AnkiBoxFragViewModel
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
            }
        }

    }
}