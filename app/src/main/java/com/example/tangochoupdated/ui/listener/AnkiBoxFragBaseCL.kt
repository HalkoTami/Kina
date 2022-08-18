package com.example.tangochoupdated.ui.listener

import android.view.View
import android.widget.TextView
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.AllFlashCardCoversFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.FavouriteAnkiBoxFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.LibraryItemsFragmentDirections
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel

class AnkiBoxFragBaseCL(
                         private val ankiBoxFrag:AnkiHomeFragBaseBinding,
                         private val ankiBoxVM:AnkiBoxFragViewModel
                         ): View.OnClickListener {

    override fun onClick(v: View?) {
        ankiBoxFrag.apply {
            when(v){
                btnStartAnki -> when(v.tag){

                }
            }
        }

    }
}