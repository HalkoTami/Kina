package com.korokoro.kina.ui.listener.recyclerview

import android.view.View
import com.korokoro.kina.databinding.LibraryFragRvItemBaseBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.viewmodel.CreateCardViewModel
import com.korokoro.kina.ui.viewmodel.EditFileViewModel
import com.korokoro.kina.ui.viewmodel.DeletePopUpViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel


class LibraryRVCL(val item: Any,
                  private val libraryViewModel: LibraryBaseViewModel,
                  private val createFileViewModel: EditFileViewModel,
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