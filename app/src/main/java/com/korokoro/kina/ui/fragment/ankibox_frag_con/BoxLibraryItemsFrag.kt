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


class BoxLibraryItemsFrag  : Fragment() {
    private val args: LibraryItemsFragmentArgs by navArgs()
    private var _binding: FullRvBinding? = null
    private val viewModel: AnkiBoxViewModel by activityViewModels()

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
            .setUpAnkiBoxRVListAdapter(binding.recyclerView,requireActivity(),viewModel,
                AnkiBoxFragments.AllFlashCardCovers,viewLifecycleOwner)
        val fileId = args.fileId?.single()
        viewModel.setCurrentChildFragment(AnkiBoxFragments.Library)
        fun makeEmptyVisibleByListSize(list: List<Any>){
            binding.frameLayFullRvEmpty.visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
        }
        when(args.flashCard){
            false ->viewModel.getLibraryFilesFromDB(fileId).observe(viewLifecycleOwner){
                val filtered = it.filter { it.descendantsData.descendantsCardsAmount !=0 }
                adapter.submitList(filtered)
                makeEmptyVisibleByListSize(filtered)
            }
            true -> viewModel.getCardsFromDB(fileId).observe(viewLifecycleOwner){
                adapter.submitList(it)
                makeEmptyVisibleByListSize(it)
            }
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}