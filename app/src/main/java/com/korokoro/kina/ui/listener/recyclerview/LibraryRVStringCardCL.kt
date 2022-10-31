package com.korokoro.kina.ui.listener.recyclerview

import android.view.View
import androidx.navigation.NavController
import com.korokoro.kina.databinding.LibraryFragRvItemCardStringBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.customClasses.StringFragFocusedOn
import com.korokoro.kina.ui.viewmodel.CreateCardViewModel
import com.korokoro.kina.ui.viewmodel.CardTypeStringViewModel

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