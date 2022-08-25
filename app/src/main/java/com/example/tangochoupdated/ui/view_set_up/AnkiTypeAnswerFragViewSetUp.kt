package com.example.tangochoupdated.ui.view_set_up
import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.enumclass.*
import com.example.tangochoupdated.ui.listener.FlipBaseFragCL
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipTypeAndCheckViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel

class AnkiTypeAnswerFragViewSetUp(val typeAndCheckViewModel: AnkiFlipTypeAndCheckViewModel) {
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