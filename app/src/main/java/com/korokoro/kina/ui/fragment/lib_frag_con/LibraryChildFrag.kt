package com.korokoro.kina.ui.fragment.lib_frag_con

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.*
import com.korokoro.kina.actions.*
import com.korokoro.kina.databinding.*
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.ColorStatus
import com.korokoro.kina.customClasses.enumClasses.LibraryFragment
import com.korokoro.kina.ui.listadapter.LibFragPlaneRVListAdapter
import com.korokoro.kina.ui.listadapter.LibFragSearchRVListAdapter
import com.korokoro.kina.ui.listener.recyclerview.LibraryRVItemClickListener
import com.korokoro.kina.ui.view_set_up.GetCustomDrawables
import com.korokoro.kina.ui.view_set_up.LibrarySetUpItems
import com.korokoro.kina.ui.viewmodel.*


class LibraryChildFrag :  Fragment(){
    private val args: LibraryChildFragArgs by navArgs()

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var mainNavCon:NavController
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val cardTypeStringViewModel: CardTypeStringViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryBaseViewModel: LibraryBaseViewModel by activityViewModels()
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: LibraryChildFragWithMulModeBaseBinding? = null
    private val binding get() = _binding!!
    private var searchLoup:ImageView? = null
    private var _fileTopBarBinding:LibraryFragTopBarFileBinding ? =null
    private val fileTopBarBinding get() =_fileTopBarBinding!!
    private var _homeTopBarBinding:LibraryFragTopBarHomeBinding ? =null
    private val homeTopBarBinding get() =_homeTopBarBinding!!
    private var _inboxTopBarBinding:LibraryFragTopBarInboxBinding ? =null
    private val inboxTopBarBinding get() =_inboxTopBarBinding!!
    private var _chooseFileMoveToTopBarBinding:LibraryFragTopBarChooseFileMoveToBinding ? =null
    private val chooseFileMoveToTopBarBinding get() =_chooseFileMoveToTopBarBinding!!
    private var _topBarView:View? =null
    private val topBarView get() = _topBarView!!

    fun setUpPlaneRVListAdapter():LibFragPlaneRVListAdapter{
        val adapter =  LibFragPlaneRVListAdapter(
            stringCardViewModel  = cardTypeStringViewModel,
            createCardViewModel  = createCardViewModel,
            mainNavController = mainNavCon,
            deletePopUpViewModel = deletePopUpViewModel,
            createFileViewModel = editFileViewModel,
            libraryViewModel = libraryBaseViewModel,
        )
        val mainRV = binding.vocabCardRV
        mainRV.adapter = adapter
        mainRV.layoutManager = LinearLayoutManager(context)
        mainRV.isNestedScrollingEnabled = true
        return adapter

    }
    fun getSearchRVListAdapter():LibFragSearchRVListAdapter{
        return  LibFragSearchRVListAdapter(
            libraryViewModel = libraryBaseViewModel,
            stringCardViewModel = cardTypeStringViewModel,
            createCardViewModel = createCardViewModel,
            searchViewModel = searchViewModel,
            lifecycleOwner = viewLifecycleOwner,
            mainNavController = mainNavCon,
            context = requireActivity(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fun setUpFileTopBarView():LibraryFragTopBarFileBinding{
            _fileTopBarBinding = LibraryFragTopBarFileBinding.inflate(inflater,container,false)
            setClickListeners(arrayOf(
                fileTopBarBinding.imvGoBack,
                binding.ancestorsBinding.lineLayGPFile,
                binding.ancestorsBinding.lineLayGGFile),LibFragTopBarFileCL())
            searchLoup = fileTopBarBinding.imvSearchLoupe
            changeViewVisibility(binding.frameLayAncestors,true)
            return fileTopBarBinding
        }
        fun addTopBar(args:LibraryFragment){
            _topBarView =  when(args){
                LibraryFragment.Folder,LibraryFragment.FlashCardCover-> setUpFileTopBarView().root
                LibraryFragment.Home -> homeTopBarBinding.root
                LibraryFragment.ChooseFileMoveTo -> chooseFileMoveToTopBarBinding.root
                LibraryFragment.InBox   -> inboxTopBarBinding.root
            }
            binding.frameLayTopBar.addView(
               topBarView
            )

        }

        fun addMultiTopBarCL(){
            setClickListeners(arrayOf(
                binding.topBarMultiselectBinding.imvCloseMultiMode,
                binding.topBarMultiselectBinding.imvSelectAll,
                binding.topBarMultiselectBinding.imvChangeMenuVisibility,
                binding.multiSelectMenuBinding.linLayMoveSelectedItems,
                binding.multiSelectMenuBinding.linLayDeleteSelectedItems,),LibFragTopBarMultiModeCL())
        }
        fun searchEditTextAddListener(){
            binding.bindingSearch.edtLibrarySearch.addTextChangedListener {
                val textIsEmpty = it.toString()==""
                changeViewVisibility(binding.mainFrameLayout,textIsEmpty)
                changeViewVisibility(binding.frameLaySearchRv,textIsEmpty.not())
                if(textIsEmpty.not())  searchViewModel.setSearchText(it.toString())
            }
        }
        fun searchListeners(){
            val searchLoup = searchLoup
            if(searchLoup!=null)
                setClickListeners(arrayOf(searchLoup,binding.bindingSearch.txvCancel),LibFragSearchBarCL())
            searchEditTextAddListener()
        }
        fun setUpLateInitVars(){
            _binding = LibraryChildFragWithMulModeBaseBinding.inflate(inflater, container, false)
            libNavCon =  requireActivity().findNavController(R.id.lib_frag_con_view)
            recyclerView = binding.vocabCardRV
            mainNavCon = mainViewModel.returnMainActivityNavCon() ?:return

        }
        fun setUpView(){
            addTopBar(args.fragType)
            addMultiTopBarCL()
            searchListeners()
            binding.libChildFragBackground.setOnClickListener {
                libraryBaseViewModel.setMultiMenuVisibility(false)
            }
            recyclerView.addOnItemTouchListener(
                object : LibraryRVItemClickListener(requireActivity(),binding.frameLayTest,recyclerView,libraryBaseViewModel){})
        }
        setUpLateInitVars()
        setUpView()
        val allUnSwipedObserver = Observer<Boolean>{
            if(it) LibrarySetUpItems().makeLibRVUnSwiped(recyclerView)
        }
        val multiSelectModeObserver = Observer<Boolean> {
            changeViewVisibility(binding.topBarMultiselectBinding.root,it)
            changeViewVisibility(topBarView,it.not())
            LibrarySetUpItems().changeLibRVSelectBtnVisibility(recyclerView,it)
            if(it.not()) changeViewVisibility(binding.frameLayMultiModeMenu,false)
        }
        val changeAllRVSelectedStatusObserver = Observer<Boolean> {
            LibrarySetUpItems().changeLibRVAllSelectedState(recyclerView,it)
        }
        val allItemSelectedObserver = Observer<Boolean> {
            binding.topBarMultiselectBinding.imvSelectAll.isSelected = it
        }
        val selectedItemObserver = Observer<List<Any>> {
            binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
        }
        val multiMenuVisibilityObserver =  Observer<Boolean>{ visible->
            arrayOf(binding.frameLayMultiModeMenu,binding.libChildFragBackground).onEach {
                changeViewVisibility(it,visible)
            }
        }
        val matchedItemObserver = Observer<List<Any>> {
            getSearchRVListAdapter().submitList(it)
        }
        searchViewModel.matchedItems.observe(viewLifecycleOwner,matchedItemObserver)
        val emptyView = RvEmptyBinding.inflate(inflater,container,false).root
        val searchModeObserver =  Observer<Boolean>{
            binding.apply {
                if(it) {
                    changeViewVisibility(frameLaySearchBar,true)
                    bindingSearch.edtLibrarySearch.text = SpannableStringBuilder(searchViewModel.returnSearchText())
                } else {
                    changeViewVisibility(frameLaySearchBar,false)
                    bindingSearch.edtLibrarySearch.text = SpannableStringBuilder("")
                }
            }
        }
        val fileRVItemsObserver = Observer<List<File>>{
            if(args.fragType==LibraryFragment.Folder||
                    args.fragType == LibraryFragment.Home){
                libraryBaseViewModel.setParentRVItems(it)
                setUpPlaneRVListAdapter().submitList(it)
                changeViewIfRVEmpty(it,binding.frameLayRvEmpty,emptyView)
            }
        }
        val parentFileObserver = Observer<File> {
            when(args.fragType){
                LibraryFragment.Folder,LibraryFragment.FlashCardCover-> {
                    libraryBaseViewModel.setParentFile(it)
                    editFileViewModel.setParentTokenFileParent(it)
                    fileTopBarBinding.apply {
                        txvFileTitle.text = it?.title ?:"タイトルなし"
                        imvFileType.setImageDrawable(
                            GetCustomDrawables(requireActivity()).getFileIconByFile(it)
                        )
                    }
                }
                LibraryFragment.ChooseFileMoveTo -> {
                    libraryBaseViewModel.setParentFile(it)
                }
                else -> return@Observer
            }
        }
        fun setUpEachAncestor(linLay:LinearLayoutCompat,txv:TextView, imv:ImageView, file: File){
            val getDraw =  GetCustomDrawables(requireActivity())
            linLay.visibility = View.VISIBLE
            txv.text = file.title
            imv.setImageDrawable(getDraw.getFileIconByFile(file))
        }
        val parentFileAncestorsObserver = Observer<List<File>> {
            if(it.isEmpty().not()){
                libraryBaseViewModel.setParentFileAncestorsFromDB(it)
                editFileViewModel.filterBottomMenuByAncestors(it,it[0])
                binding.ancestorsBinding.apply {
                    if(it.size>2)setUpEachAncestor(lineLayGGFile,txvGGrandParentFileTitle,imvGGrandParentFile,it[2]) else changeViewVisibility(lineLayGGFile,false)
                    if(it.size>1)setUpEachAncestor(lineLayGPFile,txvGrandParentFileTitle,imvGrandParentFile,it[1])  else changeViewVisibility(lineLayGPFile,false)
                    setUpEachAncestor(lineLayParentFile,txvParentFileTitle,imvParentFile,it[0])
                }
            }

        }
        libraryBaseViewModel.clearFinalList()
        libraryBaseViewModel.setLibraryNavCon(libNavCon)
        libraryBaseViewModel.makeAllUnSwiped.observe(viewLifecycleOwner,allUnSwipedObserver)
        libraryBaseViewModel.multipleSelectMode.observe(viewLifecycleOwner,multiSelectModeObserver)
        libraryBaseViewModel.changeAllRVSelectedStatus.observe(viewLifecycleOwner,changeAllRVSelectedStatusObserver)
        libraryBaseViewModel.allRVItemSelected.observe(viewLifecycleOwner,allItemSelectedObserver)
        libraryBaseViewModel.selectedItems.observe(viewLifecycleOwner,selectedItemObserver)
        libraryBaseViewModel.multiMenuVisibility.observe(viewLifecycleOwner,multiMenuVisibilityObserver)
        libraryBaseViewModel.parentFileFromDB(args.fileId.single()).observe(viewLifecycleOwner,parentFileObserver)
        libraryBaseViewModel.childFilesFromDB(args.fileId.single()).observe(viewLifecycleOwner,fileRVItemsObserver)
        libraryBaseViewModel.parentFileAncestorsFromDB(args.fileId.single()).observe(viewLifecycleOwner,parentFileAncestorsObserver)
        searchViewModel.searchModeActive.observe(viewLifecycleOwner,searchModeObserver)


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    inner class LibFragTopBarFileCL(): View.OnClickListener{
        override fun onClick(v: View?) {
            val popAmount = when(v){
                fileTopBarBinding.imvGoBack -> 1
                binding.ancestorsBinding.lineLayGPFile -> 1
                binding.ancestorsBinding.lineLayGGFile ->2
                else -> 0
            }
            NavigationActions().popMultipleBackStack(libNavCon, popAmount)
        }

    }
    inner class LibFragTopBarMultiModeCL: View.OnClickListener{
        private val topBar = binding.topBarMultiselectBinding
        private val menu = binding.multiSelectMenuBinding
        override fun onClick(v: View?) {
            when(v){
                topBar.imvCloseMultiMode        -> libraryBaseViewModel.setMultipleSelectMode(false)
                topBar.imvSelectAll             -> libraryBaseViewModel.changeAllRVSelectedStatus(libraryBaseViewModel.getAllRVItemSelected().not())
                topBar.imvChangeMenuVisibility  -> libraryBaseViewModel.setMultiMenuVisibility(libraryBaseViewModel.returnMultiMenuVisibility().not())
                menu.linLayMoveSelectedItems    -> libraryBaseViewModel.openChooseFileMoveTo(null)
                menu.linLayDeleteSelectedItems  -> deletePopUpViewModel.onCLickMultiMenuDelete(libraryBaseViewModel.returnSelectedItems())
            }
        }
    }
    inner class LibFragSearchBarCL: View.OnClickListener{
            private val searchBinding = binding.bindingSearch
            override fun onClick(v: View?) {
            searchBinding.apply {
                when(v){
                    searchLoup -> searchViewModel.onClickSearchLoup(edtLibrarySearch,requireActivity())
                    txvCancel -> searchViewModel.onClickCancel(edtLibrarySearch,requireActivity())
                }
            }
        }
    }
}

