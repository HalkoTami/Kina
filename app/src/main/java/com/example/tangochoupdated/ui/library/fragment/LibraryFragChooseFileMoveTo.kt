package com.example.tangochoupdated.ui.library.fragment

import LibraryFragChooseFileMoveToClickListener
import LibraryPopUpConfirmDeleteClickListener
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragHomeBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragSelectFileMoveToBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragTopBarChooseFileMoveToBinding
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryTopBarMode
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.mainactivity.Animation


class LibraryFragChooseFileMoveTo  : Fragment(){
    private val args: LibraryFragChooseFileMoveToArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter: LibraryListAdapter
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
        adapter = LibraryListAdapter(createFileViewModel,createCardViewModel,libraryViewModel, requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false
        libraryViewModel.apply {
            childFilesFromDB(args.folderId.single()).observe(viewLifecycleOwner) {
                setChildFilesFromDB(it)
            }
            myFinalList.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding.emptyBinding.root.visibility =
                    if (it.isEmpty()) View.VISIBLE else View.GONE
            }

        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addTopBarViews(){

        val moveTo = binding.topBarChooseFileMoveToBinding
        arrayOf(
            moveTo.imvCloseChooseFileMoveTo
        ).onEach { it.setOnClickListener( LibraryFragChooseFileMoveToClickListener(requireContext(), binding,libraryViewModel, myNavCon)) }
    }
}

