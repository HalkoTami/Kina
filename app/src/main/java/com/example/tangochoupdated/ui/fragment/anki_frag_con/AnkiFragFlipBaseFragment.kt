package com.example.tangochoupdated.ui.fragment.anki_frag_con

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnPause
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFlipFragBaseBinding
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.*
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringFragment
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringFragmentDirections
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringTypeAnswerFragmentDirections
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.view_set_up.AnkiFlipFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


class AnkiFragFlipBaseFragment  : Fragment() {

    private var _binding: AnkiFlipFragBaseBinding? = null
    private val boxViewModel: AnkiBoxFragViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiFragBaseViewModel by activityViewModels()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  AnkiFlipFragBaseBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frag = childFragmentManager.findFragmentById(binding.fragConViewFlip.id) as NavHostFragment
        val navCon = frag.navController
        val viewSetUp = AnkiFlipFragViewSetUp(binding,flipBaseViewModel,requireActivity(),ankiBaseViewModel,settingVM,navCon)


        viewSetUp.setUpViewStart()
        baseViewModel.setBnvVisibility(false)

        ankiBaseViewModel.setActiveFragment(AnkiFragments.Flip)
        flipBaseViewModel.apply {
            setParentPosition(0)
//            val action = FlipStringFragmentDirections.toFlipString()
//            navCon.navigate(action)
            setFront(
                !settingVM.returnReverseCardSide()
            )
            if(boxViewModel.returnAnkiBoxItems().isNullOrEmpty()){
                getAllCardsFromDB.observe(viewLifecycleOwner){
                    setAnkiFlipItems(it)
                }
            }




            var start :LocalDateTime? = null
            countFlip.observe(viewLifecycleOwner){
                if(it.flipSaved) Toast.makeText(requireActivity(),"flip saved",Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when(it.count) {
                        Count.Start -> start = LocalDateTime.now()
                        Count.End -> {
                            val duration = Duration.between(start ?:return@observe,LocalDateTime.now()).seconds
                            if(duration>5){
                                updateFlipped(it.countingCard)
                                Toast.makeText(requireActivity(),"flip saved",Toast.LENGTH_SHORT).show()
                            }
                        }
                        null -> return@observe
                    }
                }
            }
            parentCard.observe(viewLifecycleOwner){
                binding.btnRemembered.isSelected =  it.remembered ?:false
                binding.btnSetFlag.isSelected = it.flag
                binding.topBinding.txvCardPosition.text = "id:${it.id} position:${returnParentPosition()+1}/ size:${returnFlipItems().size}"
            }
            val textview = binding.txvCountDown
            fun getCountDownAnim(sec:Int,txv:TextView,btnStop:ImageView,navController: NavController):ValueAnimator{
                val animation = ValueAnimator.ofInt(sec)
                animation.apply {
                    duration = (sec * 1000).toLong()
                    doOnStart{
                        binding.txvCountDown.visibility = View.VISIBLE
                        btnStop.isSelected =  false
                        binding.btnStopCount.visibility = View.VISIBLE
                    }
//                    doOnPause {
//                        binding.btnStopCount.isSelected = true
//                    }
                    doOnCancel {
                        it.removeAllListeners()
                    }
                    addUpdateListener {
                        val a =(it.duration - it.currentPlayTime)/1000
                        txv.text = a.toString()
                    }
                    doOnEnd {
                        navController.navigate(flipNext(settingVM.returnReverseCardSide(),settingVM.returnTypeAnswer()) ?:return@doOnEnd)
                    }

                }
                return animation
            }

            var parentCountAnimation:ValueAnimator? = null
            countDownAnim.observe(viewLifecycleOwner){
                when(it.attributes){
                    AnimationAttributes.StartAnim ->    {
                        parentCountAnimation?.cancel()
                        parentCountAnimation = getCountDownAnim(settingVM.returnAutoFlip().seconds,binding.txvCountDown,binding.btnStopCount, navCon)
                        parentCountAnimation?.start()
                    }
                    AnimationAttributes.EndAnim ->      parentCountAnimation?.end()
                    AnimationAttributes.Pause ->        parentCountAnimation?.pause()
                    AnimationAttributes.Resume ->       parentCountAnimation?.resume()
                    AnimationAttributes.Cancel ->       parentCountAnimation?.cancel()
                    else ->  return@observe
                }

            }

            flipAction.observe(viewLifecycleOwner){
                viewSetUp.applyProgress(returnParentPosition(),
                    returnFlipItems().size,
                    (it == FlipAction.LookStringFront||it == FlipAction.TypeAnswerString),
                    settingVM.returnReverseCardSide())
                if(settingVM.returnAutoFlip().active){
//                    val sec = settingVM.returnAutoFlip().seconds
//                    controlCountDownAnim(AnimationAttributes.Cancel)
                    setCountDownAnim(AnimationController(AnimationAttributes.StartAnim))

                }
            }
            settingVM.autoFlip.observe(viewLifecycleOwner){
                if(it.active){
                    if(parentCountAnimation == null) {
                        setCountDownAnim(AnimationController(AnimationAttributes.StartAnim))
                    }


                } else {
                    flipBaseViewModel.apply {
                        if(parentCountAnimation!=null){
                            controlCountDownAnim(AnimationAttributes.Cancel)
                        }

                    }
                }
            }



        }



    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                val navCon = requireActivity().findViewById<FragmentContainerView>(R.id.anki_frag_container_view).findNavController()
                navCon.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        baseViewModel.setBnvVisibility(true)
        flipBaseViewModel.controlCountDownAnim(AnimationAttributes.EndAnim)
        _binding = null
    }
}