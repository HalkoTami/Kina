package com.koronnu.kina.ui.fragment.lib_frag_con


import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.*
import com.koronnu.kina.actions.changeViewIfRVEmpty
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.*
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.customClasses.enumClasses.LibraryFragment
import com.koronnu.kina.ui.listadapter.LibFragPlaneRVListAdapter
import com.koronnu.kina.ui.listadapter.LibFragSearchRVListAdapter
import com.koronnu.kina.ui.listener.recyclerview.LibraryRVItemClickListener
import com.koronnu.kina.ui.listener.topbar.LibFragTopBarHomeCL
import com.koronnu.kina.ui.observer.LibraryOb
import com.koronnu.kina.ui.view_set_up.LibraryAddListeners
import com.koronnu.kina.ui.view_set_up.LibrarySetUpItems
import com.koronnu.kina.ui.viewmodel.*


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
    private val mainViewModel: MainViewModel by activityViewModels()

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
                stringCardViewModel  = cardTypeStringViewModel,
                createCardViewModel  = createCardViewModel,
                mainNavController = mainNavCon,
                deletePopUpViewModel = deletePopUpViewModel,
                createFileViewModel = editFileViewModel,
                libraryViewModel = libraryBaseViewModel,
            )
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
            ).onEach { it.setOnClickListener( LibFragTopBarHomeCL(topBarBinding, libraryBaseViewModel)) }
        }
        fun addCL(){
            homeTopBarAddCL()

            LibraryAddListeners().fragChildMultiBaseAddCL(
                binding,requireActivity(),
                libraryBaseViewModel,
                topBarBinding.imvSearchLoup,
                deletePopUpViewModel,
                searchViewModel,
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            )
            recyclerView.addOnItemTouchListener(
                object :LibraryRVItemClickListener(requireActivity(),binding.frameLayTest,recyclerView,libraryBaseViewModel){})
        }
        fun setUpView(){
            val commonViewSetUp = LibrarySetUpItems()
            commonViewSetUp.setUpLibFragWithMultiModeBase(binding,topBarBinding.root,searchAdapter,adapter,requireActivity())
        }

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
                multiMenuVisibility
                    .observe(viewLifecycleOwner,LibraryOb().multiMenuVisibilityObserver(binding))

            }

        }
        setUpLateInitVars()
        val emptyView = RvEmptyBinding.inflate(inflater,container,false).root
        val searchModeObserver = LibraryOb().searchModeObserver(binding,searchViewModel)
        val homeRVItemsObserver = Observer<List<File>>{
            val sorted = it
            libraryBaseViewModel.setParentRVItems(sorted)
            val mainRV = binding.vocabCardRV
            adapter = LibFragPlaneRVListAdapter(
                stringCardViewModel  = cardTypeStringViewModel,
                createCardViewModel  = createCardViewModel,
                mainNavController = mainNavCon,
                deletePopUpViewModel = deletePopUpViewModel,
                createFileViewModel = editFileViewModel,
                libraryViewModel = libraryBaseViewModel,
            )
            mainRV.adapter = adapter
            mainRV.layoutManager = LinearLayoutManager(context)
            mainRV.isNestedScrollingEnabled = true
//            adapter.submitList(null)
            adapter.submitList(sorted)
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
        libraryBaseViewModel.setParentFile(null)
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


