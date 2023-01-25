package com.koronnu.kina.ui.tabLibrary.flashCardCover

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.*
import com.koronnu.kina.util.changeViewVisibility
import com.koronnu.kina.databinding.LibraryChildFragWithMulModeBaseBinding
import com.koronnu.kina.databinding.LibraryFragTopBarFileBinding
import com.koronnu.kina.databinding.RvEmptyBinding
import com.koronnu.kina.data.model.enumClasses.LibraryFragment
import com.koronnu.kina.ui.EditFileViewModel
import com.koronnu.kina.ui.editCard.CreateCardViewModel
import com.koronnu.kina.ui.editCard.editCardContent.stringCard.CardTypeStringViewModel
import com.koronnu.kina.util.LibraryOb
import com.koronnu.kina.ui.tabLibrary.*
import com.koronnu.kina.util.view_set_up.LibraryAddListeners
import com.koronnu.kina.util.view_set_up.LibrarySetUpItems
import com.koronnu.kina.ui.viewmodel.*


class
LibraryFlashCardCoverFrag  : Fragment(){
    private val args: com.koronnu.kina.ui.tabLibrary.flashCardCover.LibraryFlashCardCoverFragArgs by navArgs()

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var topBarBinding:LibraryFragTopBarFileBinding
    private lateinit var mainNavCon:NavController
    private lateinit var adapter: LibFragPlaneRVListAdapter
    private lateinit var searchAdapter: LibFragSearchRVListAdapter
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val cardTypeStringViewModel: CardTypeStringViewModel by activityViewModels()
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()
    private val libraryBaseViewModel: LibraryBaseViewModel by activityViewModels()
    private var _binding: LibraryChildFragWithMulModeBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fun setUpLateInitVars(){
            topBarBinding = LibraryFragTopBarFileBinding.inflate(inflater,container,false)
            topBarBinding.apply {
                libraryViewModel = libraryBaseViewModel
                lifecycleOwner = viewLifecycleOwner
            }
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
        fun addCL(){
            val addCL = LibraryAddListeners()
            addCL.fileTopBarAddCL(topBarBinding,binding.ancestorsBinding,libraryBaseViewModel)
            LibraryAddListeners().fragChildMultiBaseAddCL(
                binding,requireActivity(),
                libraryBaseViewModel,
                topBarBinding.imvSearchLoup,
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
            binding.frameLayAncestors.visibility = View.VISIBLE
        }
        fun makeAllUnSwipedObserver(recyclerView:RecyclerView) = Observer<Boolean>{
            if(it) LibrarySetUpItems().makeLibRVUnSwiped(recyclerView)
        }
        fun observeSwipe(){
            libraryBaseViewModel.apply {
                makeAllUnSwiped.observe(viewLifecycleOwner,makeAllUnSwipedObserver(recyclerView))
            }
        }

        fun observeMultiMode(){
            libraryBaseViewModel.apply {
                multipleSelectMode.observe(viewLifecycleOwner){
                    binding.topBarMultiselectBinding.root.visibility = if(it) View.VISIBLE else View.GONE
                    topBarBinding.root.visibility = if(!it) View.VISIBLE else View.GONE
                    LibrarySetUpItems().changeLibRVSelectBtnVisibility(recyclerView,it)
                }
                changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                    LibrarySetUpItems().changeLibRVAllSelectedState(recyclerView,it)
                }
                selectedItems.observe(viewLifecycleOwner){
                    binding.topBarMultiselectBinding.txvSelectingStatus.text =
                        resources.getString(R.string.topBarMultiSelectBin_selectingStatus,it.size)
                }
                multiMenuVisibility
                    .observe(viewLifecycleOwner, LibraryOb().multiMenuVisibilityObserver(binding))

            }

        }
        setUpLateInitVars()
        setUpView()
        addCL()
        observeSwipe()
        observeMultiMode()
        val searchModeObserver = LibraryOb().searchModeObserver(binding,searchViewModel)
        searchViewModel.searchModeActive.observe(viewLifecycleOwner,searchModeObserver)
        topBarBinding.imvFileType.setImageDrawable(AppCompatResources.getDrawable(requireActivity(),R.drawable.icon_flashcard))

        libraryBaseViewModel.apply {
            setLibraryFragment(LibraryFragment.FlashCardCover)
            clearFinalList()
            parentFileFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner){ it->
                setParentFile(it)
                createCardViewModel.setParentFlashCardCover(it)

            }
            parentFileAncestorsFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner){
                setParentFileAncestorsFromDB(it)
            }

            val emptyView = RvEmptyBinding.inflate(inflater,container,false).root
            topBarBinding.txvFileTitle.text = args.flashCardCoverId.single().toString()
            childCardsFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner) {
                setParentRVItems(it?: mutableListOf())
                adapter.submitList(it)
                if(it!=null){
                    createCardViewModel.setSisterCards(it)
                }
                if(it.isNullOrEmpty()){
                    binding.frameLayRvEmpty.addView(emptyView)
                } else {
                    binding.frameLayRvEmpty.removeView(emptyView)
                }
            }
            val commonViewSetUp = LibrarySetUpItems()
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility =if(it) View.VISIBLE else View.GONE
                topBarBinding.root.visibility = if(!it) View.VISIBLE else View.INVISIBLE
                commonViewSetUp.changeLibRVSelectBtnVisibility(recyclerView,it)
                commonViewSetUp.changeStringBtnVisibility(recyclerView,it)
                if(it.not()) changeViewVisibility(binding.frameLayMultiModeMenu,false)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) commonViewSetUp.makeLibRVUnSwiped(recyclerView)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                commonViewSetUp.changeLibRVAllSelectedState(recyclerView,it)
            }
            searchViewModel.matchedItems.observe(viewLifecycleOwner){
                searchAdapter.submitList(it)
            }
//            parentFileAncestors.observe(viewLifecycleOwner){
//
////                binding.ancestorsBinding.apply {
////                    txvGGrandParentFileTitle.text = it[2].title
////                    txvGrandParentFileTitle.text = it.title
////                    txvParentFileTitle.text = it.ParentFile?.title
////                    lineLayGGFile.visibility =
////                        if(it.gGrandPFile != null) View.VISIBLE else View.GONE
////                    lineLayGPFile.visibility =
////                        if(it.gParentFile != null) View.VISIBLE else View.GONE
////                    lineLayParentFile.visibility =
////                        if(it.ParentFile != null) View.VISIBLE else View.GONE
////                }
//            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = resources.getString(R.string.topBarMultiSelectBin_selectingStatus,it.size)
            }
        }
        return binding.root
    }





    override fun onDestroyView() {
        super.onDestroyView()
        libraryBaseViewModel.clearSelectedItems()
        _binding = null
    }

}