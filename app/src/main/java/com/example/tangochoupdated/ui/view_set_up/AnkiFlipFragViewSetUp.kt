package com.example.tangochoupdated.ui.view_set_up
import android.animation.ValueAnimator
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.enumclass.*
import com.example.tangochoupdated.ui.listener.FlipBaseFragCL
import com.example.tangochoupdated.ui.viewmodel.*

class AnkiFlipFragViewSetUp(private val flipBaseBinding:AnkiFlipFragBaseBinding,
                            private val flipViewModel:AnkiFlipFragViewModel,
                            private val flipBaseFragmentActivity: FragmentActivity,
                            private val ankiBaseViewModel:AnkiFragBaseViewModel,
                            private val settingViewModel: AnkiSettingPopUpViewModel,
                            private val flipNavCon:NavController) {
    fun setUpViewStart(){
        flipBaseBinding.progressBarBinding.frameLayProgressbarRemembered.removeView(flipBaseBinding.progressBarBinding.imvRememberedEndIcon)
//        setUpCL()
    }
    fun getCountDownAnim(sec:Int, txv: TextView, btnStop: ImageView, navController: NavController): ValueAnimator {
        val animation = ValueAnimator.ofInt(sec)
        animation.apply {
            duration = (sec * 1000).toLong()
            doOnStart{
                txv.visibility = View.VISIBLE
                btnStop.isSelected =  false
                btnStop.visibility = View.VISIBLE
            }
            doOnCancel {
                it.removeAllListeners()
            }
            addUpdateListener {
                val a =(it.duration - it.currentPlayTime)/1000
                txv.text = a.toString()
            }
            doOnEnd {
                flipNavCon.navigate(flipViewModel.flipNext(settingViewModel.returnReverseCardSide(),settingViewModel.returnTypeAnswer()) ?:return@doOnEnd)
            }

        }
        return animation
    }
    fun observeProgress(progress: Progress){
        flipBaseBinding.progressBarBinding.progressbarRemembered.progress = ((progress.now/progress.all.toDouble())*100 ).toInt()

    }
    fun setUpCL(createFileViewModel: CreateFileViewModel,
                mainNavController: NavController,
                createCardViewModel: CreateCardViewModel,){
        flipBaseBinding.apply {
            topBinding.apply {
                arrayOf(

                    imvEditCard,
                    imvBack,
                    imvAnkiSetting,
                    imvAnkiSetting,
                    btnSetFlag,
                    btnRemembered,
                    btnFlipNext,
                    btnFlipPrevious,
                    btnAddCard,
                    btnStopCount,).onEach {
                        it.setOnClickListener(FlipBaseFragCL(
                            flipBaseBinding,
                            flipViewModel,
                            ankiBaseViewModel,
                            flipBaseFragmentActivity,
                            flipNavCon,
                            settingViewModel,
                            createFileViewModel,
                            createCardViewModel,
                            mainNavController))
                }
            }

        }
    }

    fun observeAutoFlip(autoFlip: AutoFlip,parentCountAnimation:ValueAnimator?){
        if(autoFlip.active){
            flipViewModel.setCountDownAnim(AnimationAttributes.StartAnim)
        } else {
            flipBaseBinding.txvCountDown.visibility = View.GONE
            flipBaseBinding.btnStopCount.visibility = View.GONE
            flipViewModel.apply {
                if(parentCountAnimation!=null){
                    setCountDownAnim(AnimationAttributes.Cancel)
                }

            }
        }
    }


}