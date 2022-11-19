package com.koronnu.kina.ui.listener.menuBar

import android.view.View
import com.koronnu.kina.databinding.AnkiHomeFragBaseBinding
import com.koronnu.kina.customClasses.enumClasses.AnkiBoxFragments
import com.koronnu.kina.ui.viewmodel.AnkiBoxViewModel

class AnkiBoxTabChangeCL(private val ankiBoxFrag:AnkiHomeFragBaseBinding,
                         private val ankiBoxVM:AnkiBoxViewModel,
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