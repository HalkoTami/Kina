package com.koronnu.kina.ui.tabAnki.ankiBox

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.R
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.source.local.entity.enumclass.FileStatus
import com.koronnu.kina.data.model.enumClasses.AnkiBoxFragments
import com.koronnu.kina.data.model.normalClasses.AnkiBoxTabData
import com.koronnu.kina.data.model.normalClasses.AnkiFilter
import com.koronnu.kina.data.model.enumClasses.AnkiFragments
import com.koronnu.kina.databinding.FragmentAnkiBoxBinding
import com.koronnu.kina.databinding.PgbAnkiBoxDataRememberedBinding
import com.koronnu.kina.ui.EditFileViewModel
import com.koronnu.kina.ui.MainViewModel
import com.koronnu.kina.util.CommonOb
import com.koronnu.kina.ui.tabAnki.AnkiBaseViewModel
import com.koronnu.kina.ui.tabAnki.AnkiSettingPopUpViewModel
import com.koronnu.kina.ui.tabAnki.flip.AnkiFlipBaseViewModel
import kotlin.math.abs

class AnkiBoxFrag  : Fragment(),View.OnClickListener {

    private var _binding: FragmentAnkiBoxBinding? = null
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiBaseViewModel by activityViewModels()
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()
    private val ankiFlipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    lateinit var ankiBoxNavCon:NavController
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fun setUpLateInitVars(){
            _binding =  FragmentAnkiBoxBinding.inflate(inflater, container, false)
            binding.viewModel = ankiBoxViewModel
            binding.lifecycleOwner = viewLifecycleOwner
            val frag = childFragmentManager.findFragmentById(binding.fcvFragmentAnkiBox.id) as NavHostFragment
            ankiBoxNavCon = frag.navController
        }
        fun changeSelectedTab(select: AnkiBoxFragments, before: AnkiBoxFragments?){
            fun getTextView(tab: AnkiBoxFragments): TextView {
                return when(tab){
                    AnkiBoxFragments.AllFlashCardCovers-> binding.txvTwgFragmentAnkiBoxTabAllFlashCardCover
                    AnkiBoxFragments.Library -> binding.txvTwgFragmentAnkiBoxTabLibrary
                    AnkiBoxFragments.Favourites -> binding.txvTwgFragmentAnkiBoxTabAnkiBoxFavourite
                }
            }
            getTextView(select).isSelected = true
            binding.llTwgFragmentAnkiBox.tag = select
            if(before!=null&&before!=select)
                getTextView(before).isSelected = false

        }
        fun addCL(){
            binding.apply {
                arrayOf(txvTwgFragmentAnkiBoxTabAnkiBoxFavourite,
                    txvTwgFragmentAnkiBoxTabLibrary,
                    txvTwgFragmentAnkiBoxTabAllFlashCardCover,
                    btnStartAnkiFromAnkiBox,
                    imvAddAnkiBoxContentToFavourite,
                    bindingTpbAnkiBox.btnSetting
                    ).onEach {
                    it.setOnClickListener(this@AnkiBoxFrag)
                }

            }


        }
        fun filterCards(filter: AnkiFilter, cards:List<Card>):List<Card>{
            val a =  cards
            if(filter.rememberedFilterActive){  a.filter { it.remembered == filter.remembered }
            }
            if(filter.answerTypedFilterActive){
                a.filter { it.lastTypedAnswerCorrect == filter.correctAnswerTyped }
            }
            return a
        }
        val checkFavouriteExistsList = mutableListOf<List<Card>>()
        val toastObserver = CommonOb().toastObserver(requireActivity())
        val ankiBoxChildFragObserver = Observer<AnkiBoxTabData>{
            changeSelectedTab(it.currentTab,it.before)
        }
        val descendantsCardIdsFromDBObserver = Observer<List<Int>> {
            ankiBoxViewModel.setAnkiBoxCardIds(it)
        }
        val getCardsFromDBByMultipleCardIdsObserver = Observer<List<Card>> {
            ankiBoxViewModel.setAnkiBoxItems(filterCards(ankiSettingPopUpViewModel.getAnkiFilter,it))
        }
        val ankiBoxFileIdsObserver = Observer<MutableList<Int>>{
            ankiBoxViewModel.getDescendantsCardIds(it).observe(viewLifecycleOwner,descendantsCardIdsFromDBObserver)
        }
        val ankiBoxCardIdsObserver = Observer<MutableList<Int>>{
            ankiBoxViewModel.getCardsFromDBByMultipleCardIds(it).observe(viewLifecycleOwner,getCardsFromDBByMultipleCardIdsObserver)
        }
        val eachFavouritesCardsObserver = Observer<List<Card>>{
            checkFavouriteExistsList.add(it)
        }
        val allFavouriteAnkiBoxFromDBObserver = Observer<List<File>>{
            it.onEach { file ->
                ankiBoxViewModel.getAnkiBoxRVCards(file.fileId).observe(viewLifecycleOwner,eachFavouritesCardsObserver)
            }
        }
        val ankiBoxItemsObserver = Observer<MutableList<Card>>{
            binding.imvAddAnkiBoxContentToFavourite.isSelected = checkFavouriteExistsList.contains(it)
            setUpAnkiBoxRing(it,binding.bindingPgbAnkiBoxDataRemembered)
            binding.btnStartAnkiFromAnkiBox.text =
                requireActivity().resources.getString(if(it.isEmpty()) R.string.btnStartAnki_withoutSelect else R.string.btnStartAnki_default)
        }




        setUpLateInitVars()
        addCL()

        ankiFlipBaseViewModel.setParentPosition(0)
//        ankiFlipBaseViewModel.setParentCard(null)
        ankiBaseViewModel.setActiveFragment(AnkiFragments.AnkiBox)
        ankiBoxViewModel.setAnkiBoxNavCon(ankiBoxNavCon)
        ankiFlipBaseViewModel.setAnkiFlipItems(mutableListOf(),)
        ankiBoxViewModel.toast.observe(viewLifecycleOwner,toastObserver)
        ankiBoxViewModel.currentChildFragment.observe(viewLifecycleOwner,ankiBoxChildFragObserver)
        ankiBoxViewModel.ankiBoxFileIds.observe(viewLifecycleOwner,ankiBoxFileIdsObserver)
        ankiBoxViewModel.ankiBoxCardIds.observe(viewLifecycleOwner,ankiBoxCardIdsObserver)
        ankiBoxViewModel.allFavouriteAnkiBoxFromDB.observe(viewLifecycleOwner,allFavouriteAnkiBoxFromDBObserver)
        ankiBoxViewModel.ankiBoxItems.observe(viewLifecycleOwner,ankiBoxItemsObserver)
        mainViewModel.setBnvVisibility(true)

        return binding.root
    }

    fun getRememberedPercentage(list: MutableList<Card>):Double{
        return if(list.size!=0) (list.filter { it.remembered == true }.size.toDouble()/list.size)
        else 0.0

    }

    fun moveViewInCircle(percentage:Double,
                         view: ImageView,
                         circleWidth: Int){
        val radious = Math.toRadians(360*percentage-90.0)
        val dx: Float = (circleWidth-view.layoutParams.width)/2 * Math.cos(radious).toFloat()
        val dy: Float = (circleWidth-view.layoutParams.height)/2 * Math.sin(radious).toFloat()
        view.translationX = dx
        view.translationY =dy
    }
    fun setUpAnkiBoxRing(list: MutableList<Card>, binding: PgbAnkiBoxDataRememberedBinding){
        val resources = binding.root.resources
        val rememberedPercentage = getRememberedPercentage(list)
        val rememberedCardsSize = list.filter { it.remembered == true }.size
        binding.apply {
            binding.imvRememberedEndIcon.visibility = View.VISIBLE
            txvInsideRing.text = resources.getString(R.string.remembered_progress,rememberedCardsSize,list.size)

        }
        val progressRingWidth = binding.ringProgressBar.layoutParams.width
        val progressBefore = binding.ringProgressBar.progress
        val beforePercentage = binding.ringProgressBar.progress.toFloat()/100
        val newProgress = rememberedPercentage.times(100).toInt()
        if(progressBefore==newProgress) {
            binding.ringProgressBar.progress = newProgress
            binding.imvRememberedEndIcon.visibility = if(newProgress!=0)  View.VISIBLE else View.INVISIBLE
            moveViewInCircle(rememberedPercentage,binding.imvRememberedEndIcon,progressRingWidth)
            return
        }
        fun getAnimation(percentageBefore:Float,newPercentage:Float): ValueAnimator {
            val percentageAnim = ValueAnimator.ofFloat(percentageBefore,newPercentage)
            percentageAnim.doOnEnd {
                binding.imvRememberedEndIcon.visibility = if(rememberedPercentage!=0.0)  View.VISIBLE else View.INVISIBLE
            }
            percentageAnim.addUpdateListener {
                val value = it.animatedValue as Float
                binding.ringProgressBar.progress = value.times(100).toInt()
                moveViewInCircle(value.toDouble(),binding.imvRememberedEndIcon,progressRingWidth)
            }
            percentageAnim.duration= abs(newProgress-progressBefore).times(30).toLong()
            return percentageAnim
        }
        getAnimation(beforePercentage,rememberedPercentage.toFloat()).start()

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                ankiBoxNavCon.popBackStack()
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

    override fun onClick(p0: View?) {
        binding.apply {
            when (p0) {

                imvAddAnkiBoxContentToFavourite -> {
                    if (imvAddAnkiBoxContentToFavourite.isSelected.not() && ankiBoxViewModel.returnAnkiBoxItems()
                            .isEmpty().not()
                    ) {
                        editFileViewModel.onClickCreateFile(FileStatus.ANKI_BOX_FAVOURITE)
                    } else return

                }
                bindingTpbAnkiBox.btnSetting -> ankiBaseViewModel.setSettingVisible(true)
                txvTwgFragmentAnkiBoxTabAllFlashCardCover -> ankiBoxViewModel.changeTab(
                    AnkiBoxFragments.AllFlashCardCovers)
                txvTwgFragmentAnkiBoxTabLibrary -> ankiBoxViewModel.changeTab(AnkiBoxFragments.Library)
                txvTwgFragmentAnkiBoxTabAnkiBoxFavourite -> ankiBoxViewModel.changeTab(
                    AnkiBoxFragments.Favourites)
            }
        }
    }
}