package com.koronnu.kina.ui.tabLibrary.chooseFileMoveTo

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
import com.koronnu.kina.R
import com.koronnu.kina.util.changeViewVisibility
import com.koronnu.kina.databinding.*
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.source.local.entity.enumclass.FileStatus
import com.koronnu.kina.data.model.enumClasses.LibraryFragment
import com.koronnu.kina.ui.EditFileViewModel
import com.koronnu.kina.ui.MainViewModel
import com.koronnu.kina.ui.tabLibrary.LibraryBaseViewModel
import com.koronnu.kina.ui.listener.topbar.LibFragTopBarChooseFileMoveToCL


class LibraryChooseFileMoveToFrag  : Fragment(){
    private val args: com.koronnu.kina.ui.tabLibrary.chooseFileMoveTo.LibraryChooseFileMoveToFragArgs by navArgs()

    private lateinit var adapter: LibFragChooseFileRVListAdapter
    private lateinit var topBarBinding:LibraryFragTopBarChooseFileMoveToBinding
    private lateinit var libNavCon:NavController
    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val editFileViewModel: EditFileViewModel by activityViewModels()
    private val libraryBaseViewModel: LibraryBaseViewModel by activityViewModels()
    private val chooseFileMoveToViewModel: ChooseFileMoveToViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

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
            libraryBaseViewModel.setChildFragBinding(binding)
            recyclerView = binding.vocabCardRV
            adapter =   LibFragChooseFileRVListAdapter(chooseFileMoveToViewModel)
            binding.apply {
                libraryViewModel = libraryBaseViewModel
                planeRVAdapter = adapter
                lifecycleOwner = viewLifecycleOwner
            }
        }

        fun setUpView(flashcard:Boolean,movingItems:List<Any>){
            val mainRV = binding.vocabCardRV
            mainRV.adapter = adapter
            mainRV.layoutManager = LinearLayoutManager(requireActivity())
            mainRV.isNestedScrollingEnabled = true

            binding.flTpbAnkiBox.addView(topBarBinding.root)
            topBarBinding.imvFileMoveToStatus.setImageDrawable(
                AppCompatResources.getDrawable(requireActivity(),
                    if(flashcard) R.drawable.icon_move_to_flashcard_cover else R.drawable.icon_move_to_folder
                )
            )
            topBarBinding.txvChooseFileMoveTo.text =
                resources.getString(R.string.chooseFileMoveToTopBarBin_topText,movingItems.size,
                    resources.getString(if(flashcard) R.string.flashcard else R.string.folder) )
        }

        fun addCL(){
            fun topBarAddCL(){
                arrayOf(
                    topBarBinding.imvCloseChooseFileMoveTo
                ).onEach { it.setOnClickListener( LibFragTopBarChooseFileMoveToCL(topBarBinding,libraryBaseViewModel)) }
            }
            fun confirmMoveToFilePopUpAddCL(){
                val popUp = binding.confirmMoveToBinding
                arrayOf(
                    popUp.btnCancelMove,
                    popUp.btnCommitMove,
                    popUp.btnCloseConfirmMove
                )   .onEach {
                    it.setOnClickListener(LibFragPopUpConfirmMoveToFileCL(popUp, chooseFileMoveToViewModel))
                }
            }
            topBarAddCL()
            confirmMoveToFilePopUpAddCL()
        }
        val flashcard = libraryBaseViewModel.returnParentFile()?.fileStatus == FileStatus.FLASHCARD_COVER ||
                libraryBaseViewModel.returnModeInBox()==true
        val popUpVisibleObserver = Observer<Boolean>{
            changeViewVisibility(binding.libChildFragBackground,it)
            binding.frameLayConfirmMove.visibility = if(it) View.VISIBLE else View.GONE
        }
        val popUpTextObserver = Observer<String> {
            binding.confirmMoveToBinding.txvConfirmMove.text = it
        }
        val parentFileFromDBObserver = Observer<File> {
            libraryBaseViewModel.setParentFile(it ?:return@Observer)
        }
//        val movableFoldersObserver = Observer<Map<File,List<File>>>{
//            if(flashcard.not()){
//                libraryBaseViewModel.setParentRVItems(it.map { it.value })
//                adapter.submitList(it.map { it.key })
//                chooseFileMoveToViewModel.setFolderAndChildFilesMap(it)
//            }
//
//
//        }
//        val movableFlashCardsObserver = Observer<Map<File,List<Card>>>{
//            if(flashcard){
//                libraryBaseViewModel.setParentRVItems(it.map { it.value })
//                adapter.submitList(it.map { it.key })
//                chooseFileMoveToViewModel.setFlashcardAndChildListMap(it)
//            }
//
//        }
        val parentRVItemsObserver = Observer<List<File>?>{
            chooseFileMoveToViewModel.setMovableFiles(it)
            adapter.submitList(it)
//            changeViewIfRVEmpty(list,binding.frameLayRvEmpty,emptyView)
        }
        val parentFileAncestorsObserver = Observer<List<File>> {
            libraryBaseViewModel.setParentFileAncestorsFromDB(it)
        }
        val chooseFileMoveToModeObserver = Observer<Boolean>{
            if(it == false){
                libraryBaseViewModel.setMultipleSelectMode(false)
                myNavCon.popBackStack()
            }
        }
        setUpLateInitVars()
        setUpView(flashcard,chooseFileMoveToViewModel.returnMovingItems())
        addCL()


        chooseFileMoveToViewModel.popUpVisible.observe(viewLifecycleOwner,popUpVisibleObserver)
        chooseFileMoveToViewModel.popUpText.observe(viewLifecycleOwner,popUpTextObserver)
        chooseFileMoveToViewModel.getFilteredFiles(args.fileId?.single()).observe(viewLifecycleOwner,parentRVItemsObserver)
//        chooseFileMoveToViewModel.collectMovingFileData.observe(viewLifecycleOwner){
//            TODO()
//        }

//        if(movingCardIdsList.isEmpty().not())
//            chooseFileMoveToViewModel.getFilesMovableFlashCards(movingCardIdsList).observe(viewLifecycleOwner,movableFlashCardsObserver)
//        if(movingFileIdsList.isEmpty().not())
//            chooseFileMoveToViewModel.getFilesMovableFolders(movingFileIdsList,args.fileId?.single()).observe(viewLifecycleOwner,movableFoldersObserver)

//        libraryBaseViewModel.childFilesFromDB(args.fileId?.single()).observe(viewLifecycleOwner){
//            val filtered = it.filter { chooseFileMoveToViewModel.returnMovingItems().filterIsInstance<File>().map { it.fileId }.contains(it.fileId).not() }
//            libraryBaseViewModel.setParentRVItems(filtered)
//        }
        libraryBaseViewModel.setLibraryFragment(LibraryFragment.ChooseFileMoveTo)
        libraryBaseViewModel.parentFileFromDB(args.fileId?.single()).observe(viewLifecycleOwner,parentFileFromDBObserver)
//        libraryBaseViewModel.parentRVItems.observe(viewLifecycleOwner,parentRVItemsObserver)
        libraryBaseViewModel.parentFileAncestorsFromDB(args.fileId?.single()).observe(viewLifecycleOwner,parentFileAncestorsObserver)
        libraryBaseViewModel.chooseFileMoveToMode.observe(viewLifecycleOwner,chooseFileMoveToModeObserver)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

