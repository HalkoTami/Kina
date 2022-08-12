package com.example.tangochoupdated.ui.library.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.LibraryFragOpenFolderBaseBinding
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryAddClickListeners
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.library.listadapter.LibFragFileRVListAdapter


class LibraryFragFolder :  Fragment(){
    private val args: LibraryFragFolderArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView

    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()

    private var _binding: LibraryFragOpenFolderBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.lib_frag_con_view).findNavController()
        _binding = LibraryFragOpenFolderBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        val adapter = LibFragFileRVListAdapter(createFileViewModel,libraryViewModel,requireActivity(),false )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false


        libraryViewModel.apply {
            clearFinalList()
            parentFileFromDB(args.folderId.single()).observe(viewLifecycleOwner){
                setParentFileFromDB(it)
                binding.topBarFileBinding.apply {
                    txvFileTitle.text = it?.title ?:"タイトルなし"
                    imvFileType.setImageDrawable(
                        GetCustomDrawables().getFileIconByCol(it?.colorStatus ?:ColorStatus.GRAY,requireActivity())
                    )
                }
            }
            childFilesFromDB(args.folderId.single()).observe(viewLifecycleOwner) {
                val childFoldersAmount = it.filter { it.fileStatus == FileStatus.FOLDER }.size
                val childFlashCardCoverAmount = it.filter { it.fileStatus == FileStatus.TANGO_CHO_COVER }.size
                if(returnParentFile()?.childFoldersAmount!=childFoldersAmount){
                    upDateContainingCardAmount(childFoldersAmount)
                }
                if(returnParentFile()?.childFlashCardCoversAmount!=childFlashCardCoverAmount){
                    upDateContainingFlashCardAmount(childFlashCardCoverAmount)
                }
                setChildFilesFromDB(it)
                adapter.submitList(it)
                binding.emptyBinding.root.visibility =
                    if (it.isEmpty()) View.VISIBLE else View.GONE
            }
            myFinalList.observe(viewLifecycleOwner) {

            }
            parentFileAncestorsFromDB(args.folderId.single()).observe(viewLifecycleOwner){
                setParentFileAncestorsFromDB(it)
            }
            parentFileAncestors.observe(viewLifecycleOwner){
                    binding.topBarFileBinding.apply {
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
                LibraryFragmentBase().changeLibRVSelectBtnVisibility(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) LibraryFragmentBase().makeLibRVUnSwiped(recyclerView)
            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }

            }
        LibraryAddClickListeners().fragLibFolderAddCL(binding,libraryViewModel,myNavCon,requireActivity())
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

