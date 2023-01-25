package com.koronnu.kina.ui.tabLibrary.folder

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.*
import com.koronnu.kina.actions.changeViewIfRVEmpty
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.*
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.source.local.entity.enumclass.ColorStatus
import com.koronnu.kina.data.model.enumClasses.LibraryFragment
import com.koronnu.kina.ui.EditFileViewModel
import com.koronnu.kina.ui.MainViewModel
import com.koronnu.kina.ui.editCard.CreateCardViewModel
import com.koronnu.kina.ui.editCard.editCardContent.stringCard.CardTypeStringViewModel
import com.koronnu.kina.util.LibraryOb
import com.koronnu.kina.ui.tabLibrary.*
import com.koronnu.kina.util.view_set_up.DrawableConverter
import com.koronnu.kina.util.view_set_up.LibraryAddListeners
import com.koronnu.kina.util.view_set_up.LibrarySetUpItems
import com.koronnu.kina.ui.viewmodel.*


class LibraryFolderFrag :  Fragment(){
    private val args: com.koronnu.kina.ui.tabLibrary.folder.LibraryFolderFragArgs by navArgs()

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var topBarBinding:LibraryFragTopBarFileBinding
    private lateinit var mainNavCon:NavController
    private lateinit var adapter: LibFragPlaneRVListAdapter
    private lateinit var searchAdapter: LibFragSearchRVListAdapter
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val cardTypeStringViewModel: CardTypeStringViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()
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
            topBarBinding = LibraryFragTopBarFileBinding.inflate(inflater,container,false)
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
                    binding.topBarMultiselectBinding.txvSelectingStatus.text =  resources.getString(R.string.topBarMultiSelectBin_selectingStatus,it.size)
                }
                multiMenuVisibility.observe(viewLifecycleOwner, LibraryOb()
                    .multiMenuVisibilityObserver(binding))

            }

        }
        setUpLateInitVars()
        val emptyView = RvEmptyBinding.inflate(inflater,container,false).root
        val searchModeObserver = LibraryOb().searchModeObserver(binding,searchViewModel)
        val fileRVItemsObserver = Observer<List<File>?>{
            val sorted  = it
            libraryBaseViewModel.setParentRVItems(sorted)
            if( adapter.currentList.size == it.size) adapter.submitList(null)
            adapter.submitList(it)
            changeViewIfRVEmpty(it,binding.frameLayRvEmpty,emptyView)
        }

        setUpView()
        addCL()
        observeSwipe()
        observeMultiMode()

        searchViewModel.searchModeActive.observe(viewLifecycleOwner,searchModeObserver)
        searchViewModel.matchedItems.observe(viewLifecycleOwner){
            searchAdapter.submitList(it)
        }
        libraryBaseViewModel.apply {
            clearFinalList()
            setLibraryFragment(LibraryFragment.Folder)
            parentFileFromDB(args.folderId.single()).observe(viewLifecycleOwner){
                setParentFile(it)
                topBarBinding.apply {
                    txvFileTitle.text = it?.title ?:resources.getString(R.string.no_title)
                    imvFileType.setImageDrawable(
                        DrawableConverter(requireActivity()).getFolderIconByCol(it?.colorStatus ?: ColorStatus.GRAY,)
                    )
                }
            }
            childFilesFromDB(args.folderId.single()).observe(viewLifecycleOwner,fileRVItemsObserver)

            parentFileAncestorsFromDB(args.folderId.single()).observe(viewLifecycleOwner){
                setParentFileAncestorsFromDB(it)
            }
//            fun setUpEachAncestor(linLay:LinearLayoutCompat,txv:TextView, imv:ImageView, file: File?){
//                val getDraw =  DrawableConverter(requireActivity())
//                if(file==null)linLay.visibility = View.GONE
//                else {
//                    linLay.visibility = View.VISIBLE
//                    txv.text = file.title
//                    imv.setImageDrawable(getDraw.getFileIconByFile(file))
//                }
//            }
//            parentFileAncestors.observe(viewLifecycleOwner){
//                    binding.ancestorsBinding.apply {
//                        setUpEachAncestor(lineLayGGFile,txvGGrandParentFileTitle,imvGGrandParentFile,it.gGrandPFile)
//                        setUpEachAncestor(lineLayGPFile,txvGrandParentFileTitle,imvGrandParentFile,it.gParentFile)
//                        setUpEachAncestor(lineLayParentFile,txvParentFileTitle,imvParentFile,it.ParentFile)
//                    }
//                }
            val commonViewSetUp = LibrarySetUpItems()
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility =if(it) View.VISIBLE else View.GONE
                topBarBinding.root.visibility = if(!it) View.VISIBLE else View.INVISIBLE
                commonViewSetUp.changeLibRVSelectBtnVisibility(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) commonViewSetUp.makeLibRVUnSwiped(recyclerView)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                commonViewSetUp.changeLibRVAllSelectedState(recyclerView,it)
            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text =  resources.getString(R.string.topBarMultiSelectBin_selectingStatus,it.size)
            }

            }
//        LibrarySetUpFragment(libraryViewModel,deletePopUpViewModel).setUpFragLibFolder(binding,myNavCon,requireActivity())
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

