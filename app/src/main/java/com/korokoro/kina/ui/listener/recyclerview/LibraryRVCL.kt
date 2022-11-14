package com.korokoro.kina.ui.listener.recyclerview


import android.view.MotionEvent
import android.view.View
import com.korokoro.kina.customClasses.enumClasses.ListAttributes
import com.korokoro.kina.databinding.LibraryFragRvItemBaseBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.listener.MyTouchListener
import com.korokoro.kina.ui.viewmodel.CreateCardViewModel
import com.korokoro.kina.ui.viewmodel.EditFileViewModel
import com.korokoro.kina.ui.viewmodel.DeletePopUpViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel


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
                    if(libraryViewModel.returnLeftSwipedItemExists()==true) libraryViewModel.makeAllUnSwiped()
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
        rvBinding.btnSelect.isSelected = true
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.onClickRvSelect(ListAttributes.Add,item)
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