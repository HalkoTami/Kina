package com.example.tangochoupdated.ui.library.clicklistener.recyclerview

import android.content.Context
import android.view.View
import com.example.tangochoupdated.MyTouchListener
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibraryRVChooseFileMoveToCL(val view: View,
                                 val context: Context,
                                 val item: File,
                                 private val lVM: LibraryViewModel,
                                 private val rvBinding: LibraryFragRvItemBaseBinding,
): MyTouchListener(context){

    //    click後にtouch event を設定しなおすと親子関係のコンフリクトが防げそう
    override fun onSingleTap() {
        super.onSingleTap()
        rvBinding.apply {
            when(view){
                baseContainer       ->  lVM.openChooseFileMoveTo(item)
                btnSelect ->  lVM.moveSelectedItemToFile(item)
            }
        }
    }


}