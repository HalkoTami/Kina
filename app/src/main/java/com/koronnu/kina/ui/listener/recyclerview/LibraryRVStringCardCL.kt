package com.koronnu.kina.ui.listener.recyclerview

import android.view.View
import com.koronnu.kina.databinding.LibraryFragRvItemCardStringBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.customClasses.enumClasses.StringFragFocusedOn
import com.koronnu.kina.ui.viewmodel.CreateCardViewModel
import com.koronnu.kina.ui.viewmodel.CardTypeStringViewModel

class LibraryRVStringCardCL(val item: Card,
                            private val stringCardViewModel: CardTypeStringViewModel,
                            private val createCardViewModel: CreateCardViewModel,
                            private val binding: LibraryFragRvItemCardStringBinding,
): View.OnClickListener{

    override fun onClick(v: View?) {
        when(v){
            binding.btnEdtBack-> { stringCardViewModel.setFocusedOn(StringFragFocusedOn.BackContent)
            }
            binding.btnEdtFront-> stringCardViewModel.setFocusedOn(StringFragFocusedOn.FrontContent)
        }
        createCardViewModel.onClickEditCardFromRV(item)
    }
}