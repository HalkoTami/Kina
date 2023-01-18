package com.koronnu.kina.ui.tabAnki.ankiBox

import android.view.View
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.customClasses.enumClasses.AnkiBoxFragments
import com.koronnu.kina.databinding.ListItemAnkiBoxRvFileBinding
import com.koronnu.kina.ui.viewmodel.AnkiBoxViewModel

class AnkiBoxFileRVCL(val item: File,
                      private val tab: AnkiBoxFragments,
                      private val binding:ListItemAnkiBoxRvFileBinding,
                      val ankiBoxVM:AnkiBoxViewModel,
): View.OnClickListener{
    override fun onClick(p0: View?) {
        when(p0){
            binding.imvChbIsInAnkiBox -> {
                if(p0.isSelected){
                    ankiBoxVM.removeFromAnkiBoxFileIds(item.fileId)
                } else ankiBoxVM.addToAnkiBoxFileIds(listOf(item.fileId))

            }
            binding.root -> {
                when(tab){
                    AnkiBoxFragments.Favourites -> ankiBoxVM.openFile(item)
                    AnkiBoxFragments.Library -> ankiBoxVM.openFile(item)
                    AnkiBoxFragments.AllFlashCardCovers ->ankiBoxVM.openFile(item)
                }
            }


        }

    }
}