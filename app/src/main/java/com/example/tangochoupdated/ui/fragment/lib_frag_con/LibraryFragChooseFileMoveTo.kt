package com.example.tangochoupdated.ui.fragment.lib_frag_con

import android.content.Context
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
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.viewmodel.customClasses.LibraryFragment
import com.example.tangochoupdated.ui.listadapter.LibFragChooseFileRVListAdapter
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.listadapter.LibFragSearchRVListAdapter
import com.example.tangochoupdated.ui.listener.popUp.LibFragPopUpConfirmMoveToFileCL
import com.example.tangochoupdated.ui.listener.topbar.LibFragTopBarChooseFileMoveToCL
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpItems
import com.example.tangochoupdated.ui.viewmodel.*


class LibraryFragChooseFileMoveTo  : Fragment(){
    private val args: LibraryFragChooseFileMoveToArgs by navArgs()

    private lateinit var adapter:LibFragChooseFileRVListAdapter
    private lateinit var topBarBinding:LibraryFragTopBarChooseFileMoveToBinding
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

        fun setUpLateInitVars(){
            topBarBinding =  LibraryFragTopBarChooseFileMoveToBinding.inflate(inflater,container,false)
            libNavCon =  requireActivity().findNavController(R.id.lib_frag_con_view)
            _binding = LibraryChildFragWithMulModeBaseBinding.inflate(inflater, container, false)
            recyclerView = binding.vocabCardRV
            adapter =   LibFragChooseFileRVListAdapter(requireActivity(),chooseFileMoveToViewModel,libraryViewModel)
        }

        fun setUpView(){
            val mainRV = binding.vocabCardRV
            mainRV.adapter = adapter
            mainRV.layoutManager = LinearLayoutManager(requireActivity())
            mainRV.isNestedScrollingEnabled = true

            binding.frameLayTopBar.addView(topBarBinding.root)
        }

        fun addCL(){
            fun topBarAddCL(){
                arrayOf(
                    topBarBinding.imvCloseChooseFileMoveTo,
                ).onEach { it.setOnClickListener( LibFragTopBarChooseFileMoveToCL(topBarBinding,libraryViewModel,)) }
            }
            fun confirmMoveToFilePopUpAddCL(){
                val popUp = binding.confirmMoveToBinding
                arrayOf(
                    popUp.btnCancelMove,
                    popUp.btnCommitMove,
                    popUp.btnCloseConfirmMove
                )   .onEach {
                    it.setOnClickListener(LibFragPopUpConfirmMoveToFileCL(popUp, chooseFileMoveToViewModel,libNavCon))
                }
            }
            topBarAddCL()
            confirmMoveToFilePopUpAddCL()
        }
        setUpLateInitVars()
        setUpView()
        addCL()


        chooseFileMoveToViewModel.apply {
            popUpVisible.observe(viewLifecycleOwner){
                binding.frameLayConfirmMove.visibility = if(it) View.VISIBLE else View.GONE
            }
            setMovingItems(libraryViewModel.returnSelectedItems())
            libraryViewModel.clearSelectedItems()
            popUpText.observe(viewLifecycleOwner){
                binding.confirmMoveToBinding.txvConfirmMove.text = it
            }


        }
        libraryViewModel.apply {
            setLibraryFragment(LibraryFragment.ChooseFileMoveTo)
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
                    if(flashcard) "${chooseFileMoveToViewModel.returnMovingItems().size} 個のアイテムを単語帳に移動" else
                        "${chooseFileMoveToViewModel.returnMovingItems().size}個のアイテムをフォルダに移動"

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

