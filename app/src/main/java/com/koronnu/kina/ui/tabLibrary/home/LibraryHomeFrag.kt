package com.koronnu.kina.ui.tabLibrary.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.data.model.enumClasses.LibraryFragment
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.databinding.LibraryChildFragWithMulModeBaseBinding
import com.koronnu.kina.databinding.LibraryFragTopBarHomeBinding
import com.koronnu.kina.ui.EditFileViewModel
import com.koronnu.kina.ui.editCard.CreateCardViewModel
import com.koronnu.kina.ui.editCard.editCardContent.stringCard.CardTypeStringViewModel
import com.koronnu.kina.ui.tabLibrary.*
import com.koronnu.kina.ui.viewmodel.SearchViewModel
import com.koronnu.kina.util.LibraryOb
import com.koronnu.kina.util.view_set_up.LibrarySetUpItems


class LibraryHomeFrag : Fragment(){

    private lateinit var topBarBinding:LibraryFragTopBarHomeBinding
    private lateinit var recyclerView:RecyclerView
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()
    private val cardTypeStringViewModel: CardTypeStringViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryBaseViewModel: LibraryBaseViewModel by activityViewModels()
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()

    private var _binding: LibraryChildFragWithMulModeBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LibraryChildFragWithMulModeBaseBinding.inflate(inflater, container, false)
        topBarBinding = LibraryFragTopBarHomeBinding.inflate(inflater,container,false)
        val mainNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.fcv_activityMain).findNavController()
        val adapter =  LibFragPlaneRVListAdapter(
            stringCardViewModel  = cardTypeStringViewModel,
            createCardViewModel  = createCardViewModel,
            mainNavController = mainNavCon,
            deletePopUpViewModel = deletePopUpViewModel,
            createFileViewModel = editFileViewModel,
            libraryViewModel = libraryBaseViewModel,
        )
        val searchAdapter = LibFragSearchRVListAdapter(
            libraryViewModel = libraryBaseViewModel,
            stringCardViewModel = cardTypeStringViewModel,
            createCardViewModel = createCardViewModel,
            searchViewModel = searchViewModel,
            lifecycleOwner = viewLifecycleOwner,
            mainNavController = mainNavCon,
            context = requireActivity(),
        )
        _binding = LibraryChildFragWithMulModeBaseBinding.inflate(inflater, container, false)
        binding.apply {
            libraryViewModel = libraryBaseViewModel
            planeRVAdapter = adapter
            seachRvAdapter = searchAdapter
            lifecycleOwner = viewLifecycleOwner
        }

        topBarBinding.apply {
            libraryViewModel = libraryBaseViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        binding.flTpbLibrary.addView(topBarBinding.root)
        val recyclerView = binding.vocabCardRV

        fun observeSwipe(){
            libraryBaseViewModel.apply {
                makeAllUnSwiped.observe(viewLifecycleOwner){
                    if(it) LibrarySetUpItems().makeLibRVUnSwiped(recyclerView)
                }
            }
        }
        fun observeMultiMode(){
            libraryBaseViewModel.apply {
                multipleSelectMode.observe(viewLifecycleOwner){
                    LibrarySetUpItems().changeLibRVSelectBtnVisibility(recyclerView,it)
                }
                changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                    LibrarySetUpItems().changeLibRVAllSelectedState(recyclerView,it)
                }
            }
        }
        val searchModeObserver = LibraryOb().searchModeObserver(binding,searchViewModel)
        val homeRVItemsObserver = Observer<List<File>?>{
            val sorted = it
            libraryBaseViewModel.setParentRVItems(sorted)
            adapter.submitList(sorted)
        }
        recyclerView.addOnItemTouchListener(
            object : LibraryRVItemClickListener(recyclerView){})
        observeSwipe()
        observeMultiMode()
        createCardViewModel.setParentFlashCardCover(null)
        searchViewModel.matchedItems.observe(viewLifecycleOwner){
            searchAdapter.submitList(it)
        }
        libraryBaseViewModel.setParentFile(null)
        searchViewModel.searchModeActive.observe(viewLifecycleOwner,searchModeObserver)
        libraryBaseViewModel.apply {
            setLibraryFragment(LibraryFragment.Home)
            libraryBaseViewModel.clearFinalList()
            childFilesFromDB(null).observe(viewLifecycleOwner,homeRVItemsObserver)
            childCardsFromDB(null).observe(viewLifecycleOwner){
                createCardViewModel.setSisterCards(it?:return@observe)
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        libraryBaseViewModel.setChildFragBinding(binding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


