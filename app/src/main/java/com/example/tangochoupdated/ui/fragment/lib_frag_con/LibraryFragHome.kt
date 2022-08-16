package com.example.tangochoupdated.ui.fragment.lib_frag_con


import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.LibraryFragHomeBaseBinding
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.StringCardViewModel
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpFragment
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel
import com.example.tangochoupdated.ui.view_set_up.SearchViewModel
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBase
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.listadapter.LibFragSearchRVListAdapter


class LibraryFragHome : Fragment(){

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val stringCardViewModel: StringCardViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val searchViewModel:SearchViewModel by activityViewModels()

    private var _binding: LibraryFragHomeBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = LibraryFragHomeBaseBinding.inflate(inflater, container, false)
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
        myNavCon =  requireActivity().findNavController(R.id.frag_container_view)



        createCardViewModel.setParentFlashCardCover(null)
        createFileViewModel.makeAllBottomMenuClickable()
        libraryViewModel.apply {
            libraryViewModel.clearFinalList()
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility = if(it) View.VISIBLE else View.GONE
                binding.topBarHomeBinding.root.visibility = if(!it) View.VISIBLE else View.GONE
                LibraryFragmentBase().changeLibRVSelectBtnVisibility(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) LibraryFragmentBase().makeLibRVUnSwiped(recyclerView)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                LibraryFragmentBase().changeLibRVAllSelectedState(recyclerView,it)
            }
            childCardsFromDB(null).observe(viewLifecycleOwner){
                binding.topBarHomeBinding.txvInBoxCardAmount.apply {
                    text = it?.size.toString()
                    visibility = if(it?.size == 0) View.GONE else View.VISIBLE
                }
                createCardViewModel.setSisterCards(it)
            }
            selectedFiles.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }
            binding.bindingSearch.edtLibrarySearch.addTextChangedListener {
                if(it.toString()!=""){
                    searchViewModel.setSearchText(it.toString())
                    searchViewModel.getFilesByWords(it.toString()).observe(viewLifecycleOwner){
                        val searchAdapter = LibFragSearchRVListAdapter(
                            createFileViewModel,libraryViewModel,stringCardViewModel,createCardViewModel,searchViewModel,viewLifecycleOwner,requireActivity()
                        )
                        recyclerView.adapter = searchAdapter
                        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
                        recyclerView.isNestedScrollingEnabled = false
                        searchAdapter.submitList(it)

                    }

                } else {
                    libraryViewModel.childFilesFromDB(null).observe(viewLifecycleOwner) {
                        setChildFilesFromDB(it)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
                        recyclerView.isNestedScrollingEnabled = false
                        adapter.submitList(it)
                        binding.emptyBinding.root.visibility =
                            if (it.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }

        }

        LibrarySetUpFragment(libraryViewModel).setUpFragLibHome(binding,myNavCon,requireActivity())






        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}


