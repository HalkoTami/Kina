package com.korokoro.kina.ui.fragment.anki_frag_con

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.korokoro.kina.R
import com.korokoro.kina.actions.makeToast
import com.korokoro.kina.databinding.AnkiFlipFragBaseBinding
import com.korokoro.kina.ui.view_set_up.AnkiFlipFragViewSetUp
import com.korokoro.kina.ui.viewmodel.*
import com.korokoro.kina.ui.viewmodel.customClasses.AnimationAttributes
import com.korokoro.kina.ui.viewmodel.customClasses.AnkiFragments
import com.korokoro.kina.ui.viewmodel.customClasses.Count
import java.time.Duration
import java.time.LocalDateTime


class AnkiFlipBaseFrag  : Fragment() {

    private var _binding: AnkiFlipFragBaseBinding? = null
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiBaseViewModel by activityViewModels()
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()
    private val ankiFlipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val flipTypeAndCheckViewModel: FlipTypeAndCheckViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
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
        val viewSetUp = AnkiFlipFragViewSetUp(binding,ankiFlipBaseViewModel,requireActivity())
        val ankiNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.anki_frag_container_view).findNavController()
        viewSetUp.setUpCL(
            editFileViewModel,
            mainViewModel.returnMainActivityNavCon() ?:return,
            ankiNavCon,
            createCardViewModel,
            ankiSettingPopUpViewModel,
            ankiBaseViewModel,
            navCon,)

        viewSetUp.setUpViewStart(
            binding
        )
        ankiBaseViewModel.setActiveFragment(AnkiFragments.Flip)
        mainViewModel.setBnvVisibility(false)
        flipTypeAndCheckViewModel.keyBoardVisible.observe(viewLifecycleOwner){ visible ->
            val views = arrayOf(binding.linLayFlipBottom,binding.btnRemembered,binding.btnSetFlag)
            views.onEach {
                it.visibility = if (visible) View.GONE else View.VISIBLE
            }

        }
        editFileViewModel.filterBottomMenuOnlyCard()



        var start:Boolean = true
        ankiBaseViewModel.setActiveFragment(AnkiFragments.Flip)
        ankiFlipBaseViewModel.apply {
            setParentPosition(0)
            setFront(
                !ankiSettingPopUpViewModel.returnReverseCardSide()
            )
            val cardIds = ankiBoxViewModel.returnAnkiBoxCardIds().distinct()
            makeToast(requireActivity(),cardIds.size.toString())
            getAllCardsFromDB.observe(viewLifecycleOwner){
                if(cardIds.isEmpty())
                setAnkiFlipItems(it,ankiSettingPopUpViewModel.returnAnkiFilter())
            }
            ankiBoxViewModel.getCardsFromDBByMultipleCardIds(cardIds) .observe(viewLifecycleOwner){
                if(cardIds.isEmpty().not()){
                    setAnkiFlipItems(it,ankiSettingPopUpViewModel.returnAnkiFilter())
                }
            }
            ankiFlipItems.observe(viewLifecycleOwner){
                if(it.isEmpty()) makeToast(requireActivity(),"カードがありません")
                navCon.popBackStack()
                if(start){
                    navCon.navigate(getStart(ankiSettingPopUpViewModel.returnReverseCardSide(),ankiSettingPopUpViewModel.returnTypeAnswer(),it))
                    start = false
                } else return@observe
            }

            ankiSettingPopUpViewModel.typeAnswer.observe(viewLifecycleOwner){
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
                ankiFlipBaseViewModel.updateLooked(it)
                if(returnFlipItems().contains(it)){
                    binding.topBinding.txvCardPosition.text ="${returnFlipItems().indexOf(it)}/${returnFlipItems().size}"
                }

                binding.btnRemembered.isSelected =  it.remembered ?:false
                binding.btnSetFlag.isSelected = it.flag

            }
            val textview = binding.txvCountDown


            var parentCountAnimation:ValueAnimator? = null
            countDownAnim.observe(viewLifecycleOwner){
                when(it){
                    AnimationAttributes.StartAnim ->    {
                        parentCountAnimation?.cancel()
                        parentCountAnimation = viewSetUp.getCountDownAnim(ankiSettingPopUpViewModel.returnAutoFlip().seconds,binding.txvCountDown,binding.btnStopCount, navCon,ankiSettingPopUpViewModel.returnReverseCardSide(),ankiSettingPopUpViewModel.returnTypeAnswer())
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

                ankiSettingPopUpViewModel.autoFlip.observe(viewLifecycleOwner){
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
        mainViewModel.setBnvVisibility(true)
        ankiFlipBaseViewModel.setCountDownAnim(AnimationAttributes.Cancel)
        _binding = null
    }
}