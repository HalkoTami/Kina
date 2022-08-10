package com.example.tangochoupdated.ui.library.fragment

import LibraryFragFileClickListener
import LibraryPopUpConfirmDeleteClickListener
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragHomeBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragOpenFolderBaseBinding
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryTopBarMode
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.mainactivity.Animation
import com.example.tangochoupdated.ui.mainactivity.MainActivity


class LibraryFragFolder :  Fragment(){
    private val args: LibraryFragFolderArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter: LibraryListAdapter
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
        adapter = LibraryListAdapter(createFileViewModel,createCardViewModel,libraryViewModel, requireActivity())
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
                        MainActivity().changeFileIconCol(it?.colorStatus ?:ColorStatus.GRAY,requireActivity())
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
            }
            myFinalList.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding.emptyBinding.root.visibility =
                    if (it.isEmpty()) View.VISIBLE else View.GONE
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
        addTopBarViews()
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addTopBarViews(){

        val file = binding.topBarFileBinding
        val multi = binding.topBarMultiselectBinding
        arrayOf(
            file.imvGoBack,
            file.lineLayGGFile,
            file.lineLayGPFile,

            multi.imvCloseMultiMode,
            multi.imvSelectAll,
            multi.imvChangeMenuVisibility,
            multi.multiSelectMenuBinding.imvMoveSelectedItems,
            multi.multiSelectMenuBinding.imvDeleteSelectedItems,
            multi.multiSelectMenuBinding.imvSetFlagToSelectedItems,

        ).onEach { it.setOnClickListener( LibraryFragFileClickListener(requireContext(), binding.topBarFileBinding,libraryViewModel, myNavCon)) }
    }

}

