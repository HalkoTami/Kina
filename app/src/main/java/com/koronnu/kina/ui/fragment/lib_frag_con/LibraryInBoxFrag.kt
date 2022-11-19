package com.koronnu.kina.ui.fragment.lib_frag_con

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.*
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.*
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.customClasses.LibraryFragment
import com.koronnu.kina.ui.listadapter.LibFragPlaneRVListAdapter
import com.koronnu.kina.ui.listadapter.LibFragSearchRVListAdapter
import com.koronnu.kina.ui.listener.recyclerview.LibraryRVItemClickListener
import com.koronnu.kina.ui.listener.topbar.LibFragTopBarInBoxCL
import com.koronnu.kina.ui.observer.LibraryOb
import com.koronnu.kina.ui.view_set_up.LibraryAddListeners
import com.koronnu.kina.ui.view_set_up.LibrarySetUpItems
import com.koronnu.kina.ui.viewmodel.*


class LibraryInBoxFrag  : Fragment(){

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var topBarBinding:LibraryFragTopBarInboxBinding
    private lateinit var mainNavCon:NavController
    private lateinit var adapter:LibFragPlaneRVListAdapter
    private lateinit var searchAdapter:LibFragSearchRVListAdapter
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryBaseViewModel: LibraryBaseViewModel by activityViewModels()
    private val cardTypeStringViewModel: CardTypeStringViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: LibraryChildFragWithMulModeBaseBinding? = null
    private val binding get() = _binding!!
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fun setUpLateInitVars(){
            topBarBinding = LibraryFragTopBarInboxBinding.inflate(inflater,container,false)
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
        fun inBoxTopBarAddCL(){
            arrayOf(
                topBarBinding.imvMoveToFlashCard,
                topBarBinding.imvCloseInbox,
            ).onEach { it.setOnClickListener( LibFragTopBarInBoxCL(topBarBinding, libraryBaseViewModel)) }
        }
        fun addCL(){
            inBoxTopBarAddCL()
            LibraryAddListeners().fragChildMultiBaseAddCL(
                binding,requireActivity(),
                libraryBaseViewModel,
                null,
                deletePopUpViewModel,
                searchViewModel,
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                )
            recyclerView.addOnItemTouchListener(
                object : LibraryRVItemClickListener(requireActivity(),binding.frameLayTest,recyclerView,libraryBaseViewModel){})
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
        fun setTopBarText(list: List<Card>?){
            topBarBinding.txvInboxStatus.text =
                if(list.isNullOrEmpty().not()) "${list!!.size}個の単語帳に未追加のアイテム" else
                    "InBox"
        }
        setUpLateInitVars()
        setUpView()
        addCL()
        observeSwipe()
        observeMultiMode()
        val searchModeObserver = LibraryOb().searchModeObserver(binding,searchViewModel)
        searchViewModel.searchModeActive.observe(viewLifecycleOwner,searchModeObserver)

        editFileViewModel.filterBottomMenuOnlyCard()
        libraryBaseViewModel.apply {
            setModeInBox(true)
            setLibraryFragment(LibraryFragment.InBox)
            createCardViewModel.setParentFlashCardCover(null)
            val emptyView = RvEmptyBinding.inflate(inflater,container,false).root
            childCardsFromDB(null).observe(viewLifecycleOwner) {

                val sorted = it
                setParentRVItems(sorted?: mutableListOf())
                adapter.submitList(sorted)
                if(it.isNullOrEmpty()){
                    binding.frameLayRvEmpty.addView(emptyView)
                } else {
                    binding.frameLayRvEmpty.removeView(emptyView)
                }
                setTopBarText(it)
            }
            val commonViewSetUp = LibrarySetUpItems()
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility = if(it) View.VISIBLE else View.GONE
                topBarBinding.root.visibility = if(it) View.GONE else View.VISIBLE
                commonViewSetUp.changeLibRVSelectBtnVisibility(recyclerView,it)
                commonViewSetUp.changeStringBtnVisibility(recyclerView,it)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                commonViewSetUp.changeLibRVAllSelectedState(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) commonViewSetUp.makeLibRVUnSwiped(recyclerView)
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
        libraryBaseViewModel.setModeInBox(false)
        _binding = null
    }

}


