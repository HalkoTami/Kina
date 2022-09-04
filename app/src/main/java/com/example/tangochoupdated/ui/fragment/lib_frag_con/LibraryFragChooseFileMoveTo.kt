package com.example.tangochoupdated.ui.fragment.lib_frag_con

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpFragment
import com.example.tangochoupdated.ui.listadapter.LibFragChooseFileRVListAdapter
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.viewmodel.*


class LibraryFragChooseFileMoveTo  : Fragment(){
    private val args: LibraryFragChooseFileMoveToArgs by navArgs()

    private lateinit var libNavCon:NavController
    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val deletePopUpViewModel:DeletePopUpViewModel by activityViewModels()
    private val chooseFileMoveToViewModel:ChooseFileMoveToViewModel by activityViewModels()

    private var _binding: LibraryChildFragWithMulModeBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        myNavCon =  requireActivity().findViewById<FragmentContainerView>(R.id.lib_frag_con_view).findNavController()
        libNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.lib_frag_con_view).findNavController()
        _binding = LibraryChildFragWithMulModeBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        val adapter = LibFragChooseFileRVListAdapter(createFileViewModel,libraryViewModel,requireActivity(),
            deletePopUpViewModel,libNavCon,chooseFileMoveToViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false
        val topBarBinding = LibraryFragTopBarChooseFileMoveToBinding.inflate(inflater,container,false)
        val addListeners = LibraryAddListeners(libraryViewModel,deletePopUpViewModel,libNavCon)
        addListeners.fragChooseFileMoveToTopBarAddCL(topBarBinding,requireActivity(),libNavCon)
        binding.frameLayTopBar.addView(topBarBinding.root)
        addListeners.confirmMoveToFilePopUpAddCL(binding.confirmMoveToBinding,chooseFileMoveToViewModel)
        chooseFileMoveToViewModel.apply {
            popUpVisible.observe(viewLifecycleOwner){
                binding.frameLayConfirmMove.visibility = if(it) View.VISIBLE else View.GONE
            }
            setMovingItems(libraryViewModel.returnSelectedItems())

        }
        libraryViewModel.apply {
            val flashcard = returnParentFile()?.fileStatus == FileStatus.TANGO_CHO_COVER ||
                    returnModeInBox()==true
            if(args.fileId != null){
                parentFileFromDB(args.fileId?.single()).observe(viewLifecycleOwner){
                    setParentFileFromDB(it)
                }
            }
            val emptyView = LibraryFragLayFlashCardCoverRvEmptyBinding.inflate(inflater,container,false).root
            childFilesFromDB(args.fileId?.single()).observe(viewLifecycleOwner) {
                setParentRVItems(it)
                val list = if(flashcard){
                    it.filter { it.fileStatus == FileStatus.TANGO_CHO_COVER }
                } else it.filter { it.fileStatus == FileStatus.FOLDER && returnSelectedItems().contains(it).not()}
                adapter.submitList(list)
                if(list.isNullOrEmpty()){
                    binding.frameLayRvEmpty.addView(emptyView)
                } else {
                    binding.frameLayRvEmpty.removeView(emptyView)
                }

            }
            parentFileAncestorsFromDB(args.fileId?.single()).observe(viewLifecycleOwner){
                createFileViewModel.filterBottomMenuWhenInChooseFileMoveTo(
                    flashCard = flashcard, args.fileId == null,it
                )
            }

            topBarBinding.imvFileMoveToStatus.setImageDrawable(
                AppCompatResources.getDrawable(requireActivity(),
                    if(flashcard) R.drawable.icon_move_to_flashcard_cover else R.drawable.icon_move_to_folder
                )
            )
            topBarBinding.txvChooseFileMoveTo.text =
                    if(flashcard) "${returnSelectedItems().size} 個のアイテムを単語帳に移動" else
                        "${returnSelectedItems().size}個のアイテムをフォルダに移動"

            chooseFileMoveToMode.observe(viewLifecycleOwner){
                if(it == false){
                    setMultipleSelectMode(false)
                    myNavCon.popBackStack()
                }
            }
        }

//        LibrarySetUpFragment(libraryViewModel,deletePopUpViewModel).setUpFragLibChooseFileMoveTo(binding,myNavCon,requireActivity())

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

