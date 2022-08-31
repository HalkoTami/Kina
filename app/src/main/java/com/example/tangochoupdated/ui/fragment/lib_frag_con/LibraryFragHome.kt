package com.example.tangochoupdated.ui.fragment.lib_frag_con


import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.ui.view_set_up.SearchViewModel
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBase
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.listadapter.LibFragSearchRVListAdapter
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.viewmodel.*


class LibraryFragHome : Fragment(){

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val stringCardViewModel: StringCardViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()

    private var _binding: LibraryChildFragWithMulModeBaseBinding? = null
    private val binding get() = _binding!!


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
            createCardViewModel  = createCardViewModel,
            deletePopUpViewModel, mainNavController =mainNavCon, libNavController = libNavCon)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false
        libNavCon =  requireActivity().findNavController(R.id.lib_frag_con_view)

        val topBarBinding = LibraryFragTopBarHomeBinding.inflate(inflater,container,false)
        val addListeners = LibraryAddListeners(libraryViewModel,deletePopUpViewModel,libNavCon)
        addListeners.homeTopBarAddCL(topBarBinding,requireActivity(),libNavCon)
        binding.frameLayTopBar.addView(topBarBinding.root)

        createCardViewModel.setParentFlashCardCover(null)
        createFileViewModel.setParentFile(null)
        createFileViewModel.makeAllBottomMenuClickable()
        libraryViewModel.apply {
            libraryViewModel.clearFinalList()
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility = if(it) View.VISIBLE else View.GONE
                topBarBinding.root.visibility = if(!it) View.VISIBLE else View.GONE
                LibraryFragmentBase().changeLibRVSelectBtnVisibility(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) LibraryFragmentBase().makeLibRVUnSwiped(recyclerView)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                LibraryFragmentBase().changeLibRVAllSelectedState(recyclerView,it)
            }
            val emptyView = LibraryFragLayHomeRvEmptyBinding.inflate(inflater,container,false).root
            childFilesFromDB(null).observe(viewLifecycleOwner){
                topBarBinding.txvInBoxCardAmount.apply {
                    text = it?.size.toString()
                    visibility = if(it?.size == 0) View.GONE else View.VISIBLE
                }
                adapter.submitList(it)
                if(it.isNullOrEmpty()){
                    binding.frameLayRvEmpty.addView(emptyView)
                } else {
                    binding.frameLayRvEmpty.removeView(emptyView)
                }

            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }
            val searchAdapter = LibFragSearchRVListAdapter(
                createFileViewModel,libNavCon,
                libraryViewModel,stringCardViewModel,createCardViewModel,searchViewModel,viewLifecycleOwner,deletePopUpViewModel,
                mainNavCon,
                requireActivity()
            )
            val searchRv = binding.searchRvBinding.recyclerView
            searchRv.adapter = searchAdapter
            searchRv.layoutManager = LinearLayoutManager(requireActivity())
            searchRv.isNestedScrollingEnabled = false
            binding.bindingSearch.edtLibrarySearch.addTextChangedListener {
                if(it.toString()!=""){
                    searchViewModel.setSearchText(it.toString())
                    searchViewModel.getFilesByWords(it.toString()).observe(viewLifecycleOwner){
                        binding.searchRvBinding.root.visibility = View.VISIBLE
                        searchAdapter.submitList(it)

                    }

                } else binding.searchRvBinding.root.visibility = View.GONE
            }

        }

//        LibrarySetUpFragment(libraryViewModel,deletePopUpViewModel).setUpFragLibHome(binding,myNavCon,requireActivity())






        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}


