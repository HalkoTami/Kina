package com.example.tangochoupdated.ui.fragment.anki_frag_con

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.activity.MainActivity
import com.example.tangochoupdated.databinding.AnkiFlipFragBaseBinding
import com.example.tangochoupdated.db.enumclass.*
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringFragmentDirections
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringTypeAnswerFragment
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringTypeAnswerFragmentDirections
import com.example.tangochoupdated.ui.view_set_up.AnkiFlipFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.*
import java.time.Duration
import java.time.LocalDateTime


class AnkiFragFlipBaseFragment  : Fragment() {

    private var _binding: AnkiFlipFragBaseBinding? = null
    private val boxViewModel: AnkiBoxFragViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiFragBaseViewModel by activityViewModels()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()
    private val typeAndCheckViewModel: AnkiFlipTypeAndCheckViewModel by activityViewModels()
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val  createCardViewModel: CreateCardViewModel by activityViewModels()
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

        viewSetUp.setUpCL(createFileViewModel,baseViewModel.returnMainActivityNavCon() ?:return, createCardViewModel)

        viewSetUp.setUpViewStart()
        baseViewModel.setBnvVisibility(false)
        typeAndCheckViewModel.keyBoardVisible.observe(viewLifecycleOwner){ visible ->
            val views = arrayOf(binding.linLayFlipBottom,binding.btnRemembered,binding.btnSetFlag)
            views.onEach {
                it.visibility = if (visible) View.GONE else View.VISIBLE
            }

        }
        createFileViewModel.filterBottomMenuOnlyCard()



        var start:Boolean = true
        ankiBaseViewModel.setActiveFragment(AnkiFragments.Flip)
        flipBaseViewModel.apply {
            setParentPosition(0)

                getAllCardsFromDB.observe(viewLifecycleOwner){
                    if(boxViewModel.returnAnkiBoxItems().isNullOrEmpty()){
                        setAnkiFlipItems(it)
                    }
                    if(start){
                        navCon.navigate(getStart(settingVM.returnReverseCardSide(),settingVM.returnTypeAnswer()))
                        start = false
                    } else return@observe
                }

            setFront(
                !settingVM.returnReverseCardSide()
            )
            if(boxViewModel.returnAnkiBoxItems().isNullOrEmpty()){
                getAllCardsFromDB.observe(viewLifecycleOwner){
                    setAnkiFlipItems(it)
                }
            }

            settingVM.typeAnswer.observe(viewLifecycleOwner){
                if(it){
                    binding.btnFlipPrevious.visibility = View.INVISIBLE
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
                            if(duration>1){
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


            var parentCountAnimation:ValueAnimator? = null
            countDownAnim.observe(viewLifecycleOwner){
                when(it){
                    AnimationAttributes.StartAnim ->    {
                        parentCountAnimation?.cancel()
                        parentCountAnimation = viewSetUp.getCountDownAnim(settingVM.returnAutoFlip().seconds,binding.txvCountDown,binding.btnStopCount, navCon)
                        parentCountAnimation?.start()
                    }
                    AnimationAttributes.EndAnim ->      parentCountAnimation?.end()
                    AnimationAttributes.Pause ->        parentCountAnimation?.pause()
                    AnimationAttributes.Resume ->       parentCountAnimation?.resume()
                    AnimationAttributes.Cancel ->       parentCountAnimation?.cancel()
                    else ->  return@observe
                }

            }
            flipProgress.observe(viewLifecycleOwner){
                viewSetUp.observeProgress(it)
            }

                settingVM.autoFlip.observe(viewLifecycleOwner){
                   viewSetUp.observeAutoFlip(it,parentCountAnimation)
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
        flipBaseViewModel.setCountDownAnim(AnimationAttributes.Cancel)
        _binding = null
    }
}