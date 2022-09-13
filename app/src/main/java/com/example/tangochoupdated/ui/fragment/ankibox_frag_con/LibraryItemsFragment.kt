package com.example.tangochoupdated.ui.fragment.ankibox_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.databinding.FullRvBinding
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiBoxFragments
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel


class LibraryItemsFragment  : Fragment() {
    private val args: LibraryItemsFragmentArgs by navArgs()
    private var _binding: FullRvBinding? = null
    private val viewModel: AnkiBoxFragViewModel by activityViewModels()

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
        when(args.flashCard){
            false ->viewModel.getLibraryFilesFromDB(fileId).observe(viewLifecycleOwner){
                adapter.submitList(it)
            }
            true -> viewModel.getCardsFromDB(fileId).observe(viewLifecycleOwner){
                adapter.submitList(it)
            }
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}