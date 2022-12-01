package com.koronnu.kina.ui.viewmodel

import android.animation.ValueAnimator
import android.view.View
import androidx.lifecycle.*
import com.koronnu.kina.actions.makeToast
import com.koronnu.kina.actions.setClickListeners
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.databinding.PopupJumpToGuideBinding
import com.koronnu.kina.ui.animation.Animation
import com.koronnu.kina.ui.listener.libraryBaseFragment.PopUpJumpToGuideCL

class PopUpJumpToGuideViewModel:ViewModel() {

    private lateinit var mainViewModel: MainViewModel
    private val frameLayout get () = mainViewModel.mainActivityBinding.frameLayLibraryChildFragBaseJumpToGuidePopup
    val popupJumpToGuideBinding get() =mainViewModel.mainActivityBinding.bindingPopupJumpToGuide
    private val popUpTextView get() = popupJumpToGuideBinding.txvPopUpJumpToGuide
    companion object{
        fun getViewModelFactory(mainVM: MainViewModel): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val viewModel = PopUpJumpToGuideViewModel()
                viewModel.mainViewModel = mainVM
                return viewModel as T
            }

        }
    }

    private val getPopUpJumpToGuideBindingClickableViews:Array<View> get() {
        val binding = popupJumpToGuideBinding
        return arrayOf(binding.imvPopUpJumpToGuideClose,
            binding.conLayPopUpJumpToGuideContent)
    }
    fun setPopUpJumpToGuideCL(){
        setClickListeners(getPopUpJumpToGuideBindingClickableViews, PopUpJumpToGuideCL(this))
    }



    private val _popUpVisible = MutableLiveData<Boolean>()
    fun setPopUpVisible(visible:Boolean){
        _popUpVisible.value = visible
    }
    private val popUpVisible :LiveData<Boolean> = _popUpVisible
    private var _popUpTextId:Int? = null
    private val popUpTextId get() = _popUpTextId!!
    fun setPopUpTextId(int: Int){
        _popUpTextId = int
    }
    private val popUpVisibilityObserver = Observer<Boolean>{ visible->
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

    fun onClickImvPopUpJumpToGuideClose(){
        setPopUpVisible(false)
    }


    fun onClickConLayPopUpJumpToGuideContent(){
        TODO()
    }



}