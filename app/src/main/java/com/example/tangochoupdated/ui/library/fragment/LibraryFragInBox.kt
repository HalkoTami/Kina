package com.example.tangochoupdated.ui.library.fragment

import LibraryFragInBoxClickListener
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
import com.example.tangochoupdated.databinding.LibraryFragInboxBaseBinding
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryTopBarMode
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.mainactivity.Animation


class LibraryFragInBox  : Fragment(){

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter: LibraryListAdapter
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()

    private var _binding: LibraryFragInboxBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myNavCon =
            requireActivity().findNavController(R.id.frag_container_view)
        _binding = LibraryFragInboxBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        adapter = LibraryListAdapter(createFileViewModel,createCardViewModel,libraryViewModel, requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false


        libraryViewModel.apply {
            childCardsFromDB(null).observe(viewLifecycleOwner) {
                setChildCardsFromDB(it)
            }
            myFinalList.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding.emptyBinding.root.visibility =
                    if (it.isEmpty()) View.VISIBLE else View.GONE
            }

        }
        addTopBarViews()
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addTopBarViews(){

        val inBox = binding.topBarInboxBinding
        val multi = binding.topBarMultiselectBinding
        arrayOf(
            inBox.imvMoveToFlashCard,
            inBox.imvCloseInbox,
            multi.imvCloseMultiMode,
            multi.imvSelectAll,
            multi.imvChangeMenuVisibility,
            multi.multiSelectMenuBinding.imvMoveSelectedItems,
            multi.multiSelectMenuBinding.imvDeleteSelectedItems,
            multi.multiSelectMenuBinding.imvSetFlagToSelectedItems,
        ).onEach { it.setOnClickListener( LibraryFragInBoxClickListener(requireContext(), binding,libraryViewModel, myNavCon)) }
    }
}


