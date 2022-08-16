package com.example.tangochoupdated.ui.fragment.anki_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiViewModel
import com.example.tangochoupdated.ui.viewmodel.BaseViewModel


class AnkiFragmentAnkiBox  : Fragment() {

    private var _binding: AnkiHomeFragBaseBinding? = null
    private val viewModel: AnkiBoxFragViewModel by activityViewModels()

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

        binding.apply {
            linLayTabChange.tag = AnkiBoxTab.AllFlashCardCovers
            tabAllFlashCardCoverToAnkiBox.isSelected = true
            arrayOf(tabFavouritesToAnkiBox,tabLibraryToAnkiBox,tabAllFlashCardCoverToAnkiBox).onEach {
                it.setOnClickListener(AnkiBoxTabChangeCL(binding,viewModel))
            }
        }
        viewModel.tabChangeAction.observe(viewLifecycleOwner){
            val a = childFragmentManager.findFragmentById(binding.ankiBoxFragConView.id) as NavHostFragment
            a.navController.navigate(it)
            Toast.makeText(requireActivity(), "navigated ", Toast.LENGTH_SHORT).show()
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}