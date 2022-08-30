package com.example.tangochoupdated.ui.fragment.lib_frag_con

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
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
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpFragment
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBase
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.view_set_up.GetCustomDrawables
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.viewmodel.*


class LibraryFragFolder :  Fragment(){
    private val args: LibraryFragFolderArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
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
        myNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.lib_frag_con_view).findNavController()
        _binding = LibraryChildFragWithMulModeBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        val adapter= LibFragPlaneRVListAdapter(
            createFileViewModel  = createFileViewModel,
            libraryViewModel  = libraryViewModel,
            context  = requireActivity(),
            stringCardViewModel  = stringCardViewModel,
            createCardViewModel  = createCardViewModel,deletePopUpViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false

        val topBarBinding = LibraryFragTopBarFileBinding.inflate(inflater,container,false)
        val addListeners = LibraryAddListeners(libraryViewModel,deletePopUpViewModel)
        addListeners.fileTopBarAddCL(topBarBinding,binding.ancestorsBinding,requireActivity(),myNavCon)
        binding.frameLayTopBar.addView(topBarBinding.root)

        libraryViewModel.apply {
            clearFinalList()
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
                if(it.isNullOrEmpty().not()){
                    binding.mainFrameLayout.addView(emptyView)
                } else {
                    binding.mainFrameLayout.removeView(emptyView)
                }
            }

            parentFileAncestorsFromDB(args.folderId.single()).observe(viewLifecycleOwner){
                setParentFileAncestorsFromDB(it)
                createFileViewModel.filterBottomMenuByAncestors(it,returnParentFile()!!)
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
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility =if(it) View.VISIBLE else View.GONE
                topBarBinding.root.visibility = if(!it) View.VISIBLE else View.INVISIBLE
                LibraryFragmentBase().changeLibRVSelectBtnVisibility(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) LibraryFragmentBase().makeLibRVUnSwiped(recyclerView)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                LibraryFragmentBase().changeLibRVAllSelectedState(recyclerView,it)
            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
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

