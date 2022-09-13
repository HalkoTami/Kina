package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.ItemColorPaletBinding
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragPopupConfirmDeleteAllChildrenBinding
import com.example.tangochoupdated.databinding.LibraryFragPopupConfirmDeleteBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.ui.viewmodel.customClasses.LibRVState
import com.example.tangochoupdated.ui.viewmodel.customClasses.MainFragment
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.listener.popUp.LibFragPopUpConfirmDeleteCL
import com.example.tangochoupdated.ui.observer.CommonOb
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.viewmodel.*


class LibraryFragmentBase : Fragment(),View.OnClickListener{

    private lateinit var libNavCon:NavController
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()
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
        val fileMovedToastObserver = Observer<Boolean>{
            CommonOb().observeToast(requireActivity(),chooseFileMoveToViewModel.returnToastText(),it)
        }
        val deletedToastObserver = Observer<Boolean>{
            CommonOb().observeToast(requireActivity(),deletePopUpViewModel.returnToastText(),it)
        }
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
                txvContainingFolder.text = "${it.containingFolder}個"
                txvContainingFlashcard.text = "${it.containingFlashCardCover}個"
                txvContainingCard.text = "${it.containingCards}枚"
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
        searchViewModel.searchingText.observe(viewLifecycleOwner){
            if(it == "")  {
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
            else{
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                searchViewModel.getFilesByWords(it).observe(viewLifecycleOwner){
                    searchViewModel.setMatchedFiles(it)
                    val a = mutableListOf<Any>()
                    a.addAll(searchViewModel.returnMatchedCards())
                    a.addAll(it)
                    searchViewModel.setMatchedItems(a)

                }
                searchViewModel.getCardsByWords(it).observe(viewLifecycleOwner){
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

        libraryViewModel.setLibraryNavCon(libNavCon)
        libraryViewModel.setRVCover(LibraryViewModel.RvCover(true))
        baseViewModel.setChildFragmentStatus(MainFragment.Library)

        chooseFileMoveToViewModel.showToast.observe(viewLifecycleOwner,fileMovedToastObserver)
        deletePopUpViewModel.showToast.observe(viewLifecycleOwner,deletedToastObserver)
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
                if(!libraryViewModel.checkViewReset())
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
            onlyP.btnCancel -> deletePopUpViewModel.setConfirmDeleteVisible(false,)
            deleteAllC.btnCloseConfirmDeleteOnlyParentPopup -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
            deleteAllC.btnDeleteAllChildren -> deletePopUpViewModel.deleteFileWithChildren()
            deleteAllC.deleteOnlyFile -> deletePopUpViewModel.deleteOnlyFile()
            deleteAllC.btnCancel -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
        }
    }

}


