package com.example.tangochoupdated.ui.listener.recyclerview

import android.view.View
import com.example.tangochoupdated.databinding.LibraryFragRvItemCardStringBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.StringFragFocusedOn
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.StringCardViewModel

class LibraryRVStringCardCL(val item: Card,
                            private val stringCardViewModel: StringCardViewModel,
                            private val createCardViewModel: CreateCardViewModel,
                            private val binding: LibraryFragRvItemCardStringBinding,
): View.OnClickListener{

    override fun onClick(v: View?) {
        when(v){
            binding.btnEdtBack-> stringCardViewModel.setFocusedOn(StringFragFocusedOn.BackContent)
            binding.btnEdtFront-> stringCardViewModel.setFocusedOn(StringFragFocusedOn.FrontContent)
        }
        createCardViewModel.onClickEditCard(item)
    }
}