package com.example.tangochoupdated.ui.listener.recyclerview

import android.view.View
import com.example.tangochoupdated.databinding.AnkiHomeFragRvItemFileBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.AnkiBoxFragments
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.AllFlashCardCoversFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.LibraryItemsFragmentDirections
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel

class AnkiBoxFileRVCL(val item: File,
                      private val tab:AnkiBoxFragments,
                      private val binding:AnkiHomeFragRvItemFileBinding,
                      val ankiBoxVM:AnkiBoxFragViewModel,
): View.OnClickListener{
    override fun onClick(p0: View?) {
        when(p0){
            binding.checkboxAnkiRv -> {
                if(p0.isSelected){
                    ankiBoxVM.removeFromAnkiBoxFileIds(item.fileId)
                } else ankiBoxVM.addToAnkiBoxFileIds(listOf(item.fileId))
                p0.isSelected = !p0.isSelected

            }
            binding.root -> {
                when(tab){
                    AnkiBoxFragments.Favourites -> {

                    }
                    AnkiBoxFragments.Library -> ankiBoxVM.openFile(item)
                    AnkiBoxFragments.AllFlashCardCovers ->ankiBoxVM.openFile(item)
                }
            }


        }

    }
}