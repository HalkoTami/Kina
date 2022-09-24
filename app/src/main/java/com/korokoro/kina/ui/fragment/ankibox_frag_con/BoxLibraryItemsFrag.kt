package com.korokoro.kina.ui.fragment.ankibox_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.korokoro.kina.databinding.FullRvBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.customClasses.AnkiBoxFragments
import com.korokoro.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.korokoro.kina.ui.viewmodel.AnkiBoxViewModel


class BoxLibraryItemsFrag  : Fragment() {
    private val args: BoxLibraryItemsFragArgs by navArgs()
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
        val viewSetUp = AnkiBoxFragViewSetUp()
        val adapter = viewSetUp
            .setUpAnkiBoxRVListAdapter(binding.recyclerView,requireActivity(),ankiBoxViewModel,
                AnkiBoxFragments.AllFlashCardCovers,viewLifecycleOwner)
        val fileId = args.fileId?.single()
        ankiBoxViewModel.setCurrentChildFragment(AnkiBoxFragments.Library)
        fun makeEmptyVisibleByListSize(list: List<Any>){
            binding.frameLayFullRvEmpty.visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
        }
        val getLibraryFilesFromDBObserver = Observer<List<File>>{
            if(!args.flashCard){
                adapter.submitList(it)
                makeEmptyVisibleByListSize(it)
            }
        }
        val getCardsFromDBObserver = Observer<List<Card>>{
            if(args.flashCard){
            adapter.submitList(it)
            makeEmptyVisibleByListSize(it)
            }
        }
        ankiBoxViewModel.getLibraryFilesFromDB(fileId).observe(viewLifecycleOwner,getLibraryFilesFromDBObserver)
        ankiBoxViewModel.getCardsFromDB(fileId).observe(viewLifecycleOwner,getCardsFromDBObserver)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}