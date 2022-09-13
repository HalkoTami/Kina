package com.example.tangochoupdated.ui.listener.menuBar

import android.view.View
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiBoxFragments
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel

class AnkiBoxTabChangeCL(private val ankiBoxFrag:AnkiHomeFragBaseBinding,
                         private val ankiBoxVM:AnkiBoxFragViewModel,
                         ): View.OnClickListener {

    override fun onClick(v: View?) {
        ankiBoxFrag.apply {
            when(v){
                tabAllFlashCardCoverToAnkiBox ->ankiBoxVM.changeTab(AnkiBoxFragments.AllFlashCardCovers)
                tabLibraryToAnkiBox ->ankiBoxVM.changeTab( AnkiBoxFragments.Library)
                tabFavouritesToAnkiBox ->  ankiBoxVM.changeTab(AnkiBoxFragments.Favourites)
            }
        }

    }
}