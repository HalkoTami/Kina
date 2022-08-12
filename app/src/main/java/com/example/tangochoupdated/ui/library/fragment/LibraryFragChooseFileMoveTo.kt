package com.example.tangochoupdated.ui.library.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.LibraryFragSelectFileMoveToBaseBinding
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryAddClickListeners
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.library.listadapter.LibFragFileRVListAdapter



class LibraryFragChooseFileMoveTo  : Fragment(){
    private val args: LibraryFragChooseFileMoveToArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()

    private var _binding: LibraryFragSelectFileMoveToBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navHostFragment =
            childFragmentManager.findFragmentById(binding.mainFrameLayout.id) as NavHostFragment
        myNavCon = navHostFragment.navController
        _binding = LibraryFragSelectFileMoveToBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        val adapter = LibFragFileRVListAdapter(createFileViewModel,libraryViewModel,requireActivity(),true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false
        libraryViewModel.apply {
            childFilesFromDB(args.folderId.single()).observe(viewLifecycleOwner) {
                setChildFilesFromDB(it)
                adapter.submitList(it)
            }
            myFinalList.observe(viewLifecycleOwner) {
            }

        }
        LibraryAddClickListeners().fragLibChooseFileMoveToAddCL(binding,libraryViewModel,myNavCon,requireActivity())

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

