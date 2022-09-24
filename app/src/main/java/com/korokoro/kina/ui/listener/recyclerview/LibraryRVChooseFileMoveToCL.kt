package com.korokoro.kina.ui.listener.recyclerview

import android.view.View
import com.korokoro.kina.databinding.LibraryFragRvItemBaseBinding
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.viewmodel.ChooseFileMoveToViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel

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