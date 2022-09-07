package com.example.tangochoupdated.ui.fragment.ankibox_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.databinding.FullRvBinding
import com.example.tangochoupdated.db.enumclass.AnkiBoxFragments
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel


class AllFlashCardCoversFragment  : Fragment() {

    private val args: AllFlashCardCoversFragmentArgs by navArgs()
    private var _binding: FullRvBinding? = null
    private val ankiBoxViewModel: AnkiBoxFragViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  FullRvBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val fileId = args.fileId?.single()
        val viewSetUp = AnkiBoxFragViewSetUp()
        val adapter = viewSetUp
            .setUpAnkiBoxRVListAdapter(binding.recyclerView,requireActivity(),ankiBoxViewModel,AnkiBoxFragments.AllFlashCardCovers,viewLifecycleOwner)

        ankiBoxViewModel.apply {
            setCurrentChildFragment(AnkiBoxFragments.AllFlashCardCovers)
            when(fileId){
                null -> {

                    allFlashCardCoverFromDB.observe(viewLifecycleOwner){
                        adapter.submitList(it)
                        binding.recyclerView
                    }
                }
                else -> {
                    getCardsFromDB(fileId).observe(viewLifecycleOwner){
                        adapter.submitList(it)

                    }
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}