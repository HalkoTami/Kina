package com.example.tangochoupdated.ui.library

import LibraryFragTopBarClickListener
import LibraryPopUpConfirmDeleteClickListener
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.mainactivity.Animation


class LibraryFragment : Fragment(){
    private val args: LibraryFragmentArgs by navArgs()

    private lateinit var myNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter: LibraryListAdapter
    private val  LibFragClickListenerItem = mutableListOf<View>()
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
//        FragmentのviewBinding定義
        _binding = LibraryFragBinding.inflate(inflater, container, false)
//       NavControllerの定義
        myNavCon = requireActivity().findNavController(
            requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).id
        )
//        viewの初期設定
        resetView(binding)
//        recyclerViewの定義
        recyclerView = binding.vocabCardRV
        adapter = LibraryListAdapter(createFileViewModel,createCardViewModel,libraryViewModel, requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.isNestedScrollingEnabled = true

//        clickListenerを追加
        addClickListenerToLibViews()
//        －－－－LibraryViewModelの読み取り－－－－

        libraryViewModel.apply {
//            初期設定
            onStart()
//            －－－－DBから降ろしてきたデータをviewModelに設定－－－－

//            親アイテムのid
            val myId: Int? = args.parentItemId?.single()
            parentFileFromDB(myId).observe(viewLifecycleOwner) {
                Toast.makeText(requireActivity(),"${it?.parentFileId}",Toast.LENGTH_SHORT).show()
                setParentFileFromDB(it)
            }
            parentFileAncestorsFromDB(myId).observe(viewLifecycleOwner){
                setParentFileAncestorsFromDB(it)
            }
            childFilesFromDB(myId).observe(viewLifecycleOwner) {
                setChildFilesFromDB(it)
            }
            childCardsFromDB(myId).observe(viewLifecycleOwner) {
                setChildCardsFromDB(it)
            }
            deletingItem.observe(viewLifecycleOwner) { list ->
                if (list.isEmpty().not()) {
                    val childFileIds = mutableListOf<Int>()
                    list.onEach {
                        when (it.type) {
                            LibRVViewType.FlashCardCover, LibRVViewType.Folder -> childFileIds.add(
                                it.file!!.fileId
                            )
                        }
                    }
                    getAllDescendantsByFileId(list[0].file?.fileId).observe(viewLifecycleOwner) {
                        setDeletingItemChildrenFiles(it)
                        Toast.makeText(requireContext(), "${it.size}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

//            －－－－－－－－
//            －－－－UIへの反映－－－－

//            recyclerView
            multipleSelectMode.observe(viewLifecycleOwner) { selectable ->
                recyclerView.children.iterator().forEach {
                    it.findViewById<ConstraintLayout>(R.id.base_container).tag =
                        if (selectable) LibRVState.Selectable else LibRVState.Plane
                    it.findViewById<ImageView>(R.id.btn_select).visibility =
                        if (selectable) View.VISIBLE else View.GONE
                }
            }
            myFinalList.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding.emptyBinding.root.visibility =
                    if (it.isEmpty()) View.VISIBLE else View.GONE
            }
            makeUnSwiped.observe(viewLifecycleOwner){
                if(it){
                    recyclerView.children.iterator().forEach { view ->
                        val parent = view.findViewById<ConstraintLayout>(R.id.base_container)
                        if(parent.tag == LibRVState.LeftSwiped){
                            Animation().animateLibRVLeftSwipeLay(
                                view.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show),false)
                            parent.tag = LibRVState.Plane
                        }

                    }
                }
            }

            binding.apply {
//                トップバー
                parentFileAncestors.observe(viewLifecycleOwner){
                    topBarFileBinding.apply {
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

                if(myId == null) topBarHomeBinding.root.visibility = View.VISIBLE
                else topBarFileBinding.root.visibility = View.VISIBLE
                topBarMode.observe(viewLifecycleOwner) {
                    resetView(binding)
                    when(it){
                        LibraryTopBarMode.Home -> topBarHomeBinding.root.visibility = View.VISIBLE
                        LibraryTopBarMode.Multiselect -> topBarMultiselectBinding.root.visibility = View.VISIBLE
                        LibraryTopBarMode.File -> {
                            topframelayout.elevation = 0f
                            topBarFileBinding.root.visibility = View.VISIBLE
                        }
                        LibraryTopBarMode.InBox -> {
                            topBarInboxBinding.root.visibility = View.VISIBLE
                        }
                        LibraryTopBarMode.ChooseFileMoveTo -> {
                            topBarChooseFileMoveToBinding.root.visibility = View.VISIBLE
                            recyclerView.children.iterator().forEach {
                                it.findViewById<ImageView>(R.id.btn_select).visibility = View.VISIBLE
                            }

                        }
                        else -> return@observe
                    }
                }
                parentFile.observe(viewLifecycleOwner){
                    topBarFileBinding.apply {
                        txvFileTitle.text = it?.title
                        val fileIconDraw:Drawable? =
                        when(it?.fileStatus){
                            FileStatus.FOLDER -> requireActivity().getDrawable(R.drawable.icon_file)
                            FileStatus.TANGO_CHO_COVER -> requireActivity().getDrawable(R.drawable.icon_flashcard)
                            else -> return@observe
                        }
                        imvFileType.setImageDrawable(fileIconDraw)
                    }
                }
                selectedItems.observe(viewLifecycleOwner){
                    topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
                }



//                空だった時
                emptyBinding.apply {
                    fileEmptyText.observe(viewLifecycleOwner) {
                        txvCenter.text = it
                    }
                }
//                削除確認のポップアップ
                confirmPopUp.observe(viewLifecycleOwner) {
                    binding.apply {
                        when(it.confirmMode){


                        }
                        }
                    }

            }

//            －－－－－－－－
        }

//        －－－－－－－－
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resetView(binding)
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
    fun makeAllUnClickable(){
        LibFragClickListenerItem.onEach {
            it.setOnClickListener(null)
        }
        Toast.makeText(requireActivity(),"called",Toast.LENGTH_SHORT).show()

    }



//    override fun onClick(v: View?) {
//        fun changeMenuVisibility(){
//            binding.topBarMultiselectBinding.frameLayMultiModeMenu.apply{
//                visibility =  if(this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
//            }
//        }
//
//        binding.apply {
//            when(v){
//                topBarMultiselectBinding.imvClose
//                    ->  libraryViewModel.setMultipleSelectMode(false)
//                topBarMultiselectBinding.imvChangeMenuVisibility
//                    -> changeMenuVisibility()
//                topBarMultiselectBinding.multiSelectMenuBinding.imvDeleteFile
//                    -> libraryViewModel.onClickDeleteParentItem()
//                topBarMultiselectBinding.multiSelectMenuBinding.imvMoveTo
//                        -> {
//                    libraryViewModel.onClickMoveTo()
//                    Toast.makeText(requireActivity(),"called",Toast.LENGTH_SHORT).show()
//                }
//                topBarHomeBinding.frameLayInBox
//                    -> libraryViewModel.onClickInBox()
//                topBarInboxBinding.imvClose
//                    -> myNavCon.popBackStack()
//                confirmDeletePopUp.btnCommitDelete
//                    -> libraryViewModel.onClickBtnCommitConfirm(v.tag as LibraryViewModel.ConfirmMode)
//                confirmDeletePopUp.btnDenial
//                    -> libraryViewModel.onClickBtnDenial(v.tag as LibraryViewModel.ConfirmMode)
//
//            }
//        }
//    }
    fun resetView(binding: LibraryFragBinding){
        binding.apply {
            arrayOf(topBarInboxBinding.root,
            topBarFileBinding.root,
            topBarHomeBinding.root,
            topBarMultiselectBinding.root,
                topBarChooseFileMoveToBinding.root).onEach {
                it.visibility = View.GONE
            }
        }

    }
    private fun addClickListenerToLibViews(){
        fun addTopBarViews(){
            val home = binding.topBarHomeBinding
            val file = binding.topBarFileBinding
            val inBox = binding.topBarInboxBinding
            val multi = binding.topBarMultiselectBinding
            val moveTo = binding.topBarChooseFileMoveToBinding
            arrayOf(
                home.frameLayInBox,
                home.imvBookMark,
                file.imvGoBack,
                file.lineLayGGFile,
                file.lineLayGPFile,
                inBox.imvMoveToFlashCard,
                inBox.imvClose,
                multi.imvClose,
                multi.imvSelectAll,
                multi.imvChangeMenuVisibility,
                multi.multiSelectMenuBinding.imvMoveSelectedItems,
                multi.multiSelectMenuBinding.imvDeleteSelectedItems,
                multi.multiSelectMenuBinding.imvSetFlagToSelectedItems,
                moveTo.imvCloseChooseFileMoveTo
                ).onEach { it.setOnClickListener { LibraryFragTopBarClickListener(binding,libraryViewModel, myNavCon) } }
        }
        fun addConfirmDeletePopUp(){
            val onlyP = binding.confirmDeletePopUpBinding
            val deleteAllC = binding.confirmDeleteChildrenPopUpBinding
            arrayOf(
                onlyP.btnCloseConfirmDeleteOnlyParentPopup,
                onlyP.btnCommitDeleteOnlyParent,
                onlyP.btnDenyDeleteOnlyParent,
                deleteAllC.btnCloseConfirmDeleteOnlyParentPopup,
                deleteAllC.btnCommitDeleteAllChildren,
                deleteAllC.btnDenyDeleteAllChildren
            )   .onEach {
                it.setOnClickListener { LibraryPopUpConfirmDeleteClickListener(binding,libraryViewModel) }
            }
        }

        addTopBarViews()
        addConfirmDeletePopUp()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


