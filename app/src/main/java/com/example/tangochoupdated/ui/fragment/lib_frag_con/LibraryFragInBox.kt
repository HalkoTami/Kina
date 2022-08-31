package com.example.tangochoupdated.ui.fragment.lib_frag_con

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBase
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.viewmodel.*


class LibraryFragInBox  : Fragment(){

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val stringCardViewModel: StringCardViewModel by activityViewModels()
    private var _binding: LibraryChildFragWithMulModeBaseBinding? = null
    private val binding get() = _binding!!
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mainNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).findNavController()
        libNavCon =  requireActivity().findNavController(R.id.lib_frag_con_view)
        _binding = LibraryChildFragWithMulModeBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        val adapter= LibFragPlaneRVListAdapter(
            createFileViewModel  = createFileViewModel,
            libraryViewModel  = libraryViewModel,
            context  = requireActivity(),
            stringCardViewModel  = stringCardViewModel,
            createCardViewModel  = createCardViewModel,deletePopUpViewModel,
            libNavController = libNavCon,

            mainNavController = mainNavCon)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false


        val topBarBinding = LibraryFragTopBarInboxBinding.inflate(inflater,container,false)
        val addListeners = LibraryAddListeners(libraryViewModel,deletePopUpViewModel,libNavCon)
        addListeners.inBoxTopBarAddCL(topBarBinding,requireActivity(),libNavCon)
        binding.frameLayTopBar.addView(topBarBinding.root)

        createFileViewModel.filterBottomMenuWhenInBox()
        libraryViewModel.apply {
            setModeInBox(true)

            createCardViewModel.setParentFlashCardCover(null)
            val emptyView = LibraryFragLayInboxRvEmptyBinding.inflate(inflater,container,false).root
            childCardsFromDB(null).observe(viewLifecycleOwner) {
                setParentRVItems(it ?: mutableListOf())
                adapter.submitList(it)
                if(it.isNullOrEmpty().not()){
                    binding.frameLayRvEmpty.addView(emptyView)
                } else {
                    binding.frameLayRvEmpty.removeView(emptyView)
                }
            }
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility = if(it) View.VISIBLE else View.GONE
                topBarBinding.root.visibility = if(it) View.GONE else View.VISIBLE
                LibraryFragmentBase().changeLibRVSelectBtnVisibility(recyclerView,it)
                LibraryFragmentBase().changeStringBtnVisibility(recyclerView,it)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                LibraryFragmentBase().changeLibRVAllSelectedState(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) LibraryFragmentBase().makeLibRVUnSwiped(recyclerView)
            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }


        }
//        LibrarySetUpFragment(libraryViewModel, deletePopUpViewModel).setUpFragLibInBox(binding,myNavCon,requireActivity())
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        libraryViewModel.setModeInBox(false)
        _binding = null
    }

}


