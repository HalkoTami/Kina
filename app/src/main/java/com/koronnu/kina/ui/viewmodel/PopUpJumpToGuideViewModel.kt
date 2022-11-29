package com.koronnu.kina.ui.viewmodel

import android.animation.Animator
import android.animation.AnimatorInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.koronnu.kina.R

class PopUpJumpToGuideViewModel(val libraryBaseViewModel: LibraryBaseViewModel):ViewModel() {




    private val _popUpVisible = MutableLiveData<Boolean>()
    private fun setPopUpVisible(visible:Boolean){
        _popUpVisible.value = visible
    }
    val popUpVisible :LiveData<Boolean> = _popUpVisible
    private var _popUpTextId:Int? = null
    private val popUpTextId get() = _popUpTextId!!
    fun setPopUpTextId(int: Int){
        _popUpTextId = int
    }
    fun getPopUpVisibilityObserver(popUpTextView: TextView,frameLayout: FrameLayout) = Observer<Boolean>{ visible->
        val context = popUpTextView.context
        fun doAfterSetPopUpVisibilityTrue(){
            val text = context.getString(popUpTextId)
            popUpTextView.text = text
        }
        fun getVisibilityAnim():Animator{
            fun getAnim(animId:Int): Animator =  AnimatorInflater.loadAnimator(context,animId)
            return when(visible){
                true -> getAnim(R.animator.slide_in_left_anim)
                false -> getAnim(R.animator.slide_out_right_anim)
            }
        }
        if(visible) doAfterSetPopUpVisibilityTrue()
        getVisibilityAnim().apply {
            setTarget(frameLayout)
            start()
        }
    }



}