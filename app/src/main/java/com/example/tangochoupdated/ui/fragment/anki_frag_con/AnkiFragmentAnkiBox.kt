package com.example.tangochoupdated.ui.fragment.anki_frag_con

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel


class AnkiFragmentAnkiBox  : Fragment() {

    private var _binding: AnkiHomeFragBaseBinding? = null
    private val viewModel: AnkiBoxFragViewModel by activityViewModels()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()

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
            ankiBoxVM = viewModel,
            context =  requireActivity(),
            bindingAnkiBoxFrag = binding,
        )
        viewSetUp.ankiBoxFragAddCL(settingVM)

        binding.apply {
            linLayTabChange.tag = AnkiBoxTab.AllFlashCardCovers
            tabAllFlashCardCoverToAnkiBox.isSelected = true
            arrayOf(tabFavouritesToAnkiBox,tabLibraryToAnkiBox,tabAllFlashCardCoverToAnkiBox).onEach {
                it.setOnClickListener(AnkiBoxTabChangeCL(binding,viewModel))
            }
        }
        viewModel.apply{
            tabChangeAction.observe(viewLifecycleOwner){
                val a = childFragmentManager.findFragmentById(binding.ankiBoxFragConView.id) as NavHostFragment
                a.navController.navigate(it)
                Toast.makeText(requireActivity(), "navigated ", Toast.LENGTH_SHORT).show()
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

            viewSetUp.apply {
                ankiBoxItems.observe(viewLifecycleOwner) {
                    setUpAnkiBoxRing(it)
                    setUpFlipProgressBar(it)
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
                val navCon = requireActivity().findViewById<FragmentContainerView>(R.id.anki_box_frag_con_view).findNavController()
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
        _binding = null
    }
}