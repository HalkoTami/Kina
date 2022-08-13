package com.example.tangochoupdated.ui.library.clicklistener.recyclerview

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.core.view.children
import com.example.tangochoupdated.MyTouchListener
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragRvItemCardStringBinding
import com.example.tangochoupdated.databinding.LibraryFragRvItemFileBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.enumclass.StringFragFocusedOn
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.db.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.mainactivity.Animation

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