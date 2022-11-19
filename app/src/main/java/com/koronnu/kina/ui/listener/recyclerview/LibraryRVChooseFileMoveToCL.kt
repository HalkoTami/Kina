package com.koronnu.kina.ui.listener.recyclerview

import android.view.View
import com.koronnu.kina.databinding.LibraryFragRvItemBaseBinding
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.viewmodel.ChooseFileMoveToViewModel
import com.koronnu.kina.ui.viewmodel.LibraryBaseViewModel

class LibraryRVChooseFileMoveToCL(val item: File,
                                  private val lVM: LibraryBaseViewModel,
                                  private val rvBinding: LibraryFragRvItemBaseBinding,
                                  private val chooseFileMoveToViewModel: ChooseFileMoveToViewModel,
): View.OnClickListener{
    override fun onClick(p0: View?) {
        rvBinding.apply {
            when(p0){
                libRvBaseContainer       -> if(item.fileStatus == FileStatus.FOLDER) lVM.openChooseFileMoveTo(item)
                rvBaseFrameLayLeft ->  chooseFileMoveToViewModel.onClickRvBtnMove(item)
            }
        }
    }

    //    click後にtouch event を設定しなおすと親子関係のコンフリクトが防げそう



}