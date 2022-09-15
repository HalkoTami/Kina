package com.example.tangochoupdated.ui.listener.recyclerview

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.ui.listener.MyTouchListener
import com.example.tangochoupdated.R
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.viewmodel.customClasses.LibRVState
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibraryRVTouchListener(val context: Context,
                             val recyclerView: RecyclerView,
                             val libraryViewModel: LibraryViewModel,
                             private val scrollView: NestedScrollView,
                             private val createCardViewModel: CreateCardViewModel,
): MyTouchListener(context){
    override fun onSingleTap(motionEvent: MotionEvent?) {
        super.onSingleTap(motionEvent)
        val view = recyclerView.findChildViewUnder(motionEvent!!.x,motionEvent.y)
        val conLay = view?.findViewById<ConstraintLayout>(R.id.lib_rv_base_container) ?:return
        val btnSelect = view.findViewById<ImageView>(R.id.btn_select)
        val position = recyclerView.getChildAdapterPosition(view)
        val item = libraryViewModel.returnParentRVItems()[position]
        conLay.apply {
            when(view){
                conLay       ->  {
                    if(libraryViewModel.returnLeftSwipedItemExists()==true){
                        libraryViewModel.makeAllUnSwiped()
                    }
                    else if(libraryViewModel.returnMultiSelectMode()==true){
                        libraryViewModel.onClickSelectableItem(item,btnSelect.isSelected.not())
                        btnSelect.isSelected = btnSelect.isSelected.not()
                    }else{
                        when(item){
                            is File -> libraryViewModel.openNextFile(item)
                            is Card -> createCardViewModel.onClickEditCardFromRV(item)
                        }

                    }

                }
            }
        }
    }

    private var startPosition : MotionEvent? = null
    private var swipingDistance:Float = 1f
    override fun onScrollLeft(distanceX: Float, motionEvent: MotionEvent?) {
        super.onScrollLeft(distanceX, motionEvent)
        scrollView.requestDisallowInterceptTouchEvent(true)
        startPosition = motionEvent
        swipingDistance = distanceX
        val view = recyclerView.findChildViewUnder(motionEvent!!.x,motionEvent.y)
        val lineLay = view?.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return
        val conLay = view.findViewById<ConstraintLayout>(R.id.lib_rv_base_container) ?:return
        conLay.apply {
            if(conLay.tag== LibRVState.Plane){
                lineLay.layoutParams.width = 1
                lineLay.requestLayout()
                lineLay.children.iterator().forEach {
                    it.visibility = View.VISIBLE
                }
                lineLay.visibility = View.VISIBLE
                conLay.tag = LibRVState.LeftSwiping

            }else if(conLay.tag== LibRVState.LeftSwiping) {
                if(conLay.tag!= LibRVState.LeftSwiping){
                    conLay.tag = LibRVState.LeftSwiping
                }

                lineLay.layoutParams.width = swipingDistance.toInt()/5 + 1
                lineLay.requestLayout()

            }

        }
    }
    override fun onLongClick(motionEvent: MotionEvent?) {
        super.onLongClick(motionEvent)
        val view = recyclerView.findChildViewUnder(motionEvent!!.x,motionEvent.y)
        val btnSelect = view?.findViewById<ImageView>(R.id.btn_select) ?:return
        val position = recyclerView.getChildAdapterPosition(view)
        val item = libraryViewModel.returnParentRVItems()[position]
        btnSelect.isSelected = true
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.onClickSelectableItem(item,true)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        if(startPosition==null){
            startPosition = event
        } else {
            if((event?.actionMasked== MotionEvent.ACTION_UP)){
                val view = recyclerView.findChildViewUnder(startPosition!!.x,startPosition!!.y)
                val lineLay = view?.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return true
                val conLay = view.findViewById<ConstraintLayout>(R.id.lib_rv_base_container) ?:return true
                if(conLay.tag== LibRVState.LeftSwiping){
                    if(lineLay.layoutParams.width <25){
                        Animation().animateLibRVLeftSwipeLay(lineLay,false)
                        conLay.tag = LibRVState.Plane
                    }
                    else if (lineLay.layoutParams.width>=25){
                        Animation().animateLibRVLeftSwipeLay(lineLay ,true)
                        conLay.tag = LibRVState.LeftSwiped
                        libraryViewModel.setLeftSwipedItemExists(true)
                    }

                }

            }
        }


        return super.onTouch(v, event)
    }
}