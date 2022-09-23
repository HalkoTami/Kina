package com.korokoro.kina.ui.listener.recyclerview

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.navigation.NavController
import com.korokoro.kina.ui.listener.MyTouchListener
import com.korokoro.kina.databinding.LibraryFragRvItemBaseBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.ui.viewmodel.customClasses.LibRVState
import com.korokoro.kina.ui.viewmodel.CreateCardViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel
import com.korokoro.kina.ui.animation.Animation
import com.korokoro.kina.ui.fragment.base_frag_con.CreateCardFragmentBaseDirections
import com.korokoro.kina.ui.viewmodel.DeletePopUpViewModel

class LibraryRVCardCL(val view: View,
                      val context: Context,
                      val item: Card,
                      val navController: NavController,
                      private val createCardViewModel: CreateCardViewModel,
                      private val lVM: LibraryBaseViewModel,
                      private val rvBinding: LibraryFragRvItemBaseBinding,
                      private val deletePopUpViewModel: DeletePopUpViewModel
): MyTouchListener(context){

    //    click後にtouch event を設定しなおすと親子関係のコンフリクトが防げそう
    override fun onSingleTap(motionEvent: MotionEvent?) {
        super.onSingleTap(motionEvent)
        rvBinding.apply {
            when(view){
                libRvBaseContainer       ->  {
                    if(lVM.returnLeftSwipedItemExists()==true){
                        lVM.makeAllUnSwiped()
                    }
                    else if(lVM.returnMultiSelectMode()==true){
                        lVM.onClickSelectableItem(item,btnSelect.isSelected.not())
                        btnSelect.isSelected = btnSelect.isSelected.not()
                    }else{
//                        createCardViewModel.setStartingPosition(item.libOrder)
                        navController.navigate(CreateCardFragmentBaseDirections.openCreateCard())
                    }

                }
                btnSelect -> TODO()
                btnDelete       -> deletePopUpViewModel.apply{
                    setDeletingItem(mutableListOf(item))
                    setConfirmDeleteVisible(true)
                }
                btnAddNewCard -> {
                    createCardViewModel.onClickAddNewCardRV(item)
                }
            }
        }
    }
    override fun onScrollLeft(distanceX: Float, motionEvent: MotionEvent?) {
        super.onScrollLeft(distanceX, motionEvent)
//        rvBinding.apply {
//            if(rvBinding.root.tag== LibRVState.Plane){
//                linLaySwipeShow.layoutParams.width = 1
//                linLaySwipeShow.requestLayout()
//                linLaySwipeShow.children.iterator().forEach {
//                    it.visibility = View.VISIBLE
//                }
//                linLaySwipeShow.visibility = View.VISIBLE
//                rvBinding.root.tag = LibRVState.LeftSwiping
//
//            }else if(rvBinding.root.tag== LibRVState.LeftSwiping) {
//                if(rvBinding.root.tag!= LibRVState.LeftSwiping){
//                    rvBinding.root.tag = LibRVState.LeftSwiping
//                }
//                linLaySwipeShow.layoutParams.width = distanceX.toInt()/5
//                linLaySwipeShow.requestLayout()
//
//            }
//
//        }
//        lVM.setRVCover(LibraryViewModel.RvCover(visible = true))
    }
    override fun onLongClick(motionEvent: MotionEvent?) {
        super.onLongClick(motionEvent)
        rvBinding.btnSelect.isSelected = true
        lVM.setMultipleSelectMode(true)
        lVM.onClickSelectableItem(item,true)
    }
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(event?.actionMasked== MotionEvent.ACTION_UP||event?.actionMasked== MotionEvent.ACTION_CANCEL){
            if(rvBinding.root.tag== LibRVState.LeftSwiping){
                if(rvBinding.linLaySwipeShow.width <25){
                    Animation().animateLibRVLeftSwipeLay(rvBinding.linLaySwipeShow,false)
                    rvBinding.root.tag = LibRVState.Plane
                }
                else if (rvBinding.linLaySwipeShow.width>=25){
                    Animation().animateLibRVLeftSwipeLay(rvBinding.linLaySwipeShow ,true)
                    rvBinding.root.tag = LibRVState.LeftSwiped
                    lVM.setLeftSwipedItemExists(true)
                }

            }

        }
        return super.onTouch(v, event)
    }

}