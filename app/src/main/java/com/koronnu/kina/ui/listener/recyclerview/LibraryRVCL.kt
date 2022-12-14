package com.koronnu.kina.ui.listener.recyclerview


import android.view.MotionEvent
import android.view.View
import com.koronnu.kina.customClasses.enumClasses.ListAttributes
import com.koronnu.kina.databinding.LibraryFragRvItemBaseBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.ui.listener.MyTouchListener
import com.koronnu.kina.ui.viewmodel.CreateCardViewModel
import com.koronnu.kina.ui.viewmodel.EditFileViewModel
import com.koronnu.kina.ui.viewmodel.DeletePopUpViewModel
import com.koronnu.kina.tabLibrary.LibraryBaseViewModel


class LibraryRVCL(val item: Any,
                  private val libraryViewModel: LibraryBaseViewModel,
                  private val createFileViewModel: EditFileViewModel,
                  private val rvBinding: LibraryFragRvItemBaseBinding,
                  private val deletePopUpViewModel: DeletePopUpViewModel,
                  private val createCardViewModel: CreateCardViewModel,
                  val v:View,
): MyTouchListener(v.context){
    override fun onSingleTap(motionEvent: MotionEvent?) {
        super.onSingleTap(motionEvent)
        rvBinding.apply {
            when(v){
                contentBindingFrame -> {
                    if(libraryViewModel.getOnlySwipeActive||libraryViewModel.getOnlyLongClickActive) return
                    else if(libraryViewModel.returnLeftSwipedItemExists()) libraryViewModel.makeAllUnSwiped()
                    else if(libraryViewModel.returnMultiSelectMode()){
                        libraryViewModel.onClickRvSelect(
                            if(btnSelect.isSelected) ListAttributes.Remove else ListAttributes.Add,item)
                        btnSelect.isSelected = btnSelect.isSelected.not()
                    }else{
                        when(item){
                            is File -> libraryViewModel.openNextFile(item)
                            is Card -> createCardViewModel.onClickEditCardFromRV(item)
                        }

                    }

                }
                btnDelete       -> {
                    deletePopUpViewModel.setDeletingItem(mutableListOf(item))
                    deletePopUpViewModel.setConfirmDeleteVisible(true)
                }
                btnEditWhole    -> {
                    when(item ){
                        is File -> createFileViewModel.onClickEditFileInRV(item)
                        is Card -> createCardViewModel.onClickEditCardFromRV(item)
                        else -> return
                    }

                }
                btnAddNewCard -> {
                    when(item){
                        is Card -> {
                            createCardViewModel.onClickAddNewCardRV(item)
                        }
                        else -> return
                    }
                }
            }
        }

    }

    override fun onLongClick(motionEvent: MotionEvent?) {
        super.onLongClick(motionEvent)
        if(libraryViewModel.getOnlySwipeActive) return
        rvBinding.btnSelect.isSelected = true
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.onClickRvSelect(ListAttributes.Add,item)
        libraryViewModel.getDoAfterLongClick()
    }

}

class LibraryRVCLNewCard(val item: Card,
                         private val libraryViewModel: LibraryBaseViewModel,
                         private val createFileViewModel: EditFileViewModel,
                         private val rvBinding: LibraryFragRvItemBaseBinding,
                         private val deletePopUpViewModel: DeletePopUpViewModel,
                         private val createCardViewModel: CreateCardViewModel,
                         val v:View,
): MyTouchListener(v.context){
    override fun onSingleTap(motionEvent: MotionEvent?) {
        super.onSingleTap(motionEvent)
        rvBinding.apply {
            when(v){
                contentBindingFrame -> {
                    if(libraryViewModel.returnLeftSwipedItemExists()==true) libraryViewModel.makeAllUnSwiped()
                    else if(libraryViewModel.returnMultiSelectMode()){
                        val a = item as Card
                        if(a.cardBefore == null) return
                        libraryViewModel.onClickRvSelect(
                            if(btnSelect.isSelected) ListAttributes.Remove else ListAttributes.Add,item)
                        btnSelect.isSelected = btnSelect.isSelected.not()
                    }else{
                        createCardViewModel.onClickEditCardFromRV(item)

                    }

                }
                btnDelete       -> {
                    deletePopUpViewModel.setDeletingItem(mutableListOf(item))
                    deletePopUpViewModel.setConfirmDeleteVisible(true)
                }
                btnEditWhole    -> createCardViewModel.onClickEditCardFromRV(item)
                btnAddNewCard -> createCardViewModel.onClickAddNewCardRV(item)
            }
        }

    }

    override fun onLongClick(motionEvent: MotionEvent?) {
        super.onLongClick(motionEvent)
        rvBinding.btnSelect.isSelected = true
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.onClickRvSelect(ListAttributes.Add,item)
    }

}