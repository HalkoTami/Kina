package com.korokoro.kina.ui.fragment.base_frag_con

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.korokoro.kina.actions.makeToast
import com.korokoro.kina.databinding.LibraryFragBinding
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.customClasses.MainFragment
import com.korokoro.kina.ui.observer.CommonOb
import com.korokoro.kina.ui.viewmodel.*
import com.korokoro.kina.ui.customClasses.MakeToastFromVM


class LibraryBaseFrag : Fragment(),View.OnClickListener{

    private lateinit var libNavCon:NavController
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val libraryBaseViewModel: LibraryBaseViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()
    private val chooseFileMoveToViewModel:ChooseFileMoveToViewModel by activityViewModels()
    private var _binding: LibraryFragBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fun setLateInitVars(){
            _binding = LibraryFragBinding.inflate(inflater, container, false)
            val a = childFragmentManager.findFragmentById(binding.libFragConView.id) as NavHostFragment
            libNavCon = a.navController
        }
        fun addClickListeners(){
            fun confirmDeletePopUpAddCL(){
                val onlyP = binding.confirmDeletePopUpBinding
                val deleteAllC = binding.confirmDeleteChildrenPopUpBinding
                arrayOf(
                    onlyP.btnCloseConfirmDeleteOnlyParentPopup,
                    onlyP.btnCommitDeleteOnlyParent,
                    onlyP.btnCancel,
                    deleteAllC.btnCloseConfirmDeleteOnlyParentPopup,
                    deleteAllC.btnDeleteAllChildren,
                    deleteAllC.deleteOnlyFile,
                    deleteAllC.btnCancel
                )   .onEach {
                    it.setOnClickListener(this)
                }
            }
            confirmDeletePopUpAddCL()

        }
        val toastObserver = CommonOb().toastObserver(requireActivity())
        val confirmDeleteViewObserver = Observer<DeletePopUpViewModel.ConfirmDeleteView>{
            binding.confirmDeletePopUpBinding.apply {
                binding.background.visibility = if(it.visible) View.VISIBLE else View.GONE
                binding.frameLayConfirmDelete.visibility = if(it.visible) View.VISIBLE else View.GONE
                txvConfirmDeleteOnlyParent.text = it.confirmText
            }
        }
        val confirmDeleteWithChildrenViewObserver = Observer<DeletePopUpViewModel.ConfirmDeleteWithChildrenView>{
            binding.confirmDeleteChildrenPopUpBinding.apply {
                binding.background.visibility = if(it.visible) View.VISIBLE else View.GONE
                binding.frameLayConfirmDeleteWithChildren.visibility =  if(it.visible) View.VISIBLE else View.GONE
                txvContainingFolder.text = it.containingFolder.toString()
                txvContainingFlashcard.text = it.containingFlashCardCover.toString()
                txvContainingCard.text = it.containingCards.toString()
            }
        }
        val deletingItemObserver = Observer<List<Any>> { list->
            deletePopUpViewModel.apply {
                if (list.isEmpty().not()) {
                    setDeleteText(list)
                    setDeleteWithChildrenText(list)
                    val fileIds = mutableListOf<Int>()
                    list.onEach { when(it ){
                        is File -> fileIds.add(it.fileId)
                    } }
                    getAllDescendantsByFileId(fileIds).observe(viewLifecycleOwner) {
                        setDeletingItemChildrenFiles(it)
                        setContainingFilesAmount(it)
                    }
                    getCardsByMultipleFileId(fileIds).observe(viewLifecycleOwner){
                        setDeletingItemChildrenCards(it)
                        setContainingCardsAmount(it)
                    }
                }
            }

        }
        searchViewModel.searchingText.observe(viewLifecycleOwner){ searchText ->
            if(searchText == "")  {
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
            else{
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                searchViewModel.getFilesByWords(searchText).observe(viewLifecycleOwner){
                    searchViewModel.setMatchedFiles(it)
                    val a = mutableListOf<Any>()
                    a.addAll(searchViewModel.returnMatchedCards())
                    a.addAll(it)
                    searchViewModel.setMatchedItems(a)

                }
                searchViewModel.getCardsByWords(searchText).observe(viewLifecycleOwner){
                    searchViewModel.setMatchedCards(it)
                    val a = mutableListOf<Any>()
                    a.addAll(searchViewModel.returnMatchedFiles())
                    a.addAll(it)
                    searchViewModel.setMatchedItems(a)

                }
            }
        }


        setLateInitVars()
        addClickListeners()

        libraryBaseViewModel.setLibraryNavCon(libNavCon)
        libraryBaseViewModel.setRVCover(LibraryBaseViewModel.RvCover(true))
        mainViewModel.setChildFragmentStatus(MainFragment.Library)
        mainViewModel.setBnvVisibility(true)

        chooseFileMoveToViewModel.toast.observe(viewLifecycleOwner,toastObserver)
        deletePopUpViewModel.toast.observe(viewLifecycleOwner,toastObserver)
        deletePopUpViewModel.confirmDeleteView.observe(viewLifecycleOwner,confirmDeleteViewObserver)
        deletePopUpViewModel.confirmDeleteWithChildrenView.observe(viewLifecycleOwner,confirmDeleteWithChildrenViewObserver)
        deletePopUpViewModel.deletingItem.observe(viewLifecycleOwner,deletingItemObserver)


         return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                if(!libraryBaseViewModel.checkViewReset())
                    libNavCon.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }
    override fun onClick(v: View?) {
        val onlyP = binding.confirmDeletePopUpBinding
        val deleteAllC = binding.confirmDeleteChildrenPopUpBinding
        when(v){
            onlyP.btnCloseConfirmDeleteOnlyParentPopup -> deletePopUpViewModel.setConfirmDeleteVisible(false)
            onlyP.btnCommitDeleteOnlyParent -> {
                deletePopUpViewModel.apply {
                    if(checkDeletingItemsHasChildren()) {
                        setConfirmDeleteVisible(false)
                        setConfirmDeleteWithChildrenVisible(true)
                    } else deleteOnlyFile()
                }

            }
            onlyP.btnCancel -> deletePopUpViewModel.setConfirmDeleteVisible(false)
            deleteAllC.btnCloseConfirmDeleteOnlyParentPopup -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
            deleteAllC.btnDeleteAllChildren -> deletePopUpViewModel.deleteFileWithChildren()
            deleteAllC.deleteOnlyFile -> deletePopUpViewModel.deleteOnlyFile()
            deleteAllC.btnCancel -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
        }
    }

}


