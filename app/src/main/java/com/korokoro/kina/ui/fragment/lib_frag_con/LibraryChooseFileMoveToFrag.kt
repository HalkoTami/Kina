package com.korokoro.kina.ui.fragment.lib_frag_con

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.actions.changeViewIfRVEmpty
import com.korokoro.kina.databinding.*
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.customClasses.LibraryFragment
import com.korokoro.kina.ui.listadapter.LibFragChooseFileRVListAdapter
import com.korokoro.kina.ui.listener.popUp.LibFragPopUpConfirmMoveToFileCL
import com.korokoro.kina.ui.listener.topbar.LibFragTopBarChooseFileMoveToCL
import com.korokoro.kina.ui.viewmodel.*


class LibraryChooseFileMoveToFrag  : Fragment(){
    private val args: LibraryChooseFileMoveToFragArgs by navArgs()

    private lateinit var adapter:LibFragChooseFileRVListAdapter
    private lateinit var topBarBinding:LibraryFragTopBarChooseFileMoveToBinding
    private lateinit var libNavCon:NavController
    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val editFileViewModel: EditFileViewModel by activityViewModels()
    private val libraryBaseViewModel: LibraryBaseViewModel by activityViewModels()
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
            adapter =   LibFragChooseFileRVListAdapter(requireActivity(),chooseFileMoveToViewModel,libraryBaseViewModel)
        }

        fun setUpView(flashcard:Boolean){
            val mainRV = binding.vocabCardRV
            mainRV.adapter = adapter
            mainRV.layoutManager = LinearLayoutManager(requireActivity())
            mainRV.isNestedScrollingEnabled = true

            binding.frameLayTopBar.addView(topBarBinding.root)
            topBarBinding.imvFileMoveToStatus.setImageDrawable(
                AppCompatResources.getDrawable(requireActivity(),
                    if(flashcard) R.drawable.icon_move_to_flashcard_cover else R.drawable.icon_move_to_folder
                )
            )
            topBarBinding.txvChooseFileMoveTo.text =
                if(flashcard) "${chooseFileMoveToViewModel.returnMovingItems().size} 個のアイテムを単語帳に移動" else
                    "${chooseFileMoveToViewModel.returnMovingItems().size}個のアイテムをフォルダに移動"
        }

        fun addCL(){
            fun topBarAddCL(){
                arrayOf(
                    topBarBinding.imvCloseChooseFileMoveTo,
                ).onEach { it.setOnClickListener( LibFragTopBarChooseFileMoveToCL(topBarBinding,libraryBaseViewModel,)) }
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
        val emptyView = LibraryFragLayFlashCardCoverRvEmptyBinding.inflate(inflater,container,false).root
        val flashcard = libraryBaseViewModel.returnParentFile()?.fileStatus == FileStatus.FLASHCARD_COVER ||
                libraryBaseViewModel.returnModeInBox()==true
        val popUpVisibleObserver = Observer<Boolean>{
            binding.frameLayConfirmMove.visibility = if(it) View.VISIBLE else View.GONE
        }
        val popUpTextObserver = Observer<String> {
            binding.confirmMoveToBinding.txvConfirmMove.text = it
        }
        val parentFileFromDBObserver = Observer<File> {
            libraryBaseViewModel.setParentFileFromDB(it)
        }
        val childFilesFromDBObserver = Observer<List<File>>{
            val filteredList = if(flashcard){
                it.filter { it.fileStatus == FileStatus.FLASHCARD_COVER }
            } else it.filter { it.fileStatus == FileStatus.FOLDER && libraryBaseViewModel.returnSelectedItems().contains(it).not()}
            libraryBaseViewModel.setParentRVItems(filteredList)
        }
        val parentRVItemsObserver = Observer<List<Any>>{
            val list = it.filterIsInstance<File>()
            adapter.submitList(list)
            changeViewIfRVEmpty(list,binding.frameLayRvEmpty,emptyView)
        }
        val parentFileAncestorsObserver = Observer<List<File>> {
            editFileViewModel.filterBottomMenuWhenInChooseFileMoveTo(
                flashCard = flashcard, args.fileId == null,it
            )
        }
        val chooseFileMoveToModeObserver = Observer<Boolean>{
            if(it == false){
                libraryBaseViewModel.setMultipleSelectMode(false)
                myNavCon.popBackStack()
            }
        }
        setUpLateInitVars()
        setUpView(flashcard)
        addCL()

        chooseFileMoveToViewModel.setMovingItems(libraryBaseViewModel.returnSelectedItems())
        chooseFileMoveToViewModel.popUpVisible.observe(viewLifecycleOwner,popUpVisibleObserver)
        chooseFileMoveToViewModel.popUpText.observe(viewLifecycleOwner,popUpTextObserver)

        libraryBaseViewModel.setLibraryFragment(LibraryFragment.ChooseFileMoveTo)
        libraryBaseViewModel.parentFileFromDB(args.fileId?.single() ).observe(viewLifecycleOwner,parentFileFromDBObserver)
        libraryBaseViewModel.childFilesFromDB(args.fileId?.single()).observe(viewLifecycleOwner,childFilesFromDBObserver)
        libraryBaseViewModel.parentRVItems.observe(viewLifecycleOwner,parentRVItemsObserver)
        libraryBaseViewModel.parentFileAncestorsFromDB(args.fileId?.single()).observe(viewLifecycleOwner,parentFileAncestorsObserver)
        libraryBaseViewModel.chooseFileMoveToMode.observe(viewLifecycleOwner,chooseFileMoveToModeObserver)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

