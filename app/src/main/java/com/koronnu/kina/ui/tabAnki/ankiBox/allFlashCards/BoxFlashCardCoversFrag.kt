package com.koronnu.kina.ui.tabAnki.ankiBox.allFlashCards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.koronnu.kina.databinding.FullRvBinding
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.model.enumClasses.AnkiBoxFragments
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxListAdapter
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxViewModel


class BoxFlashCardCoversFrag  : Fragment() {

    private val args: BoxFlashCardCoversFragArgs by navArgs()
    private var _binding: FullRvBinding? = null
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  FullRvBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val fileId = args.fileId?.single()
        val listAdapter =  AnkiBoxListAdapter(requireContext(),ankiBoxViewModel,AnkiBoxFragments.AllFlashCardCovers,viewLifecycleOwner)
//        val adapter = viewSetUp
//            .setUpAnkiBoxRVListAdapter(binding.recyclerView,requireActivity(),ankiBoxViewModel,
//                AnkiBoxFragments.AllFlashCardCovers,viewLifecycleOwner)

        binding.listAdapter = listAdapter
        fun makeEmptyVisibleByListSize(list: List<Any>){
            binding.frameLayFullRvEmpty.visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
        }
        val allFlashCardCoverFromDBObserver = Observer<List<File>>{
            if(fileId==null){
                listAdapter.submitList(it)
                makeEmptyVisibleByListSize(it)
            }
        }
        val getCardsFromDBObserver = Observer<List<Card>>{
            if(fileId!=null){
                listAdapter.submitList(it)
                makeEmptyVisibleByListSize(it)
            }
        }
        ankiBoxViewModel.setCurrentChildFragment(AnkiBoxFragments.AllFlashCardCovers)
        ankiBoxViewModel.allFlashCardCoverFromDB.observe(viewLifecycleOwner,allFlashCardCoverFromDBObserver)
        ankiBoxViewModel.getCardsFromDB(fileId).observe(viewLifecycleOwner,getCardsFromDBObserver)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}