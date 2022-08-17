package com.example.tangochoupdated.ui.listener.recyclerview

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.view.children
import androidx.navigation.NavController
import com.example.tangochoupdated.MyTouchListener
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.databinding.AnkiHomeFragRvItemBinding
import com.example.tangochoupdated.databinding.AnkiHomeFragRvItemFileBinding
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.AllFlashCardCoversFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.FavouriteAnkiBoxFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.LibraryItemsFragmentDirections
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel

class AnkiBoxFileRVCL(val item: File,
                  private val tab:AnkiBoxTab,
                      private val binding:AnkiHomeFragRvItemFileBinding,
                      val ankiBoxVM:AnkiBoxFragViewModel
): View.OnClickListener{
    override fun onClick(p0: View?) {
        when(p0){
            binding.checkboxAnkiRv -> ankiBoxVM.onClickCheckBox(item)
            binding.root -> {
                when(tab){
                    AnkiBoxTab.Favourites -> {

                    }
                    AnkiBoxTab.Library -> {
                        val a = LibraryItemsFragmentDirections.toLibraryItemsFrag()
                        a.fileId = intArrayOf(item.fileId)
                        a.flashCard = item.fileStatus == FileStatus.TANGO_CHO_COVER
                        ankiBoxVM.setTabChangeAction(a)
                    }
                    AnkiBoxTab.AllFlashCardCovers ->{
                        val a = AllFlashCardCoversFragmentDirections.toAllFlashCardCoverFrag()
                        a.fileId = intArrayOf(item.fileId)
                        ankiBoxVM.setTabChangeAction(a)
                    }
                }
            }


        }

    }
}