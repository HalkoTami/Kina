package com.korokoro.kina.ui.view_set_up
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.korokoro.kina.ui.viewmodel.FlipTypeAndCheckViewModel

class AnkiTypeAnswerFragViewSetUp(val typeAndCheckViewModel: FlipTypeAndCheckViewModel) {
   fun getKeyBoardChangeListener(view:ConstraintLayout,activity:FragmentActivity):ViewTreeObserver.OnGlobalLayoutListener{
       return object :ViewTreeObserver.OnGlobalLayoutListener{
           override fun onGlobalLayout() {
               view.viewTreeObserver.removeOnGlobalLayoutListener(this)
               val r = Rect()
               view.getWindowVisibleDisplayFrame(r)
               val heightDiff: Int = view.rootView.height - (r.bottom - r.top)
//               if(typeAndCheckViewModel.returnKeyBoardVisible()){
//                   view.requestFocus()
//                   Toast.makeText(activity,"layouit", Toast.LENGTH_SHORT).show()
//                   typeAndCheckViewModel.setKeyBoardVisible(false)
////                   keyboardVisible = false
//               }

               if (heightDiff>500) {
                   Toast.makeText(activity,"if called", Toast.LENGTH_SHORT).show()
//                   typeAndCheckViewModel.setKeyBoardVisible(true)
                   // if more than 100 pixels, its probably a keyboard...
               }
           }
       }
   }



}