package com.example.tangochoupdated.ui.library.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.LibraryFragInboxBaseBinding
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibrarySetUpFragment
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.library.listadapter.LibFragPlaneRVListAdapter


class LibraryFragInBox  : Fragment(){

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val stringCardViewModel: StringCardViewModel by activityViewModels()
    private var _binding: LibraryFragInboxBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myNavCon =
            requireActivity().findNavController(R.id.lib_frag_con_view)
        _binding = LibraryFragInboxBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        val adapter= LibFragPlaneRVListAdapter(
            createFileViewModel  = createFileViewModel,
            libraryViewModel  = libraryViewModel,
            context  = requireActivity(),
            stringCardViewModel  = stringCardViewModel,
            createCardViewModel  = createCardViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false


        createFileViewModel.filterBottomMenuWhenInBox()
        libraryViewModel.apply {
            setModeInBox(true)

            childCardsFromDB(null).observe(viewLifecycleOwner) {
                setChildCardsFromDB(it)
                adapter.submitList(it)
                binding.emptyBinding.root.visibility =
                    if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility = if(it) View.VISIBLE else View.GONE
                binding.topBarInboxBinding.root.visibility = if(it) View.GONE else View.VISIBLE
                LibraryFragmentBase().changeLibRVSelectBtnVisibility(recyclerView,it)
                LibraryFragmentBase().changeStringBtnVisibility(recyclerView,it)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                LibraryFragmentBase().changeLibRVAllSelectedState(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) LibraryFragmentBase().makeLibRVUnSwiped(recyclerView)
            }
            selectedCards.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }


        }
        LibrarySetUpFragment(libraryViewModel).setUpFragLibInBox(binding,myNavCon,requireActivity())
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        libraryViewModel.setModeInBox(false)
        _binding = null
    }

}


