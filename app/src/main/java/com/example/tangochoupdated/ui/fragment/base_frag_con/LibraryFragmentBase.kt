package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.view_set_up.ConfirmMode
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpFragment
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel
import com.example.tangochoupdated.ui.animation.Animation


class LibraryFragmentBase : Fragment(){

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()

    private var _binding: LibraryFragBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LibraryFragBinding.inflate(inflater, container, false)



        libraryViewModel.apply {
            confirmPopUp.observe(viewLifecycleOwner){
                val visibility = it.visible
                if(!visibility){
                    binding.confirmDeletePopUpBinding.root.visibility = View.GONE
                    binding.confirmDeleteChildrenPopUpBinding.root.visibility = View.GONE
                } else {
                    when(it.confirmMode){
                        ConfirmMode.DeleteOnlyParent-> binding.confirmDeletePopUpBinding.root.visibility = View.VISIBLE
                        ConfirmMode.DeleteWithChildren -> binding.confirmDeleteChildrenPopUpBinding.root.visibility = View.VISIBLE
                    }
                }
            }
            deletingItem.observe(viewLifecycleOwner){ list ->
                if (list.isEmpty().not()) {
                    val fileIds = mutableListOf<Int>()
                    list.onEach { when(it ){
                        is File -> fileIds.add(it.fileId)
                    } }
                    getAllDescendantsByFileId(fileIds).observe(viewLifecycleOwner) {
                        setDeletingItemChildrenFiles(it)
                    }
                    getCardsByMultipleFileId(fileIds).observe(viewLifecycleOwner){
                        setDeletingItemChildrenCards(it)
                    }
                }
                binding.confirmDeletePopUpBinding.apply {
                    txvConfirmDeleteOnlyParent.text =
                    if(list.size==1){
                        when(list[0]){
                            is File -> {
                                val file = list[0] as File
                                "${file.title}を削除しますか？"
                            }
                            is Card  -> "カードを削除しますか？"
                            else -> ""

                        }

                    } else if(list.size>1){
                        when(returnParentFile()?.fileStatus){
                            FileStatus.TANGO_CHO_COVER -> "${list.size}のカードを削除しますか？"
                            else -> "${list.size}のアイテムを削除しますか？"
                        }
                    } else ""
                }

            }
            deletingItemChildrenFiles.observe(viewLifecycleOwner){ list ->
                val folderAmount = list?.filter { it.fileStatus == FileStatus.FOLDER }?.size ?:0
                val flashcardCoverAmount = list?.filter { it.fileStatus == FileStatus.TANGO_CHO_COVER }?.size ?:0
                binding.confirmDeleteChildrenPopUpBinding.apply {
                    txvContainingFolder.text = "${folderAmount}個"
                    txvContainingFlashcard.text = "${flashcardCoverAmount}個"
                }
            }
            deletingItemChildrenCards.observe(viewLifecycleOwner){ list->
                val cardAmount = list?.size
                binding.confirmDeleteChildrenPopUpBinding.txvContainingCard.text = "${cardAmount}枚"
            }
        }

        val a = childFragmentManager.findFragmentById(binding.libFragConView.id) as NavHostFragment
        myNavCon = a.navController

        libraryViewModel.action.observe(viewLifecycleOwner){
            val a = childFragmentManager.findFragmentById(binding.libFragConView.id) as NavHostFragment
            a.navController.navigate(it)
            Toast.makeText(requireActivity(), "action called ", Toast.LENGTH_SHORT).show()
        }

        LibrarySetUpFragment(libraryViewModel).setUpFragLibBase(binding)
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
                libraryViewModel.onClickBack()
                myNavCon.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }
    fun changeLibRVSelectBtnVisibility(rv:RecyclerView,visible: Boolean){
        rv.children.iterator().forEach { view ->
            view.findViewById<ImageView>(R.id.btn_select).apply {
                visibility =if(visible) View.VISIBLE else View.GONE
            }
        }
    }
    fun changeLibRVAllSelectedState(rv:RecyclerView,selected:Boolean){
        rv.children.iterator().forEach { view ->
            view.findViewById<ImageView>(R.id.btn_select).apply {
                isSelected = selected
            }
        }
    }
    fun changeStringBtnVisibility(rv:RecyclerView,visible:Boolean){
        rv.children.iterator().forEach { view ->
            arrayOf(
                view.findViewById<ImageView>(R.id.btn_edt_front),
                view.findViewById<ImageView>(R.id.btn_edt_back),
                view.findViewById(R.id.btn_add_new_card))
                .onEach {
                    it.visibility = if(visible)View.VISIBLE else View.GONE
                }
        }
    }

    fun makeLibRVUnSwiped(rv:RecyclerView){
        rv.children.iterator().forEach { view ->
            val parent = view.findViewById<ConstraintLayout>(R.id.base_container)
            if(parent.tag == LibRVState.LeftSwiped){
                Animation().animateLibRVLeftSwipeLay(
                    view.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show),false)
            }
            view.findViewById<ImageView>(R.id.btn_select).visibility = View.GONE
            parent.tag = LibRVState.Plane
        }
    }

}


