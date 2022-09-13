package com.example.tangochoupdated.ui.listener.recyclerview

import android.view.View
import com.example.tangochoupdated.databinding.AnkiHomeFragRvItemCardBinding
import com.example.tangochoupdated.databinding.AnkiHomeFragRvItemFileBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiBoxFragments
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel

class AnkiBoxRVStringCardCL(val card: Card,
                      private val tab: AnkiBoxFragments,
                      private val binding:AnkiHomeFragRvItemCardBinding,
                      val ankiBoxVM:AnkiBoxFragViewModel,
): View.OnClickListener{
    override fun onClick(p0: View?) {
        when(p0){
            binding.checkboxAnkiRv -> {
                if(p0.isSelected){
                    ankiBoxVM.removeFromAnkiBoxCardIds(card.id)
                } else ankiBoxVM.addToAnkiBoxCardIds(listOf(card.id))

            }


        }

    }
}