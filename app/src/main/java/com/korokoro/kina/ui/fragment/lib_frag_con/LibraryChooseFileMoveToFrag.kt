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
import com.korokoro.kina.actions.changeViewVisibility
import com.korokoro.kina.actions.makeToast
import com.korokoro.kina.databinding.*
import com.korokoro.kina.db.dataclass.Card
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
            recyclerView = binding.vocabCardRV
            adapter =   LibFragChooseFileRVListAdapter(requireActivity(),chooseFileMoveToViewModel,libraryBaseViewModel)
        }

        fun setUpView(flashcard:Boolean,movingItems:List<Any>){
            val mainRV = binding.vocabCardRV
            mainRV.adapter = adapter
            mainRV.layoutManager = LinearLayoutManager(requireActivity())
            mainRV.isNestedScrollingEnabled = true

            changeViewVisibility(binding.imvSearchLoupe,false)
            binding.frameLayTopBar.addView(topBarBinding.root)
            topBarBinding.imvFileMoveToStatus.setImageDrawable(
                AppCompatResources.getDrawable(requireActivity(),
                    if(flashcard) R.drawable.icon_move_to_flashcard_cover else R.drawable.icon_move_to_folder
                )
            )
            topBarBinding.txvChooseFileMoveTo.text =
                if(flashcard) "${movingItems.size} 個のアイテムを単語帳に移動" else
                    "${movingItems.size}個のアイテムをフォルダに移動"
        }

        fun addCL(){
            fun topBarAddCL(){
                arrayOf(
                    topBarBinding.imvCloseChooseFileMoveTo
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
        fun getMovingFileIdsList(movingItems:List<Any>):List<Int>{
            val a = mutableListOf<Int>()
            val filtered = movingItems.filterIsInstance<File>()
            filtered.onEach { a.add(it.fileId) }
            return a.toList()
        }
        fun getMovingCardIdsList(movingItems:List<Any>):List<Int>{
            val a = mutableListOf<Int>()
            val filtered = movingItems.filterIsInstance<Card>()
            filtered.onEach { a.add(it.id) }
            return a.toList()
        }
        val movingItems = libraryBaseViewModel.returnSelectedItems()
        val movingFileIdsList = getMovingFileIdsList(movingItems)
        val movingCardIdsList = getMovingCardIdsList(movingItems)
        val emptyView = RvEmptyBinding.inflate(inflater,container,false).root
        val flashcard = libraryBaseViewModel.returnParentFile()?.fileStatus == FileStatus.FLASHCARD_COVER ||
                libraryBaseViewModel.returnModeInBox()==true
        val popUpVisibleObserver = Observer<Boolean>{
            changeViewVisibility(binding.libChildFragBackground,it)
            mainViewModel.setBnvCoverVisible(it)
            binding.frameLayConfirmMove.visibility = if(it) View.VISIBLE else View.GONE
        }
        val popUpTextObserver = Observer<String> {
            binding.confirmMoveToBinding.txvConfirmMove.text = it
        }
        val parentFileFromDBObserver = Observer<File> {
            libraryBaseViewModel.setParentFileFromDB(it ?:return@Observer)
        }
        val movableFoldersObserver = Observer<List<File>>{
            if(flashcard.not())
                libraryBaseViewModel.setParentRVItems(it)

        }
        val movableFlashCardsObserver = Observer<List<File>>{
            if(flashcard)
                libraryBaseViewModel.setParentRVItems(it)
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
        setUpView(flashcard,movingItems)
        addCL()

        chooseFileMoveToViewModel.setMovingItems(movingItems)
        chooseFileMoveToViewModel.popUpVisible.observe(viewLifecycleOwner,popUpVisibleObserver)
        chooseFileMoveToViewModel.popUpText.observe(viewLifecycleOwner,popUpTextObserver)
        if(movingCardIdsList.isEmpty().not())
            chooseFileMoveToViewModel.getFilesMovableFlashCards(movingCardIdsList).observe(viewLifecycleOwner,movableFlashCardsObserver)
        if(movingFileIdsList.isEmpty().not())
            chooseFileMoveToViewModel.getFilesMovableFolders(movingFileIdsList,args.fileId?.single()).observe(viewLifecycleOwner,movableFoldersObserver)


        libraryBaseViewModel.setLibraryFragment(LibraryFragment.ChooseFileMoveTo)
        libraryBaseViewModel.parentFileFromDB(args.fileId?.single()).observe(viewLifecycleOwner,parentFileFromDBObserver)
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

