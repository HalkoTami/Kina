package com.example.tangochoupdated.ui.library

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.db.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel


class LibraryFragment : Fragment(),View.OnClickListener {
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
        addToClickableItem(binding)
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
                recyclerView.children.iterator().forEachRemaining {
                    it.findViewById<ConstraintLayout>(R.id.base_container).tag =
                        if (selectable) LibRVState.Selectable else LibRVState.Plane
                    it.findViewById<ImageView>(R.id.btn_select).visibility =
                        if (selectable) View.VISIBLE else View.GONE
                }
            }
            myFinalList.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
                binding.emptyBinding.root.visibility =
                    if (it.isEmpty()) View.VISIBLE else View.GONE
            }

            binding.apply {
//                トップバー
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
                        if (it.visible) frameLayConfirmDelete.visibility = View.VISIBLE
                        else if (it.visible.not()) frameLayConfirmDelete.visibility = View.GONE
                        confirmDeletePopUp.apply {
                            txvConfirmDelete.text = it.txvConfirmText
                            btnDenial.text = it.btnDenialText
                            btnDenial.tag = it.confirmMode
                            btnCommitDelete.text = it.btnCommitConfirmText
                            btnCommitDelete.tag = it.confirmMode
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



    override fun onClick(v: View?) {
        fun changeMenuVisibility(){
            binding.topBarMultiselectBinding.frameLayMultiModeMenu.apply{
                visibility =  if(this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

        binding.apply {
            when(v){
                topBarMultiselectBinding.imvClose                              ->  libraryViewModel.setMultipleSelectMode(false)
                topBarMultiselectBinding.imvChangeMenuVisibility               -> changeMenuVisibility()
                topBarMultiselectBinding.multiSelectMenuBinding.imvDeleteFile  -> libraryViewModel.onClickDeleteParentItem()
                topBarHomeBinding.frameLayInBox                                -> libraryViewModel.onClickInBox()
                confirmDeletePopUp.btnCommitDelete                             -> libraryViewModel.onClickBtnCommitConfirm(v.tag as LibraryViewModel.ConfirmMode)
                confirmDeletePopUp.btnDenial                                   -> libraryViewModel.onClickBtnDenial(v.tag as LibraryViewModel.ConfirmMode)
            }
        }
    }
    fun resetView(binding: LibraryFragBinding){
        val makeGone = mutableListOf<View>()
        binding.apply {
            makeGone.addAll(arrayOf(
                topBarHomeBinding.root,
                topBarMultiselectBinding.root,
                topBarFileBinding.root,
                frameLayConfirmDelete
            ))
        }
        makeGone.onEach { it.visibility = View.GONE }
    }
    private fun addToClickableItem(binding: LibraryFragBinding){
        binding.apply {
            LibFragClickListenerItem.addAll(
                arrayOf(
                    topBarMultiselectBinding.imvClose,
                    topBarMultiselectBinding.imvChangeMenuVisibility,
                    topBarMultiselectBinding.multiSelectMenuBinding.imvDeleteFile,
                    topBarHomeBinding.frameLayInBox,
                    confirmDeletePopUp.btnCommitDelete,
                    confirmDeletePopUp.btnDenial,
                    )
            )
        }
       LibFragClickListenerItem.onEach { it.setOnClickListener(this) }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
class LibRVClickListener(val view:View,
                         val context: Context,
                         val item: LibraryRV,
                         private val createFileViewModel: CreateFileViewModel,
                         private val libraryViewModel: LibraryViewModel,
                         private val createCardViewModel: CreateCardViewModel,
                         private val rvBinding: LibraryFragRvItemBaseBinding):MyTouchListener(context){

//    click後にtouch event を設定しなおすと親子関係のコンフリクトが防げそう
    override fun onSingleTap() {
        super.onSingleTap()
        rvBinding.apply {
            when(view){
                baseContainer       ->  {
                    when(view.tag){
                        LibRVState.Selectable -> {
                            libraryViewModel.onClickSelectableItem(item,btnSelect.isSelected.not())
                            btnSelect.isSelected = btnSelect.isSelected.not()
                        }
                        LibRVState.LeftSwiped -> {
                            btnEditWhole.visibility = View.GONE
                            btnDelete.visibility = View.GONE
                            view.tag = LibRVState.Plane
                        }
                        LibRVState.Plane -> {
                            when(item.type){
                                LibRVViewType.Folder,LibRVViewType.FlashCardCover -> libraryViewModel.openNextFile(item)
                                LibRVViewType.StringCard -> createCardViewModel.onClickEditCard(item)
                            }
                        }
                    }
                }
                btnDelete       -> libraryViewModel.onClickDeleteRVItem(item)
                btnEditWhole    -> createFileViewModel.onClickEditFileInRV(item.file!!)
                btnAddNewCard   -> createCardViewModel.onClickRVAddNewCard(item)
            }
        }
    }
    override fun onLongClick() {
        super.onLongClick()
        rvBinding.btnSelect.isSelected = true
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.onClickSelectableItem(item,true)
    }
    override fun onSwipeLeft() {
        super.onSwipeLeft()
        rvBinding.apply {
            when(view){
                baseContainer -> {
                    btnDelete.visibility = View.VISIBLE
                    btnEditWhole.visibility = if(item.type==LibRVViewType.Folder||item.type == LibRVViewType.FlashCardCover) View.VISIBLE else return
                }
            }
        }
    }
}
