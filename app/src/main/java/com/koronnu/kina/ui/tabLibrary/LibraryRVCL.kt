package com.koronnu.kina.ui.tabLibrary


import android.view.MotionEvent
import android.view.View
import com.koronnu.kina.data.model.enumClasses.ListAttributes
import com.koronnu.kina.databinding.LibraryFragRvItemBaseBinding
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.util.MyTouchListener
import com.koronnu.kina.ui.editCard.CreateCardViewModel
import com.koronnu.kina.ui.EditFileViewModel


class LibraryRVCL(val item: Any,
                  private val libraryBaseViewModel: LibraryBaseViewModel,
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
                    if(libraryBaseViewModel.getOnlySwipeActive||libraryBaseViewModel.getOnlyLongClickActive) return
                    else if(libraryBaseViewModel.returnLeftSwipedItemExists()) libraryBaseViewModel.makeAllUnSwiped()
                    else if(libraryBaseViewModel.returnMultiSelectMode()){
                        libraryBaseViewModel.onClickRvSelect(
                            if(btnSelect.isSelected) ListAttributes.Remove else ListAttributes.Add,item)
                        btnSelect.isSelected = btnSelect.isSelected.not()
                    }else{
                        when(item){
                            is File -> libraryBaseViewModel.openNextFile(item)
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
        if(libraryBaseViewModel.getOnlySwipeActive) return
        rvBinding.btnSelect.isSelected = true
        libraryBaseViewModel.setMultipleSelectMode(true)
        libraryBaseViewModel.onClickRvSelect(ListAttributes.Add,item)
        libraryBaseViewModel.getDoAfterLongClick()
    }

}

