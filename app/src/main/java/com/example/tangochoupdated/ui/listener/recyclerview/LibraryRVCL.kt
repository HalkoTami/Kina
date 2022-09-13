package com.example.tangochoupdated.ui.listener.recyclerview

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.MyTouchListener
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.viewmodel.customClasses.LibRVState
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.viewmodel.DeletePopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel


class LibraryRVCL(val item: Any,
                  private val libraryViewModel: LibraryViewModel,
                  private val createFileViewModel: CreateFileViewModel,
                  private val rvBinding: LibraryFragRvItemBaseBinding,
                  private val deletePopUpViewModel: DeletePopUpViewModel,
                  private val createCardViewModel: CreateCardViewModel
): View.OnClickListener{
    override fun onClick(p0: View?) {
        rvBinding.apply {
            when(p0){
                libRvBaseContainer -> if(libraryViewModel.returnLeftSwipedItemExists()==true) libraryViewModel.makeAllUnSwiped()
                btnDelete       -> {
                    deletePopUpViewModel.setDeletingItem(mutableListOf(item))
                    deletePopUpViewModel.setConfirmDeleteVisible(true)
                }
                btnEditWhole    -> {
                    when(item ){
                        is File -> createFileViewModel.onClickEditFileInRV(item)
                        is Card -> createCardViewModel.onClickEditCardFromRV(item,)
                        else -> return
                    }

                }
                btnAddNewCard -> {
                    when(item){
                        is Card -> createCardViewModel.onClickAddNewCardRV(item)
                        else -> return
                    }
                }
            }
        }
    }


}