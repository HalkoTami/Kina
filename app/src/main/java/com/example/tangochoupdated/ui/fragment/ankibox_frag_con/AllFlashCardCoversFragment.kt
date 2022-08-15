package com.example.tangochoupdated.ui.fragment.ankibox_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.FullRvBinding
import com.example.tangochoupdated.ui.listadapter.AnkiBoxListAdapter
import com.example.tangochoupdated.ui.view_set_up.ViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel


class AllFlashCardCoversFragment  : Fragment() {

    private val args: AllFlashCardCoversFragmentArgs by navArgs()
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

        val fileId = args.fileId?.single()

        viewModel.apply {
            when(args.fileId){
                null -> {
                    val adapter = ViewSetUp().setUpAnkiBoxRVListAdapter(binding.recyclerView,requireActivity())
                    allFlashCardCoverFromDB.observe(viewLifecycleOwner){
                        adapter.submitList(it)
                    }
                }
                else -> {
                    val adapter = ViewSetUp().setUpAnkiBoxRVListAdapter(binding.recyclerView,requireActivity())
                    getLibraryCardsFromDB(fileId).observe(viewLifecycleOwner){

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