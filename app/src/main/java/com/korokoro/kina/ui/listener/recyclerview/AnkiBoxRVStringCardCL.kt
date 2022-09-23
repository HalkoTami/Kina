package com.korokoro.kina.ui.listener.recyclerview

import android.view.View
import com.korokoro.kina.databinding.AnkiHomeFragRvItemCardBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.ui.viewmodel.AnkiBoxViewModel

class AnkiBoxRVStringCardCL(val card: Card,
                            private val binding:AnkiHomeFragRvItemCardBinding,
                            val ankiBoxVM:AnkiBoxViewModel,
): View.OnClickListener{

    private fun reverseSelectedStatus(view: View){
        view.isSelected = view.isSelected.not()
    }
    override fun onClick(p0: View?) {
        when(p0){
            binding.checkboxAnkiRv -> {
                if(p0.isSelected){
                    ankiBoxVM.removeFromAnkiBoxCardIds(card.id)
                } else ankiBoxVM.addToAnkiBoxCardIds(listOf(card.id))
                reverseSelectedStatus(p0)

            }
            binding.btnAnkiRvCardSetFlag -> {
                p0.isSelected = card.flag.not()
                ankiBoxVM.updateCardFlagStatus(card)
            }


        }

    }
}