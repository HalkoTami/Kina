package com.example.tangochoupdated.ui.listener.recyclerview

import android.content.Context
import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.MyTouchListener
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.viewmodel.ChooseFileMoveToViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibraryRVChooseFileMoveToCL(
                                  val item: File,
                                  val navController: NavController,
                                  private val lVM: LibraryViewModel,
                                  private val rvBinding: LibraryFragRvItemBaseBinding,
                                  private val chooseFileMoveToViewModel: ChooseFileMoveToViewModel,
): View.OnClickListener{
    override fun onClick(p0: View?) {
        rvBinding.apply {
            when(p0){
                baseContainer       ->  lVM.openChooseFileMoveTo(item,navController)
                btnSelect ->  chooseFileMoveToViewModel.onClickRvBtnMove(item)
            }
        }
    }

    //    click後にtouch event を設定しなおすと親子関係のコンフリクトが防げそう



}