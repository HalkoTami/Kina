package com.koronnu.kina.ui.tabLibrary.chooseFileMoveTo

import android.view.View
import com.koronnu.kina.databinding.LibraryFragRvItemBaseBinding
import com.koronnu.kina.data.source.local.entity.File

class LibraryRVChooseFileMoveToCL(val item: File,
                                  private val rvBinding: LibraryFragRvItemBaseBinding,
                                  private val chooseFileMoveToViewModel: ChooseFileMoveToViewModel,
): View.OnClickListener{
    override fun onClick(p0: View?) {
        rvBinding.apply {
            when(p0){
                libRvBaseContainer  -> chooseFileMoveToViewModel.onClickChooseFileMoveToRvBaseContainer(item)
                rvBaseFrameLayLeft  -> chooseFileMoveToViewModel.onClickRvBtnMove(item)
            }
        }
    }

    //    click後にtouch event を設定しなおすと親子関係のコンフリクトが防げそう



}