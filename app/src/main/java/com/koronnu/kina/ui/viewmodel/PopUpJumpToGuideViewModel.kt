package com.koronnu.kina.ui.viewmodel

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.koronnu.kina.R
import com.koronnu.kina.actions.makeToast
import com.koronnu.kina.ui.animation.Animation

class PopUpJumpToGuideViewModel:ViewModel() {


    private lateinit var libraryBaseViewModel:LibraryBaseViewModel
    fun setLateInitVars(libraryBaseVM: LibraryBaseViewModel){
        libraryBaseViewModel = libraryBaseVM
    }


    private val _popUpVisible = MutableLiveData<Boolean>()
    fun setPopUpVisible(visible:Boolean){
        _popUpVisible.value = visible
    }
    val popUpVisible :LiveData<Boolean> = _popUpVisible
    private var _popUpTextId:Int? = null
    private val popUpTextId get() = _popUpTextId!!
    fun setPopUpTextId(int: Int){
        _popUpTextId = int
    }
    private val popUpVisibilityObserver = Observer<Boolean>{ visible->
        val popUpTextView = libraryBaseViewModel.getChildFragBinding.bindingPopupJumpToGuide.txvPopUpJumpToGuide
        val frameLayout = libraryBaseViewModel.getChildFragBinding.frameLayLibraryChildFragBaseJumpToGuidePopup
        val context = popUpTextView.context
        makeToast(context,visible.toString())
        fun doAfterSetPopUpVisibilityTrue(){
            val text = context.getString(popUpTextId)
            popUpTextView.text = text
        }
        fun getVisibilityAnim():ValueAnimator{
            return when(visible){
                true -> Animation().slideInRightAnim(frameLayout).apply {
                    startDelay = 2000
                }
                false -> Animation().slideOutRightAnim(frameLayout)
            }
        }
        if(visible) doAfterSetPopUpVisibilityTrue()
        getVisibilityAnim().apply {
            duration = 1000
            start()
        }

    }
    fun observePopUpVisibility(lifecycleOwner: LifecycleOwner){
        popUpVisible.observe(lifecycleOwner,popUpVisibilityObserver)
    }



}