package com.koronnu.kina.ui.fragment.base_frag_con

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.LibraryFragBinding
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.customClasses.enumClasses.LibraryFragment
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.ui.observer.CommonOb
import com.koronnu.kina.ui.viewmodel.*


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
                    deleteAllC.btnCloseConfirmDeleteChildrenPopup,
                    deleteAllC.btnDeleteAllChildren,
                    deleteAllC.deleteOnlyFile,
                    deleteAllC.btnCancelDeleteChildren,
                    binding.background
                )   .onEach {
                    it.setOnClickListener(this)
                }
            }
            confirmDeletePopUpAddCL()

        }
        val toastObserver = CommonOb().toastObserver(requireActivity())
        val confirmDeleteViewObserver = Observer<DeletePopUpViewModel.ConfirmDeleteView>{
            binding.confirmDeletePopUpBinding.apply {
                changeViewVisibility(binding.background,deletePopUpViewModel.checkBackgroundVisible())
                binding.frameLayConfirmDelete.visibility = if(it.visible) View.VISIBLE else View.GONE
                txvConfirmDeleteOnlyParent.text = it.confirmText
                mainViewModel.setBnvCoverVisible(deletePopUpViewModel.checkBackgroundVisible())
                deletePopUpViewModel.doOnPopUpVisibilityChanged()
            }
        }
        val confirmDeleteWithChildrenViewObserver = Observer<DeletePopUpViewModel.ConfirmDeleteWithChildrenView>{
            binding.confirmDeleteChildrenPopUpBinding.apply {
                changeViewVisibility(binding.background,deletePopUpViewModel.checkBackgroundVisible())
                binding.frameLayConfirmDeleteWithChildren.visibility =  if(it.visible) View.VISIBLE else View.GONE
                txvContainingFolder.text = it.containingFolder.toString()
                txvContainingFlashcard.text = it.containingFlashCardCover.toString()
                txvContainingCard.text = it.containingCards.toString()
                mainViewModel.setBnvCoverVisible(deletePopUpViewModel.checkBackgroundVisible())
                deletePopUpViewModel.doOnPopUpVisibilityChanged()
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
        val libraryFragObserver = Observer<LibraryFragment>{
            when(it){
                LibraryFragment.ChooseFileMoveTo -> return@Observer
                else -> libraryBaseViewModel.clearSelectedItems()
            }
        }
        val reorderedLeftItemsObserver = Observer<List<Any>> {
            val updateNeeded = libraryBaseViewModel.filterReorderedItemsForUpdate()
            chooseFileMoveToViewModel.setMovingItemSistersUpdateNeeded(updateNeeded)
            deletePopUpViewModel.setDeletingItemsSistersUpdateNeeded(updateNeeded)
        }
        searchViewModel.searchingText.observe(viewLifecycleOwner){ searchText ->
            if(searchText.isBlank())  {
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

        libraryBaseViewModel.setLibraryBaseBinding(binding)
        libraryBaseViewModel.setLibraryNavCon(libNavCon)
        mainViewModel.setChildFragmentStatus(MainFragment.Library)
        mainViewModel.setBnvVisibility(true)
        libraryBaseViewModel.setChooseFileMoveToViewModel(chooseFileMoveToViewModel)
        libraryBaseViewModel.setDeletePopUpViewModel(deletePopUpViewModel)
        libraryBaseViewModel.observeLiveDataInFragment(viewLifecycleOwner)

        libraryBaseViewModel.reorderedLeftItems.observe(viewLifecycleOwner,reorderedLeftItemsObserver)
        libraryBaseViewModel.parentFragment.observe(viewLifecycleOwner,libraryFragObserver)
        chooseFileMoveToViewModel.toast.observe(viewLifecycleOwner,toastObserver)
        deletePopUpViewModel.toast.observe(viewLifecycleOwner,toastObserver)
        libraryBaseViewModel.toast.observe(viewLifecycleOwner,toastObserver)
        deletePopUpViewModel.confirmDeleteView.observe(viewLifecycleOwner,confirmDeleteViewObserver)
        deletePopUpViewModel.confirmDeleteWithChildrenView.observe(viewLifecycleOwner,confirmDeleteWithChildrenViewObserver)
        deletePopUpViewModel.deletingItem.observe(viewLifecycleOwner,deletingItemObserver)


         return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            deleteAllC.btnCloseConfirmDeleteChildrenPopup -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
            deleteAllC.btnDeleteAllChildren -> deletePopUpViewModel.deleteFileWithChildren()
            deleteAllC.deleteOnlyFile -> deletePopUpViewModel.deleteOnlyFile()
            deleteAllC.btnCancelDeleteChildren -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
        }
    }

}


