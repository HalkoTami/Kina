package com.koronnu.kina.ui.fragment.anki_frag_con

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.koronnu.kina.R
import com.koronnu.kina.actions.DateTimeActions
import com.koronnu.kina.actions.NavigationActions
import com.koronnu.kina.db.enumclass.ActivityStatus
import com.koronnu.kina.ui.animation.Animation
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.databinding.FragmentAnkiFlipCompletedBinding
import com.koronnu.kina.ui.viewmodel.AnkiBaseViewModel
import com.koronnu.kina.ui.viewmodel.AnkiFlipBaseViewModel
import java.util.*

class AnkiFlipCompleteFrag:Fragment(),View.OnClickListener {
    private var _binding: FragmentAnkiFlipCompletedBinding? = null
    private val binding get() = _binding!!
    private val ankiBaseViewModel: AnkiBaseViewModel by activityViewModels()
    private val ankiFlipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private lateinit var flipRoundSharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentAnkiFlipCompletedBinding.inflate(inflater, container, false)
        Animation().appearAlphaAnimation(binding.root,true,1f){}.start()
        flipRoundSharedPref = requireActivity().getSharedPreferences(resources.getString(R.string.sp_title_flipRound),Context.MODE_PRIVATE)

        ankiFlipBaseViewModel.lastFlipDurationInMin.observe(viewLifecycleOwner){
            binding.txvFlippedMinutes.text = it.toString()
        }
        ankiFlipBaseViewModel.allActivityData.observe(viewLifecycleOwner){
//            val dTA = DateTimeActions()
//            val startedDate =dTA.
//            fromStringToDate(it.last { it.activityStatus == ActivityStatus.FLIP_ROUND_STARTED }.dateTime)?:return@observe
//            val diffInMin = dTA.getTimeDifference( Date(),startedDate,DateTimeActions.TimeUnit.MINUTES)
//            val finalResult = diffInMin - ankiFlipBaseViewModel.returnFlipLeavedTimeInSec()/60
//            binding.txvFlippedMinutes.text = finalResult.toString()

        }
        binding.txvFlippedItemsAmount.text = ankiFlipBaseViewModel.returnFlipItems().size.toString()
        binding.txvNewlyRememberedCardAmount.text =
            ankiFlipBaseViewModel.getNewlyRememberedCardAmount().toString()
        binding.apply {
            arrayOf(llBtnEndFlip,
                flBtnFlipStartAgain).onEach {
                it.setOnClickListener(this@AnkiFlipCompleteFrag)
            }
        }
        ankiBaseViewModel.setActiveFragment(AnkiFragments.FlipCompleted)
        return binding.root
    }
    private fun navigateToAnkiBox(){
        val navCon = ankiBaseViewModel.getAnkiBaseNavCon
        NavigationActions().popBackStackToLabel(navCon ,
            requireActivity().getString(R.string.nav_label_anki_ankiBox))
    }
    private fun navigateToFlipStart(){
        val navCon = ankiBaseViewModel.getAnkiBaseNavCon
        NavigationActions().popBackStackToLabel(navCon,
        requireActivity().getString(R.string.nav_label_anki_flip))
    }

    private fun onClickEndFlip(){
        Animation().appearAlphaAnimation(binding.root,false,1f) {
            navigateToAnkiBox()
        }.start()
    }
    private fun onClickStartAgain(){
        ankiFlipBaseViewModel.setParentPosition(0)
//        ankiFlipBaseViewModel.setParentCard(null)
        Animation().appearAlphaAnimation(binding.root,false,1f){
            navigateToFlipStart()
        }.start()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                onClickEndFlip()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                llBtnEndFlip -> onClickEndFlip()
                flBtnFlipStartAgain -> onClickStartAgain()

            }
        }

    }
}