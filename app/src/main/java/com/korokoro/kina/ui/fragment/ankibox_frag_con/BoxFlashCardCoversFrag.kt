package com.korokoro.kina.ui.fragment.ankibox_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.korokoro.kina.databinding.FullRvBinding
import com.korokoro.kina.ui.viewmodel.customClasses.AnkiBoxFragments
import com.korokoro.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.korokoro.kina.ui.viewmodel.AnkiBoxViewModel


class BoxFlashCardCoversFrag  : Fragment() {

    private val args: BoxFlashCardCoversFragArgs by navArgs()
    private var _binding: FullRvBinding? = null
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()

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
            .setUpAnkiBoxRVListAdapter(binding.recyclerView,requireActivity(),ankiBoxViewModel,
                AnkiBoxFragments.AllFlashCardCovers,viewLifecycleOwner)

        fun makeEmptyVisibleByListSize(list: List<Any>){
            binding.frameLayFullRvEmpty.visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
        }
        ankiBoxViewModel.apply {
            setCurrentChildFragment(AnkiBoxFragments.AllFlashCardCovers)
            when(fileId){
                null -> {
                    allFlashCardCoverFromDB.observe(viewLifecycleOwner){
                        adapter.submitList(it)
                        makeEmptyVisibleByListSize(it)
                    }
                }
                else -> {
                    getCardsFromDB(fileId).observe(viewLifecycleOwner){
                        adapter.submitList(it)
                        makeEmptyVisibleByListSize(it)

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