package com.korokoro.kina.ui.fragment.lib_frag_con

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.*
import com.korokoro.kina.actions.changeViewVisibility
import com.korokoro.kina.databinding.LibraryChildFragWithMulModeBaseBinding
import com.korokoro.kina.databinding.LibraryFragLayFlashCardCoverRvEmptyBinding
import com.korokoro.kina.databinding.LibraryFragTopBarFileBinding
import com.korokoro.kina.db.enumclass.ColorStatus
import com.korokoro.kina.ui.customClasses.LibraryFragment
import com.korokoro.kina.ui.listadapter.LibFragPlaneRVListAdapter
import com.korokoro.kina.ui.listadapter.LibFragSearchRVListAdapter
import com.korokoro.kina.ui.listener.recyclerview.LibraryRVItemClickListener
import com.korokoro.kina.ui.observer.CommonOb
import com.korokoro.kina.ui.observer.LibraryOb
import com.korokoro.kina.ui.view_set_up.GetCustomDrawables
import com.korokoro.kina.ui.view_set_up.LibraryAddListeners
import com.korokoro.kina.ui.view_set_up.LibrarySetUpItems
import com.korokoro.kina.ui.viewmodel.*


class LibraryFlashCardCoverFrag  : Fragment(){
    private val args: LibraryFlashCardCoverFragArgs by navArgs()

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var topBarBinding:LibraryFragTopBarFileBinding
    private lateinit var mainNavCon:NavController
    private lateinit var adapter:LibFragPlaneRVListAdapter
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
        fun addCL(){
            val addCL = LibraryAddListeners()
            addCL.fileTopBarAddCL(topBarBinding,binding.ancestorsBinding,libraryBaseViewModel)
            LibraryAddListeners().fragChildMultiBaseAddCL(
                binding,requireActivity(),
                libraryBaseViewModel,
                createCardViewModel,
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

        fun observeSwipe(){
            libraryBaseViewModel.apply {
                rvCover.observe(viewLifecycleOwner,CommonOb().rvCoverObserver(binding.rvCover))
                leftSwipedItemExists.observe(viewLifecycleOwner,CommonOb().leftSwipedItemExistsObserver(this))
                makeAllUnSwiped.observe(viewLifecycleOwner,CommonOb().makeAllUnSwipedObserver(recyclerView))
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
                    binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
                }

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
            parentFileFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner){
                setParentFileFromDB(it)
                topBarBinding.txvFileTitle.text = it.title ?:"タイトルなし"
                topBarBinding.imvFileType.setImageDrawable(
                    GetCustomDrawables(requireContext()).getFlashCardIconByCol(it?.colorStatus ?:ColorStatus.GRAY,)
                )
                createCardViewModel.setParentFlashCardCover(it)

            }
            parentFileAncestorsFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner){
                setParentFileAncestorsFromDB(it)
                editFileViewModel.filterBottomMenuByAncestors(it,returnParentFile() ?:return@observe)
            }
            val emptyView = LibraryFragLayFlashCardCoverRvEmptyBinding.inflate(inflater,container,false).root
            topBarBinding.txvFileTitle.text = args.flashCardCoverId.single().toString()
            childCardsFromDB(args.flashCardCoverId.single()).observe(viewLifecycleOwner) {
                it?.sortedBy { it.libOrder }
                val sorted = it
                setParentRVItems(sorted ?: mutableListOf())
                adapter.submitList(sorted)
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
            parentFileAncestors.observe(viewLifecycleOwner){

                binding.ancestorsBinding.apply {
                    txvGGrandParentFileTitle.text = it.gGrandPFile?.title
                    txvGrandParentFileTitle.text = it.gParentFile?.title
                    txvParentFileTitle.text = it.ParentFile?.title
                    lineLayGGFile.visibility =
                        if(it.gGrandPFile != null) View.VISIBLE else View.GONE
                    lineLayGPFile.visibility =
                        if(it.gParentFile != null) View.VISIBLE else View.GONE
                    lineLayParentFile.visibility =
                        if(it.ParentFile != null) View.VISIBLE else View.GONE
                }
            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }
        }
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
        libraryBaseViewModel.clearSelectedItems()
        _binding = null
    }

}