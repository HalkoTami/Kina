package com.example.tangochoupdated.ui.library

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
import com.example.tangochoupdated.room.enumclass.LibRVState
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.mainactivity.BaseViewModel


class HomeFragment : Fragment(),DataClickListener,View.OnClickListener {
    private val args: HomeFragmentArgs by navArgs()

    private lateinit var myNavCon:NavController
    lateinit var recyclerView:RecyclerView
    lateinit var adapter: LibraryListAdapter
    private val  homeFragClickListenerItem = mutableListOf<View>()

    private val sharedViewModel: BaseViewModel by activityViewModels()
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val stringCardViewModel : StringCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()

    private var _binding: FragmentLibraryHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //        FragmentのviewBinding定義
        _binding = FragmentLibraryHomeBinding.inflate(inflater, container, false)
//       NavControllerの定義
        myNavCon = requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).id)
//        recyclerViewの定義
        recyclerView = binding.vocabCardRV
        adapter = LibraryListAdapter(this,requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.isNestedScrollingEnabled = false

//        clickListenerを追加
        homeFragClickListenerItem.apply {
            binding.apply {
                topMenuBarFrame.imvSwitchMenu.setPadding(30)
                topMenuBarFrame.apply {
                    layEnd.visibility = View.GONE
                    addAll(arrayOf(
                        imvFileStatusOrClose,imvSwitchMenu))
                    menuBinding.apply {
                        root.visibility =View.VISIBLE
                        addAll(arrayOf(
                            imvDeleteFile,imvEditFile,imvAnki))
                    }
                }
                popupConfirmDelete.apply {
                    addAll(arrayOf(btnCommitDelete,btnDenial,btnCloseConfirm))
                }
            }
            onEach { it.setOnClickListener(this@HomeFragment) }
        }
//        －－－－LibraryViewModelの読み取り－－－－

        libraryViewModel.apply {
//            初期設定
            onStart()
//            －－－－DBから降ろしてきたデータをviewModelに設定－－－－

//            親アイテムのid
            val myId: Int? = args.parentItemId?.single()
            parentFileFromDB(myId).observe(viewLifecycleOwner){
                setParentFileFromDB(it, myId == null)
            }
            childFilesFromDB(myId).observe(viewLifecycleOwner){
                setChildFilesFromDB(it,myId == null)
            }
            childCardsFromDB(myId).observe(viewLifecycleOwner){
                setChildCardsFromDB(it,myId ==null)
            }
//            pAndGP(myId).observe(viewLifecycleOwner){
//                setPAndG(it)
//            }


            deletingItem.observe(viewLifecycleOwner){ list ->
                if(list.isEmpty().not()){
                    val childFileIds = mutableListOf<Int>()
                    list.onEach {
                        when(it.type) {
                            LibRVViewType.FlashCardCover,LibRVViewType.Folder -> childFileIds.add(it.file!!.fileId)
                        }
                    }
                    getAllDescendantsByFileId(list[0].file?.fileId).observe(viewLifecycleOwner){
                        setDeletingItemChildrenFiles(it)
                        Toast.makeText(requireContext(),"${it.size}",Toast.LENGTH_SHORT).show()
                    }
                }

            }


//            －－－－－－－－
//            －－－－UIへの反映－－－－

//            recyclerView
            multipleSlectMode.observe(viewLifecycleOwner){ selectable->
                recyclerView.children.iterator().forEachRemaining {
                    it.findViewById<ConstraintLayout>(R.id.base_container).tag = if (selectable) LibRVState.Selectable else LibRVState.Plane
                    it.findViewById<ImageView>(R.id.btn_select).visibility = if (selectable) View.VISIBLE else View.GONE
                }
            }
            myFinalList.observe(viewLifecycleOwner){
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
                binding.emptyBinding.root.visibility =
                if(it.isEmpty()) View.VISIBLE else View.GONE
            }

            binding.apply {
//                トップバー
                topMenuBarFrame.apply {
                    topBarUI.observe(viewLifecycleOwner){
                        imvFileStatusOrClose.apply {
                            if(tag != it.topBarLeftIMVDrawableId){
                                setImageDrawable(requireActivity().getDrawable(it.topBarLeftIMVDrawableId))
                                tag = it.topBarLeftIMVDrawableId
                            }
                        }
                        imvSwitchMenu.apply{
                            if(tag != it.topBarRightDrawableId){
                                setImageDrawable(requireActivity().getDrawable(it.topBarRightDrawableId))
                                tag = it.topBarRightDrawableId
                            }
                        }
                        if(txvTitle.text!=it.topBarText){
                            txvTitle.text = it.topBarText
                        }
                    }
//                    menuViewMode.observe(viewLifecycleOwner){
//                        when(it){
//                            true -> this.layEnd.visibility = View.VISIBLE
//                            false -> this.layEnd.visibility = View.GONE
//                        }
//                    }
                }

//                空だった時
                emptyBinding.apply {
                    fileEmptyText.observe(viewLifecycleOwner){
                        txvCenter.text = it
                    }
//                    fileEmptyStatus.observe(viewLifecycleOwner){
//                        when(it){
//                            true -> {
//                                root.visibility = View.VISIBLE
//                            }
//                            false ->{
//                                root.visibility = View.GONE
//                            }
//                        }
//                    }
                }
//                削除確認のポップアップ
                popupConfirmDelete.apply {
                    confirmPopUp.observe(viewLifecycleOwner){
                        if(it.visible) visibility = View.VISIBLE
                        else if(it.visible.not()) visibility = View.GONE
                        txvConfirmDelete.text = it.txvConfirmText
                        btnDenial.text = it.btnDenialText
                        btnDenial.tag = it.confirmMode
                        btnCommitDelete.text = it.btnCommitConfirmText
                        btnCommitDelete.tag = it.confirmMode
                    }

                }
            }

//            －－－－－－－－
        }

//        －－－－－－－－
        val root: View = binding.root
        return root
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



    override fun onClick(v: View?) {
        binding.apply {
            topMenuBarFrame.apply {
                when(v){
                    imvFileStatusOrClose -> if(v.tag== R.drawable.icon_close) libraryViewModel.setMultipleSelectMode(false)
                    imvSwitchMenu -> {
                        when(v.tag){
                            R.drawable.icon_dot -> layEnd.visibility =  if(layEnd.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                            R.drawable.icon_inbox -> libraryViewModel.onClickInBox()
                        }

                    }
                }

                menuBinding.apply {
                    when(v){
                        this.imvAnki ->{}
                        this.imvEditFile -> {
                           createFileViewModel.onClickEditFile(null)
                        }
//                        imvDeleteFile -> libraryViewModel.onDelete()
                    }
                }
                popupConfirmDelete.apply {
                    when(v){
                        btnCommitDelete -> libraryViewModel.onClickBtnCommitConfirm(v.tag as LibraryViewModel.ConfirmMode)
                        btnDenial -> libraryViewModel.onClickBtnDenial(v.tag as LibraryViewModel.ConfirmMode)
                    }
                }
            }
        }
    }


    override fun onLongClickMain(item: LibraryRV) {
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.onClickSelectableItem(item,true)
//        recyclerView.children.iterator().forEachRemaining {
//            it.findViewById<ImageView>(R.id.btn_select).visibility = View.VISIBLE
//        }

    }
    override fun onClickEdit(item: LibraryRV) {
        createFileViewModel.onClickEditFile(item.file)
        createFileViewModel

    }
    override fun onClickDelete(item: LibraryRV) {
        libraryViewModel.onClickDeleteRVItem(item)
    }
    override fun onClickAddNewCardByPosition(item: LibraryRV) {
        createCardViewModel.onClickRVAddNewCard(item)
    }
    override fun onClickSelectableItem(item: LibraryRV, selected: Boolean) {
        libraryViewModel.onClickSelectableItem(item,selected)
    }
    override fun openNext(item: LibraryRV) {
        when(item.type){
            LibRVViewType.Folder,LibRVViewType.FlashCardCover -> libraryViewModel.openNextFile(item)
            LibRVViewType.StringCard -> createCardViewModel.onClickEditCard(item)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
class LibRVClickListener(val view:View,
                         val context: Context,
                         val item: LibraryRV,
                         val clickListener: DataClickListener,
                         val rvBinding: ItemCoverCardBaseBinding):MyTouchListener(context){

//    click後にtouch event を設定しなおすと親子関係のコンフリクトが防げそう
    override fun onSingleTap() {
        super.onSingleTap()
        rvBinding.apply {
            when(view){
                baseContainer ->  {
                    when(view.tag){
                        LibRVState.Selectable -> {
                            clickListener.onClickSelectableItem(item,btnSelect.isSelected.not())
                            btnSelect.isSelected = btnSelect.isSelected.not()
                        }
                        LibRVState.LeftSwiped -> {
                            btnEditWhole.visibility = View.GONE
                            btnDelete.visibility = View.GONE
                            view.tag = LibRVState.Plane
                        }
                        LibRVState.Plane -> clickListener.openNext(item)

                    }
                }
                btnDelete -> clickListener.onClickDelete(item)
                btnEditWhole -> clickListener.onClickEdit(item)
                btnAddNewCard -> clickListener.onClickAddNewCardByPosition(item)

            }

        }
    }

    override fun onLongClick() {
        super.onLongClick()
        rvBinding.btnSelect.isSelected = true
//        rvBinding.btnSelect.visibility = View.VISIBLE
        clickListener.onLongClickMain(item)

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
