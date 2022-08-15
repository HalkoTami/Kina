package com.example.tangochoupdated.ui.listener.menuBar

import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.AllFlashCardCoversFragment
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.AllFlashCardCoversFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.FavouriteAnkiBoxFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.LibraryItemsFragmentDirections
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel

class AnkiBoxTabChangeCL(
                         private val ankiBoxFrag:AnkiHomeFragBaseBinding,
                         private val ankiBoxVM:AnkiBoxFragViewModel
                         ): View.OnClickListener {
    override fun onClick(v: View?) {
        ankiBoxFrag.apply {
            when(v){
                tabAllFlashCardCoverToAnkiBox ->if(linLayTabChange.tag != AnkiBoxTab.AllFlashCardCovers){
                    ankiBoxVM.setTabChangeAction(
                        AllFlashCardCoversFragmentDirections.toAllFlashCardCoverFrag()
                    )
                    linLayTabChange.tag = AnkiBoxTab.AllFlashCardCovers
                }
                tabLibraryToAnkiBox -> if(linLayTabChange.tag != AnkiBoxTab.Library){

                    linLayTabChange.tag = AnkiBoxTab.Library
                }
                tabFavouritesToAnkiBox -> if(linLayTabChange.tag != AnkiBoxTab.Favourites){
                    ankiBoxVM.setTabChangeAction(
                        FavouriteAnkiBoxFragmentDirections.toAnkiBoxFavouriteFrag()
                    )
                    linLayTabChange.tag = AnkiBoxTab.Favourites
                }
            }
        }

    }
}