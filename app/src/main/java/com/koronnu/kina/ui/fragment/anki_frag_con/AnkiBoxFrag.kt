package com.koronnu.kina.ui.fragment.anki_frag_con

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.AnkiHomeFragBaseBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.customClasses.AnkiBoxFragments
import com.koronnu.kina.customClasses.AnkiBoxTabData
import com.koronnu.kina.customClasses.AnkiFilter
import com.koronnu.kina.customClasses.AnkiFragments
import com.koronnu.kina.ui.observer.CommonOb
import com.koronnu.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.koronnu.kina.ui.viewmodel.*

class AnkiBoxFrag  : Fragment(),View.OnClickListener {

    private var _binding: AnkiHomeFragBaseBinding? = null
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()
    private val ankiBaseViewModel:AnkiBaseViewModel by activityViewModels()
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
            _binding =  AnkiHomeFragBaseBinding.inflate(inflater, container, false)
            val frag = childFragmentManager.findFragmentById(binding.fragConAnkiBoxTab.id) as NavHostFragment
            ankiBoxNavCon = frag.navController
        }
        fun changeSelectedTab(select: AnkiBoxFragments, before: AnkiBoxFragments?){
            fun getTextView(tab: AnkiBoxFragments): TextView {
                return when(tab){
                    AnkiBoxFragments.AllFlashCardCovers-> binding.tabAllFlashCardCoverToAnkiBox
                    AnkiBoxFragments.Library -> binding.tabLibraryToAnkiBox
                    AnkiBoxFragments.Favourites -> binding.tabFavouritesToAnkiBox
                }
            }
            getTextView(select).isSelected = true
            binding.linLayTabChange.tag = select
            if(before!=null&&before!=select)
                getTextView(before).isSelected = false

        }
        fun addCL(){
            binding.apply {
                arrayOf(tabFavouritesToAnkiBox,
                    tabLibraryToAnkiBox,
                    tabAllFlashCardCoverToAnkiBox,
                    btnStartAnki,btnAddToFavouriteAnkiBox,
                    topBarBinding.btnSetting
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
        val viewSetUp = AnkiBoxFragViewSetUp()
        val checkFavouriteExistsList = mutableListOf<List<Card>>()
        val toastObserver = CommonOb().toastObserver(requireActivity())
        val ankiBoxChildFragObserver = Observer<AnkiBoxTabData>{
            changeSelectedTab(it.currentTab,it.before)
        }
        val descendantsCardIdsFromDBObserver = Observer<List<Int>> {
            ankiBoxViewModel.setAnkiBoxCardIds(it)
        }
        val getCardsFromDBByMultipleCardIdsObserver = Observer<List<Card>> {
            ankiBoxViewModel.setAnkiBoxItems(filterCards(ankiSettingPopUpViewModel.returnAnkiFilter(),it))
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
            binding.btnAddToFavouriteAnkiBox.isSelected = checkFavouriteExistsList.contains(it)
            editFileViewModel.setAnkiBoxCards(it)
            viewSetUp.setUpAnkiBoxRing(it,binding.ringBinding)
            binding.btnStartAnki.text = if(it.isEmpty()) "カードを選ばず暗記" else "暗記開始"
        }
        val allCardsObserver = Observer<List<Card>> {
             arrayOf(
                binding.btnStartAnki,binding.topBarBinding.btnSetting
            ).onEach { view -> changeViewVisibility(view,
                 it.isNotEmpty()
             ) }
        }




        setUpLateInitVars()
        addCL()

        ankiFlipBaseViewModel.setParentPosition(0)
        ankiFlipBaseViewModel.setParentCard(null)
        ankiBaseViewModel.setActiveFragment(AnkiFragments.AnkiBox)
        ankiFlipBaseViewModel.getAllCardsFromDB.observe(viewLifecycleOwner,allCardsObserver)
        ankiBoxViewModel.setAnkiBoxNavCon(ankiBoxNavCon)
        ankiFlipBaseViewModel.setAnkiFlipItems(mutableListOf(), AnkiFilter())
        ankiBoxViewModel.toast.observe(viewLifecycleOwner,toastObserver)
        ankiBoxViewModel.currentChildFragment.observe(viewLifecycleOwner,ankiBoxChildFragObserver)
        ankiBoxViewModel.ankiBoxFileIds.observe(viewLifecycleOwner,ankiBoxFileIdsObserver)
        ankiBoxViewModel.ankiBoxCardIds.observe(viewLifecycleOwner,ankiBoxCardIdsObserver)
        ankiBoxViewModel.allFavouriteAnkiBoxFromDB.observe(viewLifecycleOwner,allFavouriteAnkiBoxFromDBObserver)
        ankiBoxViewModel.ankiBoxItems.observe(viewLifecycleOwner,ankiBoxItemsObserver)
        mainViewModel.setBnvVisibility(true)

        return binding.root
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
                btnStartAnki -> {
                    ankiBoxViewModel.apply {
                        if (returnAnkiBoxItems().isEmpty()) ankiBoxViewModel.setModeCardsNotSelected(
                            true
                        )
                    }
                    ankiBaseViewModel.setSettingVisible(false)
                    ankiBaseViewModel.returnAnkiBaseNavCon()
                        ?.navigate(AnkiFlipBaseFragDirections.toFlipFrag())
                }
                btnAddToFavouriteAnkiBox -> {
                    if (btnAddToFavouriteAnkiBox.isSelected.not() && ankiBoxViewModel.returnAnkiBoxItems()
                            .isEmpty().not()
                    ) {
                        editFileViewModel.onClickCreateFile(FileStatus.ANKI_BOX_FAVOURITE)
                    } else return

                }
                topBarBinding.btnSetting -> ankiBaseViewModel.setSettingVisible(true)
                tabAllFlashCardCoverToAnkiBox -> ankiBoxViewModel.changeTab(AnkiBoxFragments.AllFlashCardCovers)
                tabLibraryToAnkiBox -> ankiBoxViewModel.changeTab(AnkiBoxFragments.Library)
                tabFavouritesToAnkiBox -> ankiBoxViewModel.changeTab(AnkiBoxFragments.Favourites)
            }
        }
    }
}