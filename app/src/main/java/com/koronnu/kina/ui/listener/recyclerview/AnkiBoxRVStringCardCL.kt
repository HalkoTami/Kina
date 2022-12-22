package com.koronnu.kina.ui.listener.recyclerview

import android.view.View
import com.koronnu.kina.databinding.ListItemAnkiBoxRvCardBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.ui.viewmodel.AnkiBoxViewModel
class AnkiBoxRVStringCardCL(private val card: Card,
                            private val binding:ListItemAnkiBoxRvCardBinding,
                            private val ankiBoxVM:AnkiBoxViewModel,

): View.OnClickListener{

    private fun reverseSelectedStatus(view: View){
        view.isSelected = view.isSelected.not()
    }
    override fun onClick(p0: View?) {
        when(p0){
            binding.imvChbIsInAnkiBox -> {
                if(p0.isSelected){
                    ankiBoxVM.removeFromAnkiBoxCardIds(card.id)
                } else ankiBoxVM.addToAnkiBoxCardIds(listOf(card.id))
                reverseSelectedStatus(p0)

            }


        }

    }
}