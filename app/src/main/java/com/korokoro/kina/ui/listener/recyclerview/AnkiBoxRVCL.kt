package com.korokoro.kina.ui.listener.recyclerview

import android.view.View
import com.korokoro.kina.databinding.AnkiHomeFragRvItemFileBinding
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.viewmodel.customClasses.AnkiBoxFragments
import com.korokoro.kina.ui.viewmodel.AnkiBoxViewModel

class AnkiBoxFileRVCL(val item: File,
                      private val tab: AnkiBoxFragments,
                      private val binding:AnkiHomeFragRvItemFileBinding,
                      val ankiBoxVM:AnkiBoxViewModel,
): View.OnClickListener{
    override fun onClick(p0: View?) {
        when(p0){
            binding.checkboxAnkiRv -> {
                if(p0.isSelected){
                    ankiBoxVM.removeFromAnkiBoxFileIds(item.fileId)
                } else ankiBoxVM.addToAnkiBoxFileIds(listOf(item.fileId))

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