package com.example.tangochoupdated.ui.fragment.lib_frag_con

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.ui.viewmodel.customClasses.LibraryFragment
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBase
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.listadapter.LibFragSearchRVListAdapter
import com.example.tangochoupdated.ui.listener.topbar.LibFragTopBarInBoxCL
import com.example.tangochoupdated.ui.view_set_up.GetCustomDrawables
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpItems
import com.example.tangochoupdated.ui.viewmodel.*


class LibraryFragFolder :  Fragment(){
    private val args: LibraryFragFolderArgs by navArgs()

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var topBarBinding:LibraryFragTopBarFileBinding
    private lateinit var mainNavCon:NavController
    private lateinit var adapter:LibFragPlaneRVListAdapter
    private lateinit var searchAdapter: LibFragSearchRVListAdapter
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val stringCardViewModel: StringCardViewModel by activityViewModels()
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()

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
                context  = requireActivity(),
                stringCardViewModel  = stringCardViewModel,
                createCardViewModel  = createCardViewModel,
                mainNavController = mainNavCon,
                deletePopUpViewModel = deletePopUpViewModel,
                createFileViewModel = createFileViewModel,
                libraryViewModel = libraryViewModel)
            searchAdapter = LibFragSearchRVListAdapter(
                createFileViewModel,libNavCon,
                libraryViewModel,stringCardViewModel,createCardViewModel,searchViewModel,viewLifecycleOwner,deletePopUpViewModel,
                mainNavCon,
                requireActivity()
            )
        }
        fun addCL(){
            val addCL = LibraryAddListeners()
            addCL.fileTopBarAddCL(topBarBinding,binding.ancestorsBinding,libraryViewModel)
            LibraryAddListeners().fragChildMultiBaseAddCL(
                binding,requireActivity(),
                libraryViewModel,
                createCardViewModel,
                deletePopUpViewModel,
                searchViewModel,
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            )
        }
        fun setUpView(){
            val commonViewSetUp = LibrarySetUpItems()
            commonViewSetUp.setUpLibFragWithMultiModeBase(binding,topBarBinding.root,searchAdapter,adapter,requireActivity())
            binding.frameLayAncestors.visibility = View.VISIBLE
        }
        fun observeSwipe(){
            libraryViewModel.apply {
                rvCover.observe(viewLifecycleOwner){
                    binding.rvCover.visibility = if(it.visible) View.VISIBLE else View.GONE
                }
                leftSwipedItemExists.observe(viewLifecycleOwner){
                    setRVCover(LibraryViewModel.RvCover(it.not()))
                }
                makeAllUnSwiped.observe(viewLifecycleOwner){
                    if(it) LibrarySetUpItems().makeLibRVUnSwiped(recyclerView)
                }
            }
        }
        fun observeMultiMode(){
            libraryViewModel.apply {
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


        libraryViewModel.apply {
            clearFinalList()
            setLibraryFragment(LibraryFragment.Folder)
            parentFileFromDB(args.folderId.single()).observe(viewLifecycleOwner){
                setParentFileFromDB(it)
                createFileViewModel.setParentFile(it)
                topBarBinding.apply {
                    txvFileTitle.text = it?.title ?:"タイトルなし"
                    imvFileType.setImageDrawable(
                        GetCustomDrawables(requireActivity()).getFolderIconByCol(it?.colorStatus ?:ColorStatus.GRAY,)
                    )
                }
            }
            val emptyView = LibraryFragLayFolderRvEmptyBinding.inflate(inflater,container,false).root
            childFilesFromDB(args.folderId.single()).observe(viewLifecycleOwner) {
                setParentRVItems(it)
                adapter.submitList(it)
                if(it.isNullOrEmpty()){
                    binding.frameLayRvEmpty.removeView(emptyView)
                    binding.frameLayRvEmpty.addView(emptyView)
                } else {
                    binding.frameLayRvEmpty.removeAllViews()
                }
            }

            parentFileAncestorsFromDB(args.folderId.single()).observe(viewLifecycleOwner){
                setParentFileAncestorsFromDB(it)
                createFileViewModel.filterBottomMenuByAncestors(it,it[0])
            }
            fun setUpEachAncestor(linLay:LinearLayoutCompat,txv:TextView, imv:ImageView, file: File?){
                val getDraw =  GetCustomDrawables(requireActivity())
                if(file==null)linLay.visibility = View.GONE
                else {
                    linLay.visibility = View.VISIBLE
                    txv.text = file.title
                    imv.setImageDrawable(getDraw.getFileIconByFile(file))
                }
            }
            parentFileAncestors.observe(viewLifecycleOwner){
                    binding.ancestorsBinding.apply {
                        setUpEachAncestor(lineLayGGFile,txvGGrandParentFileTitle,imvGGrandParentFile,it.gGrandPFile)
                        setUpEachAncestor(lineLayGPFile,txvGrandParentFileTitle,imvGrandParentFile,it.gParentFile)
                        setUpEachAncestor(lineLayParentFile,txvParentFileTitle,imvParentFile,it.ParentFile)
                    }
                }
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
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }

            }
//        LibrarySetUpFragment(libraryViewModel,deletePopUpViewModel).setUpFragLibFolder(binding,myNavCon,requireActivity())
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                if(!libraryViewModel.checkViewReset())
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

