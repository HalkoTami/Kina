package com.example.tangochoupdated.ui.fragment.anki_frag_con

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiBoxFragments
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiFragments
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.*
import com.example.tangochoupdated.ui.animation.makeToast


class AnkiFragmentAnkiBox  : Fragment() {

    private var _binding: AnkiHomeFragBaseBinding? = null
    private val ankiBoxViewModel: AnkiBoxFragViewModel by activityViewModels()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()
    private val flipViewModel: AnkiFlipFragViewModel by activityViewModels()
    private val ankiBaseViewModel:AnkiFragBaseViewModel by activityViewModels()
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    lateinit var ankiBoxNavCon:NavController
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  AnkiHomeFragBaseBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val viewSetUp = AnkiBoxFragViewSetUp(
            )


        ankiBaseViewModel.setActiveFragment(AnkiFragments.AnkiBox)
        val frag = childFragmentManager.findFragmentById(binding.fragConAnkiBoxTab.id) as NavHostFragment
        ankiBoxNavCon = frag.navController

        viewSetUp.ankiBoxFragAddCL(settingVM,binding,ankiBoxViewModel,ankiBaseViewModel,createFileViewModel)
//        binding.apply {
//            linLayTabChange.tag = AnkiBoxFragments.AllFlashCardCovers
//            tabAllFlashCardCoverToAnkiBox.isSelected = true
//            arrayOf(tabFavouritesToAnkiBox,tabLibraryToAnkiBox,tabAllFlashCardCoverToAnkiBox).onEach {
//                it.setOnClickListener(AnkiBoxTabChangeCL(binding,ankiBoxViewModel,ankiBoxNavCon))
//            }
//        }
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
        createFileViewModel.apply {
            lastInsertedFile.observe(viewLifecycleOwner){
                setLastInsertedFile(it)
                makeToast(requireActivity(),it.toString())
            }
        }

        ankiBoxViewModel.apply{
//            lastInsertedFileId.observe(viewLifecycleOwner){
//                if(returnAddCardsToFavourite()) addCardsToFavourite(it) else return@observe
//            }
            setAnkiBoxNavCon(ankiBoxNavCon)
            toast.observe(viewLifecycleOwner){
                if(it.show) makeToast(requireActivity(),it.text) else return@observe
            }
            currentChildFragment.observe(viewLifecycleOwner){
                changeSelectedTab(it.currentTab,it.before)
            }

            ankiBoxFileIds.observe(viewLifecycleOwner){
                getDescendantsCardIds(it).observe(viewLifecycleOwner){
                    setAnkiBoxCardIds(it)
                }

            }
            ankiBoxCardIds.observe(viewLifecycleOwner){
                getCardsFromDBByMultipleCardIds(it).observe(viewLifecycleOwner){
                    setAnkiBoxItems(it)
                }
            }

//            modeCardsNotSelected.observe(viewLifecycleOwner){
//                if(it){
//                    flipViewModel.getAllCardsFromDB.observe(viewLifecycleOwner){
//                        flipViewModel.setAnkiFlipItems(it)
//                    }
//
//                }
//            }
            val a = mutableListOf<List<Card>>()
            allFavouriteAnkiBoxFromDB.observe(viewLifecycleOwner){
                it.onEach { file ->
                    getAnkiBoxRVCards(file.fileId).observe(viewLifecycleOwner){
                        a.add(it)
                    }
                }

            }
            viewSetUp.apply {
                ankiBoxItems.observe(viewLifecycleOwner) { it ->
                    binding.btnAddToFavouriteAnkiBox.isSelected = a.contains(it)
                    createFileViewModel.setAnkiBoxCards(it)
                    setUpAnkiBoxRing(it,binding.ringBinding)
                    setUpFlipProgressBar(it,binding.flipGraphBinding)
                    setUpPercentageIcons(binding.flipGraphBinding,binding.percentageIconBinding)
                    binding.btnStartAnki.text = if(it.isEmpty()) "カードを選ばず暗記" else "暗記開始"

                }
            }




            return root
        }
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
}