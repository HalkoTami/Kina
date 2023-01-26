package com.koronnu.kina.ui.tabLibrary.inBox

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
import com.koronnu.kina.util.changeViewVisibility
import com.koronnu.kina.databinding.*
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.model.enumClasses.LibraryFragment
import com.koronnu.kina.ui.EditFileViewModel
import com.koronnu.kina.ui.MainViewModel
import com.koronnu.kina.ui.editCard.CreateCardViewModel
import com.koronnu.kina.ui.editCard.editCardContent.stringCard.CardTypeStringViewModel
import com.koronnu.kina.ui.listener.topbar.LibFragTopBarInBoxCL
import com.koronnu.kina.util.LibraryOb
import com.koronnu.kina.ui.tabLibrary.*
import com.koronnu.kina.util.view_set_up.LibraryAddListeners
import com.koronnu.kina.util.view_set_up.LibrarySetUpItems
import com.koronnu.kina.ui.viewmodel.*


class LibraryInBoxFrag  : Fragment(){

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var topBarBinding:LibraryFragTopBarInboxBinding
    private lateinit var mainNavCon:NavController
    private lateinit var adapter: LibFragPlaneRVListAdapter
    private lateinit var searchAdapter:LibFragSearchRVListAdapter
    private val libraryBaseViewModel: LibraryBaseViewModel by activityViewModels()
    private val  libraryInBoxFragViewModel: LibraryInBoxFragViewModel by activityViewModels()
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()

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
            libraryBaseViewModel.setChildFragBinding(binding)
            recyclerView = binding.vocabCardRV
            mainNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.fcv_activityMain).findNavController()
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
            binding.apply {
                libraryViewModel = libraryBaseViewModel
                planeRVAdapter = adapter
                seachRvAdapter = searchAdapter
                lifecycleOwner = viewLifecycleOwner
            }
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
                object : LibraryRVItemClickListener(recyclerView,){})
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
                this.multipleSelectMode.observe(viewLifecycleOwner){
                    binding.topBarMultiselectBinding.root.visibility = if(it) View.VISIBLE else View.GONE
                    topBarBinding.root.visibility = if(!it) View.VISIBLE else View.GONE
                    LibrarySetUpItems().changeLibRVSelectBtnVisibility(recyclerView,it)
                    if(it.not()) changeViewVisibility(binding.frameLayMultiModeMenu,false)
                }
                changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                    LibrarySetUpItems().changeLibRVAllSelectedState(recyclerView,it)
                }
                selectedItems.observe(viewLifecycleOwner){
                    binding.topBarMultiselectBinding.txvSelectingStatus.text =  resources.getString(R.string.topBarMultiSelectBin_selectingStatus,it.size)
                }
                multiMenuVisibility
                    .observe(viewLifecycleOwner, LibraryOb().multiMenuVisibilityObserver(binding))

            }

        }
        fun setTopBarText(list: List<Card>?){
            topBarBinding.txvInboxStatus.text =if(list.isNullOrEmpty()) resources.getString( R.string.txvInBoxStatus_empty) else resources.getString(R.string.txvInBoxStatus_notEmpty,list.size)

        }
        setUpLateInitVars()
        setUpView()
        addCL()
        observeSwipe()
        observeMultiMode()
        val searchModeObserver = LibraryOb().searchModeObserver(binding,searchViewModel)
        searchViewModel.searchModeActive.observe(viewLifecycleOwner,searchModeObserver)
        libraryInBoxFragViewModel.observeLiveDataInInboxFrag(viewLifecycleOwner)
        libraryBaseViewModel.apply {
            setModeInBox(true)
            setLibraryFragment(LibraryFragment.InBox)
            createCardViewModel.setParentFlashCardCover(null)
            val emptyView = RvEmptyBinding.inflate(inflater,container,false).root
            childCardsFromDB(null).observe(viewLifecycleOwner) {

                val sorted = it
//                setParentRVItems(sorted?: mutableListOf())
                adapter.submitList(sorted)
                if(it.isNullOrEmpty()){
                    binding.frameLayRvEmpty.addView(emptyView)
                } else {
                    binding.frameLayRvEmpty.removeView(emptyView)
                }
                setTopBarText(it)
            }
            val commonViewSetUp = LibrarySetUpItems()
            this.multipleSelectMode.observe(viewLifecycleOwner){
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
                binding.topBarMultiselectBinding.txvSelectingStatus.text = resources.getString(R.string.topBarMultiSelectBin_selectingStatus,it.size)
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


