package com.korokoro.kina.ui.listener.menuBar

import android.view.View
import com.korokoro.kina.databinding.AnkiHomeFragBaseBinding
import com.korokoro.kina.ui.viewmodel.customClasses.AnkiBoxFragments
import com.korokoro.kina.ui.viewmodel.AnkiBoxViewModel

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