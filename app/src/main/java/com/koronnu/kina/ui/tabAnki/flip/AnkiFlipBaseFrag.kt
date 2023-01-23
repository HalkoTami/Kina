package com.koronnu.kina.ui.tabAnki.flip

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.R
import com.koronnu.kina.util.DateTimeActions
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.makeToast
import com.koronnu.kina.data.model.enumClasses.AnkiFragments
import com.koronnu.kina.data.model.enumClasses.NeighbourCardSide
import com.koronnu.kina.data.model.normalClasses.Progress
import com.koronnu.kina.databinding.FragmentAnkiFlipBaseBinding
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.ui.MainViewModel
import com.koronnu.kina.ui.tabAnki.AnkiBaseViewModel
import com.koronnu.kina.ui.tabAnki.AnkiSettingPopUpViewModel
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxViewModel
import java.util.Date


class AnkiFlipBaseFrag  : Fragment() {

    private var _binding: FragmentAnkiFlipBaseBinding? = null
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiBaseViewModel by activityViewModels()
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()
    private val ankiFlipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val flipTypeAndCheckViewModel: FlipTypeAndCheckViewModel by viewModels { FlipTypeAndCheckViewModel.Factory }
    private val binding get() = _binding!!
    private lateinit var flipNavCon:NavController
    private lateinit var flipRoundSharedPref:SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAnkiFlipBaseBinding.inflate(inflater, container, false)
        binding.viewModel = ankiFlipBaseViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun setUpViewStart(){
            binding.progressBarBinding.apply {
                frameLayProgressbarRemembered.
                removeView(imvRememberedEndIcon)
            }
        }
        fun setUpLateInitVars(){
            val frag = childFragmentManager.findFragmentById(binding.fragConViewFlip.id) as NavHostFragment
            flipNavCon = frag.navController
            flipRoundSharedPref = requireActivity().getSharedPreferences(resources.getString(R.string.sp_title_flipRound),Context.MODE_PRIVATE)
        }

        var start = true
        val roundStart = arrayOf(AnkiFragments.AnkiBox, AnkiFragments.FlipCompleted).contains(ankiBaseViewModel.returnActiveFragment())
        val cardIds = ankiBoxViewModel.returnAnkiBoxCardIds().distinct()
        val progressObserver = Observer<Progress>{
            binding.progressBarBinding.progressbarRemembered.progress = ((it.now/it.all.toDouble())*100 ).toInt()
        }
        val flipItemsObserver = Observer<List<Card>> {
            if(it.isEmpty()) return@Observer
            flipNavCon.popBackStack()
            if(start){
                val startingPosition = ankiFlipBaseViewModel.returnParentPosition()
                ankiFlipBaseViewModel.startFlip(ankiSettingPopUpViewModel.getReverseCardSideActive,
                    ankiSettingPopUpViewModel.getTypeAnswer,it,startingPosition,
                    roundStart.not()
                )
                start = false
            }
        }
        val typeAnswerObserver = Observer<Boolean> {

            if(it){
                binding.btnFlipPrevious.visibility = View.INVISIBLE
            }
        }
        val keyBoardVisibilityObserver = Observer<Boolean>{ visible ->
            val views = arrayOf(binding.linLayFlipBottom,binding.btnRemembered)
            views.onEach {
                changeViewVisibility(it,visible.not())
            }
        }

        setUpLateInitVars()
        setUpViewStart()
        ankiBaseViewModel.setActiveFragment(AnkiFragments.Flip)
        mainViewModel.setBnvVisibility(false)

        ankiFlipBaseViewModel.loadingNewCountDownAnim.observe(viewLifecycleOwner){
            if(it){
                ankiFlipBaseViewModel.setCountDownAnim(getCountDownAnim())
                ankiFlipBaseViewModel.setLoadingCountDownAnim(false)
            }
        }
        ankiFlipBaseViewModel.setFlipBaseNavCon(flipNavCon)
        ankiFlipBaseViewModel.setFront(!ankiSettingPopUpViewModel.getReverseCardSideActive)
        ankiBaseViewModel.setActiveFragment(AnkiFragments.Flip)
        ankiFlipBaseViewModel.setAutoFlipRunning(false)

        ankiFlipBaseViewModel.flipProgress.observe(viewLifecycleOwner,progressObserver)
        ankiFlipBaseViewModel.ankiFlipItems.observe(viewLifecycleOwner,flipItemsObserver)
        ankiSettingPopUpViewModel.typeAnswer.observe(viewLifecycleOwner,typeAnswerObserver)
        flipTypeAndCheckViewModel.keyBoardVisible.observe(viewLifecycleOwner,keyBoardVisibilityObserver)

        ankiFlipBaseViewModel.getAnkiFlipItems(cardIds,ankiSettingPopUpViewModel.getAnkiFilter.rememberedFilterActive).observe(viewLifecycleOwner){
            if(start){
                if(it.isEmpty()){
                    ankiBaseViewModel.getAnkiBaseNavCon.popBackStack()
                    makeToast(requireContext(),"アイテムがありません")
                    return@observe
                }
                ankiFlipBaseViewModel.setAnkiFlipItems(it)
            }
        }




    }

    private var pausedTime:Date? = null
    override fun onPause() {
        super.onPause()
        pausedTime = Date()

    }

    override fun onResume() {
        super.onResume()
        ankiFlipBaseViewModel.addFlipLeavedTimeInSec(
            DateTimeActions().getTimeDifference(
                Date(),pausedTime?:return, DateTimeActions.TimeUnit.SECONDS
            ))
    }


    private fun getCountDownAnim(): ValueAnimator {
        val txv = binding.txvCountDown
        val btnStop = binding.btnStopCount
        val sec = ankiSettingPopUpViewModel.getAutoFlip.seconds
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
                ankiFlipBaseViewModel.flip(
                    NeighbourCardSide.NEXT)
            }

        }
        return animation
    }


    override fun onDestroyView() {
        super.onDestroyView()
        ankiFlipBaseViewModel.getCountDownAnim?.cancel()
        _binding = null
    }


}