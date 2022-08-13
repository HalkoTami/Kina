package com.example.tangochoupdated.ui.library.clicklistener.recyclerview

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.core.view.children
import com.example.tangochoupdated.MyTouchListener
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.mainactivity.Animation

class LibraryRVCardCL(val view: View,
                      val context: Context,
                      val item: Card,
                      private val createCardViewModel: CreateCardViewModel,
                      private val lVM: LibraryViewModel,
                      private val rvBinding: LibraryFragRvItemBaseBinding,
): MyTouchListener(context){

    //    click後にtouch event を設定しなおすと親子関係のコンフリクトが防げそう
    override fun onSingleTap() {
        super.onSingleTap()
        rvBinding.apply {
            when(view){
                baseContainer       ->  {
                    if(lVM.returnLeftSwipedItemExists()==true){
                        lVM.makeAllUnSwiped()
                    }
                    else if(lVM.returnMultiSelectMode()==true){
                        lVM.onClickSelectableItem(item,btnSelect.isSelected.not())
                        btnSelect.isSelected = btnSelect.isSelected.not()
                    }else{
                        createCardViewModel.onClickEditCard(item)
                    }

                }
                btnSelect -> TODO()
                btnDelete       -> lVM.onClickDeleteRVItem(item)
            }
        }
    }
    override fun onScrollLeft(distanceX: Float, motionEvent: MotionEvent?) {
        super.onScrollLeft(distanceX, motionEvent)
        rvBinding.apply {
            if(rvBinding.root.tag== LibRVState.Plane){
                linLaySwipeShow.layoutParams.width = 1
                linLaySwipeShow.requestLayout()
                linLaySwipeShow.children.iterator().forEach {
                    it.visibility = View.VISIBLE
                }
                linLaySwipeShow.visibility = View.VISIBLE
                rvBinding.root.tag = LibRVState.LeftSwiping

            }else if(rvBinding.root.tag== LibRVState.LeftSwiping) {
                if(rvBinding.root.tag!= LibRVState.LeftSwiping){
                    rvBinding.root.tag = LibRVState.LeftSwiping
                }
                linLaySwipeShow.layoutParams.width = distanceX.toInt()/5
                linLaySwipeShow.requestLayout()

            }

        }
    }
    override fun onLongClick() {
        super.onLongClick()
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