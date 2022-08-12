package com.example.tangochoupdated.ui.library.clicklistener

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.core.view.children
import com.example.tangochoupdated.MyTouchListener
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragRvItemFileBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.db.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.mainactivity.Animation

class LibraryRVChooseFileMoveToCL(val view: View,
                                 val context: Context,
                                 val item: File,
                                 private val createFileViewModel: CreateFileViewModel,
                                 private val lVM: LibraryViewModel,
                                 private val rvBinding: LibraryFragRvItemBaseBinding,
                                 private val fileBinding: LibraryFragRvItemFileBinding
): MyTouchListener(context){

    //    click後にtouch event を設定しなおすと親子関係のコンフリクトが防げそう
    override fun onSingleTap() {
        super.onSingleTap()
        rvBinding.apply {
            when(view){
                baseContainer       ->  TODO()
                btnSelect -> lVM.moveSelectedItemToFile(item)

            }
        }
    }


}