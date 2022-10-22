package com.korokoro.kina.ui.fragment.anki_frag_con

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.korokoro.kina.actions.changeViewVisibility
import com.korokoro.kina.actions.makeToast
import com.korokoro.kina.databinding.AnkiFlipFragBaseBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.ui.customClasses.*
import com.korokoro.kina.ui.fragment.base_frag_con.EditCardBaseFragDirections
import com.korokoro.kina.ui.viewmodel.*


class AnkiFlipBaseFrag  : Fragment(),View.OnClickListener {

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
    private lateinit var ankiNavCon:NavController
    private lateinit var flipNavCon:NavController
    var parentCountAnimation:ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = AnkiFlipFragBaseBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun setUpCL(){
            binding.apply {
                topBinding.apply {
                    arrayOf(

                        btnFlipItemList,
                        imvEditCard,
                        imvBack,
                        btnSetFlag,
                        btnRemembered,
                        btnFlipNext,
                        btnFlipPrevious,
                        btnAddCard,
                        btnStopCount,
                        confirmEndBinding.btnCloseConfirmEnd,
                        confirmEndBinding.btnCommitEnd,
                        confirmEndBinding.btnCancelEnd,
                        frameLayConfirmEnd).onEach {
                        it.setOnClickListener(
                            this@AnkiFlipBaseFrag)
                    }
                }

            }
        }
        fun setUpViewStart(){
            binding.progressBarBinding.apply {
                frameLayProgressbarRemembered.
                removeView(imvRememberedEndIcon)
            }
        }
        fun setUpLateInitVars(){
            val frag = childFragmentManager.findFragmentById(binding.fragConViewFlip.id) as NavHostFragment
            flipNavCon = frag.navController
            ankiNavCon= ankiBaseViewModel.returnAnkiBaseNavCon() ?:return
        }

        var start = true
        var cardBefore :Card? = null
        val cardIds = ankiBoxViewModel.returnAnkiBoxCardIds().distinct()
        val progressObserver = Observer<Progress>{
            binding.progressBarBinding.progressbarRemembered.progress = ((it.now/it.all.toDouble())*100 ).toInt()
        }
        val countDownAnimObserver = Observer<AnimationAttributes> {
            when(it){
                AnimationAttributes.StartAnim ->    {
                    parentCountAnimation?.cancel()
                    parentCountAnimation = getCountDownAnim()
                    parentCountAnimation?.start()
                }
                AnimationAttributes.EndAnim ->      parentCountAnimation?.end()
                AnimationAttributes.Pause ->        parentCountAnimation?.pause()
                AnimationAttributes.Resume ->       parentCountAnimation?.resume()
                AnimationAttributes.Cancel ->       {parentCountAnimation?.cancel()
                makeToast(requireActivity(),"called")
                }

                else ->  return@Observer
            }
        }
        val allCardsFromDBObserver = Observer<List<Card>>{
            if(cardIds.isEmpty())
                ankiFlipBaseViewModel.setAnkiFlipItems(it,ankiSettingPopUpViewModel.returnAnkiFilter())
        }
        val getCardsByMultipleCardIdsFromDBObserver = Observer<List<Card>> {
            if(cardIds.isEmpty().not()){
               ankiFlipBaseViewModel.setAnkiFlipItems(it,ankiSettingPopUpViewModel.returnAnkiFilter())
            }
        }
        val parentCardObserver = Observer<Card?> {
            val flipItems = ankiFlipBaseViewModel.returnFlipItems()
            if((cardBefore==it||it==null).not()) {
                if(it.id!=cardBefore?.id){
                    if(cardBefore!=null)
                        ankiFlipBaseViewModel.updateLookedTime(cardBefore!!,false)
                    ankiFlipBaseViewModel.updateLookedTime(it,true)
                    ankiFlipBaseViewModel.updateFlipped(it)
                }
                if(flipItems.contains(it)){
                    binding.topBinding.txvCardPosition.text ="${flipItems.indexOf(it)+1}/${flipItems.size}"
                }
                binding.btnRemembered.isSelected =  it.remembered
                binding.btnSetFlag.isSelected = it.flag
                cardBefore = it
                createCardViewModel.setStartingCardId(it.id)
                ankiFlipBaseViewModel.setParentPosition(flipItems.indexOf(it))
            }
            ankiFlipBaseViewModel.changeProgress(ankiSettingPopUpViewModel.returnReverseCardSide())

        }
        val flipItemsObserver = Observer<List<Card>> {
            if(it.isEmpty().not()) {
                flipNavCon.popBackStack()
                if(start){
                    val startingPosition = ankiFlipBaseViewModel.returnParentPosition()
                    ankiFlipBaseViewModel.startFlip(ankiSettingPopUpViewModel.returnReverseCardSide(),ankiSettingPopUpViewModel.returnTypeAnswer(),it,startingPosition)
                    start = false
                }
            } else return@Observer
        }
        val typeAnswerObserver = Observer<Boolean> {

            if(it){
                binding.btnFlipPrevious.visibility = View.INVISIBLE
            }
        }
        val autoFlipObserver = Observer<AutoFlip>{
            if(it.active){
                ankiFlipBaseViewModel.setCountDownAnim(AnimationAttributes.StartAnim)
            } else {
                binding.txvCountDown.visibility = View.GONE
                binding.btnStopCount.visibility = View.GONE
                ankiFlipBaseViewModel.apply {
                    if(parentCountAnimation!=null){
                        setCountDownAnim(AnimationAttributes.Cancel)
                    }

                }
            }
        }
        val keyBoardVisibilityObserver = Observer<Boolean>{ visible ->
            val views = arrayOf(binding.linLayFlipBottom,binding.btnRemembered,binding.btnSetFlag)
            views.onEach {
                changeViewVisibility(it,visible.not())
            }
        }

        setUpLateInitVars()
        setUpCL()
        setUpViewStart()
        ankiBaseViewModel.setActiveFragment(AnkiFragments.Flip)
        mainViewModel.setBnvVisibility(false)
        editFileViewModel.filterBottomMenuOnlyCard()

        ankiFlipBaseViewModel.setFlipBaseNavCon(flipNavCon)
        ankiFlipBaseViewModel.setFront(!ankiSettingPopUpViewModel.returnReverseCardSide())
        ankiBaseViewModel.setActiveFragment(AnkiFragments.Flip)
        ankiFlipBaseViewModel.setAutoFlipPaused(false)

        ankiFlipBaseViewModel.flipProgress.observe(viewLifecycleOwner,progressObserver)
        ankiFlipBaseViewModel.countDownAnim.observe(viewLifecycleOwner,countDownAnimObserver)
        ankiFlipBaseViewModel.getAllCardsFromDB.observe(viewLifecycleOwner,allCardsFromDBObserver)
        ankiFlipBaseViewModel.parentCard.observe(viewLifecycleOwner,parentCardObserver)
        ankiFlipBaseViewModel.ankiFlipItems.observe(viewLifecycleOwner,flipItemsObserver)
        ankiBoxViewModel.getCardsFromDBByMultipleCardIds(cardIds).observe(viewLifecycleOwner,getCardsByMultipleCardIdsFromDBObserver)
        ankiSettingPopUpViewModel.typeAnswer.observe(viewLifecycleOwner,typeAnswerObserver)
        ankiSettingPopUpViewModel.autoFlip.observe(viewLifecycleOwner,autoFlipObserver)

        flipTypeAndCheckViewModel.keyBoardVisible.observe(viewLifecycleOwner,keyBoardVisibilityObserver)





    }

    private fun getCountDownAnim(): ValueAnimator {
        val txv = binding.txvCountDown
        val btnStop = binding.btnStopCount
        val sec = ankiSettingPopUpViewModel.returnAutoFlip().seconds
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
                    NeighbourCardSide.NEXT,
                    ankiSettingPopUpViewModel.returnReverseCardSide(),
                    ankiSettingPopUpViewModel.returnTypeAnswer())
            }

        }
        return animation
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                changeViewVisibility(binding.frameLayConfirmEnd,true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        parentCountAnimation?.cancel()
        ankiFlipBaseViewModel.setCountDownAnim(AnimationAttributes.Cancel)
        _binding = null
    }

    override fun onClick(p0: View?) {
        binding.apply {
            topBinding.apply {
                when (p0) {
                    btnFlipItemList -> ankiNavCon.navigate(AnkiBoxContentFragDirections.toFlipItemRvFrag())
                    imvBack -> requireActivity().onBackPressed()
                    btnSetFlag -> {
                        p0.isSelected = !p0.isSelected
                        ankiFlipBaseViewModel.changeFlagStatus()
                    }
                    btnRemembered -> {
                        p0.isSelected = !p0.isSelected
                        ankiFlipBaseViewModel.changeRememberStatus()
                    }
                    btnFlipNext -> {
                        ankiSettingPopUpViewModel.apply {
                            ankiFlipBaseViewModel.flip(
                                NeighbourCardSide.NEXT,
                                returnReverseCardSide(),
                                returnTypeAnswer()
                            )
                        }

                    }
                    btnFlipPrevious -> {
                        ankiSettingPopUpViewModel.apply {
                            ankiFlipBaseViewModel.flip(
                                NeighbourCardSide.PREVIOUS,
                                returnReverseCardSide(),
                                returnTypeAnswer()
                            )
                        }
                    }
                    btnAddCard -> {
                        editFileViewModel.setBottomMenuVisible(true)
                    }
                    btnStopCount -> {
                        p0.isSelected = !p0.isSelected
                        if (p0.isSelected) ankiFlipBaseViewModel.setCountDownAnim(
                            AnimationAttributes.Pause
                        ) else ankiFlipBaseViewModel.setCountDownAnim(
                            AnimationAttributes.Resume
                        )
                        ankiFlipBaseViewModel.setAutoFlipPaused(p0.isSelected)
                    }
                    imvEditCard -> {
                        val editingId = ankiFlipBaseViewModel.returnParentCard()?.id ?: return
                        createCardViewModel.setStartingCardId(editingId)
                        mainViewModel.returnMainActivityNavCon()
                            ?.navigate(EditCardBaseFragDirections.openCreateCard())
                    }
                    confirmEndBinding.btnCancelEnd, confirmEndBinding.btnCloseConfirmEnd-> changeViewVisibility(binding.frameLayConfirmEnd,false)
                    confirmEndBinding.btnCommitEnd -> ankiNavCon.popBackStack()
                }
            }
        }
    }
}