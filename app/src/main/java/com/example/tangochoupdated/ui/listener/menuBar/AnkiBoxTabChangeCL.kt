package com.example.tangochoupdated.ui.listener.menuBar

import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.TextView
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

    fun changeSelectedTab(select:AnkiBoxTab,before:AnkiBoxTab){
        fun getTextView(tab:AnkiBoxTab):TextView{
            return when(tab){
                AnkiBoxTab.AllFlashCardCovers-> ankiBoxFrag.tabAllFlashCardCoverToAnkiBox
                AnkiBoxTab.Library -> ankiBoxFrag.tabLibraryToAnkiBox
                AnkiBoxTab.Favourites -> ankiBoxFrag.tabFavouritesToAnkiBox
            }
        }
        getTextView(select).isSelected = true
        ankiBoxFrag.linLayTabChange.tag = select
        getTextView(before).isSelected = false
    }
    override fun onClick(v: View?) {
        ankiBoxFrag.apply {
            when(v){
                tabAllFlashCardCoverToAnkiBox ->if(linLayTabChange.tag != AnkiBoxTab.AllFlashCardCovers){
                    ankiBoxVM.setTabChangeAction(
                        AllFlashCardCoversFragmentDirections.toAllFlashCardCoverFrag()
                    )
                    changeSelectedTab(AnkiBoxTab.AllFlashCardCovers,linLayTabChange.tag as AnkiBoxTab)
                }
                tabLibraryToAnkiBox -> if(linLayTabChange.tag != AnkiBoxTab.Library){
                    ankiBoxVM.setTabChangeAction(
                        LibraryItemsFragmentDirections.toLibraryItemsFrag()
                    )
                    changeSelectedTab(AnkiBoxTab.Library,linLayTabChange.tag as AnkiBoxTab)
                }
                tabFavouritesToAnkiBox -> if(linLayTabChange.tag != AnkiBoxTab.Favourites){
                    ankiBoxVM.setTabChangeAction(
                        FavouriteAnkiBoxFragmentDirections.toAnkiBoxFavouriteFrag()
                    )
                    changeSelectedTab(AnkiBoxTab.Favourites,linLayTabChange.tag as AnkiBoxTab)
                }
            }
        }

    }
}