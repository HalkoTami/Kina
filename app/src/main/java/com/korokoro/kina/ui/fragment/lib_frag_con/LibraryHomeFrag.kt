package com.korokoro.kina.ui.fragment.lib_frag_con


import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.*
import com.korokoro.kina.actions.changeViewIfRVEmpty
import com.korokoro.kina.actions.changeViewVisibility
import com.korokoro.kina.actions.makeToast
import com.korokoro.kina.databinding.*
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.customClasses.LibraryFragment
import com.korokoro.kina.ui.listadapter.LibFragPlaneRVListAdapter
import com.korokoro.kina.ui.listadapter.LibFragSearchRVListAdapter
import com.korokoro.kina.ui.listener.topbar.LibFragTopBarHomeCL
import com.korokoro.kina.ui.observer.LibraryOb
import com.korokoro.kina.ui.view_set_up.LibraryAddListeners
import com.korokoro.kina.ui.view_set_up.LibrarySetUpItems
import com.korokoro.kina.ui.viewmodel.*


class LibraryHomeFrag : Fragment(){

    private lateinit var topBarBinding:LibraryFragTopBarHomeBinding
    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var mainNavCon:NavController
    private lateinit var adapter:LibFragPlaneRVListAdapter
    private lateinit var searchAdapter:LibFragSearchRVListAdapter
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

        fun setUpLateInitVars(){
            topBarBinding = LibraryFragTopBarHomeBinding.inflate(inflater,container,false)
            libNavCon =  requireActivity().findNavController(R.id.lib_frag_con_view)
            _binding = LibraryChildFragWithMulModeBaseBinding.inflate(inflater, container, false)
            recyclerView = binding.vocabCardRV
            mainNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).findNavController()
            adapter =  LibFragPlaneRVListAdapter(
                context  = requireActivity(),
                stringCardViewModel  = cardTypeStringViewModel,
                createCardViewModel  = createCardViewModel,
                mainNavController = mainNavCon,
                deletePopUpViewModel = deletePopUpViewModel,
                createFileViewModel = editFileViewModel,
                libraryViewModel = libraryBaseViewModel)
            searchAdapter = LibFragSearchRVListAdapter(
                libraryViewModel = libraryBaseViewModel,
                stringCardViewModel = cardTypeStringViewModel,
                createCardViewModel = createCardViewModel,
                searchViewModel = searchViewModel,
                lifecycleOwner = viewLifecycleOwner,
                mainNavController = mainNavCon,
                context = requireActivity(),
            )
        }
        fun homeTopBarAddCL(){
            arrayOf(
                topBarBinding.frameLayInBox,
                topBarBinding.imvBookMark,
            ).onEach { it.setOnClickListener( LibFragTopBarHomeCL(topBarBinding, libraryBaseViewModel)) }
        }
        fun addCL(){
            homeTopBarAddCL()
            LibraryAddListeners().fragChildMultiBaseAddCL(
                binding,requireActivity(),
                libraryBaseViewModel,
                createCardViewModel,
                deletePopUpViewModel,
                searchViewModel,
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            )
        }
        fun setUpView(){
            val commonViewSetUp = LibrarySetUpItems()
            commonViewSetUp.setUpLibFragWithMultiModeBase(binding,topBarBinding.root,searchAdapter,adapter,requireActivity())
        }

        fun observeSwipe(){
            libraryBaseViewModel.apply {
                rvCover.observe(viewLifecycleOwner){
                    binding.rvCover.visibility = if(it.visible) View.VISIBLE else View.GONE
                }
                leftSwipedItemExists.observe(viewLifecycleOwner){
                    setRVCover(LibraryBaseViewModel.RvCover(it.not()))
                }
                makeAllUnSwiped.observe(viewLifecycleOwner){
                    if(it) LibrarySetUpItems().makeLibRVUnSwiped(recyclerView)
                }
            }
        }
        fun observeMultiMode(){
            libraryBaseViewModel.apply {
                multipleSelectMode.observe(viewLifecycleOwner){
                    binding.topBarMultiselectBinding.root.visibility = if(it) View.VISIBLE else View.GONE
                    topBarBinding.root.visibility = if(!it) View.VISIBLE else View.GONE
                    LibrarySetUpItems().changeLibRVSelectBtnVisibility(recyclerView,it)
                    if(it.not()) changeViewVisibility(binding.frameLayMultiModeMenu,false)
                }
                changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                    LibrarySetUpItems().changeLibRVAllSelectedState(recyclerView,it)
                }
                selectedItems.observe(viewLifecycleOwner){
                    binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
                }

            }

        }
        setUpLateInitVars()
        val emptyView = LibraryFragLayHomeRvEmptyBinding.inflate(inflater,container,false).root
        val searchModeObserver = LibraryOb().searchModeObserver(binding,searchViewModel)
        val homeRVItemsObserver = Observer<List<File>>{
            val sorted = it.sortedBy { it.libOrder }
            libraryBaseViewModel.setParentRVItems(sorted)
            if( adapter.currentList.size == it.size) adapter.submitList(null)
            adapter.submitList(it)
            changeViewIfRVEmpty(it,binding.frameLayRvEmpty,emptyView)
        }

        setUpView()
        addCL()
        observeSwipe()
        observeMultiMode()
        createCardViewModel.setParentFlashCardCover(null)
        editFileViewModel.setParentTokenFileParent(null)
        editFileViewModel.makeAllBottomMenuClickable()
        searchViewModel.matchedItems.observe(viewLifecycleOwner){
            searchAdapter.submitList(it)
        }
        searchViewModel.searchModeActive.observe(viewLifecycleOwner,searchModeObserver)
        libraryBaseViewModel.apply {
            setLibraryFragment(LibraryFragment.Home)

            libraryBaseViewModel.clearFinalList()

            childFilesFromDB(null).observe(viewLifecycleOwner,homeRVItemsObserver)
            childCardsFromDB(null).observe(viewLifecycleOwner){
                topBarBinding.txvInBoxCardAmount.apply {
                    text = it?.size.toString()
                    visibility = if(it?.size == 0) View.GONE else View.VISIBLE
                }
                createCardViewModel.setSisterCards(it?:return@observe)
            }










        }

//        LibrarySetUpFragment(libraryViewModel,deletePopUpViewModel).setUpFragLibHome(binding,myNavCon,requireActivity())






        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                if(!libraryBaseViewModel.checkViewReset())
                    libNavCon.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}


