package com.example.tangochoupdated.ui.fragment.ankibox_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.databinding.FullRvBinding
import com.example.tangochoupdated.ui.fragment.lib_frag_con.LibraryFragFlashCardCoverArgs
import com.example.tangochoupdated.ui.listadapter.AnkiBoxListAdapter
import com.example.tangochoupdated.ui.view_set_up.ViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiViewModel
import com.example.tangochoupdated.ui.viewmodel.BaseViewModel


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

        val adapter = ViewSetUp().setUpAnkiBoxRVListAdapter(binding.recyclerView,requireActivity())
        viewModel.getLibraryFilesFromDB(args.fileId?.single()).observe(viewLifecycleOwner){
            adapter.submitList(it)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}