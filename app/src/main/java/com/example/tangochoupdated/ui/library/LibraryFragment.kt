package com.example.tangochoupdated.ui.library

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
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
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.anki.AnkiFragmentDirections
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
                    addAll(arrayOf(
                        imvFileStatusOrClose,imvSwitchMenu))
                    menuBinding.apply {
                        addAll(arrayOf(
                            imvDeleteFile,imvEditFile,imvAnki))
                    }
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
            pAndGP(myId).observe(viewLifecycleOwner){
                setPAndG(it)
            }
            getAllDescendants(myId).observe(viewLifecycleOwner){
                setAllDescendants(it)
                binding.bindingSearch.edtLibrarySearch.hint = it?.size.toString()
            }

//            －－－－－－－－
//            －－－－UIへの反映－－－－

//            recyclerView
            multipleSlectMode.observe(viewLifecycleOwner){ selectable->
                recyclerView.children.iterator().forEachRemaining {
                    it.findViewById<ConstraintLayout>(R.id.base_container).tag = if (selectable) LibRVState.Selectable else LibRVState.Plane
                    it.findViewById<ImageView>(R.id.btn_select).visibility = if(selectable) View.VISIBLE else View.GONE
                }
            }
            myFinalList.observe(viewLifecycleOwner){
                adapter.submitList(it)
            }

            binding.apply {
//                トップバー
                topMenuBarFrame.apply {
                    topBarLeftIMVDrawableId.observe(viewLifecycleOwner){
                        this.imvFileStatusOrClose.setImageDrawable(requireActivity().getDrawable(it))
                    }
                    topText.observe(viewLifecycleOwner){
                        this.txvTitle.text = it
                    }
                    topBarRightIMVDrawableId.observe(viewLifecycleOwner){
                        this.imvSwitchMenu.setImageDrawable(requireActivity().getDrawable(it))
                    }
                    menuViewMode.observe(viewLifecycleOwner){
                        when(it){
                            true -> this.layEnd.visibility = View.VISIBLE
                            false -> this.layEnd.visibility = View.GONE
                        }
                    }
                }

//                空だった時
                emptyBinding.apply {
                    fileEmptyText.observe(viewLifecycleOwner){
                        txvCenter.text = it
                    }
                    fileEmptyStatus.observe(viewLifecycleOwner){
                        when(it){
                            true -> {
                                root.visibility = View.VISIBLE
                            }
                            false ->{
                                root.visibility = View.GONE
                            }
                        }
                    }
                }
            }

//            －－－－－－－－
        }

//        －－－－－－－－
        val root: View = binding.root
        return root
    }



    override fun onClick(v: View?) {
        binding.apply {
            topMenuBarFrame.apply {
                when(v){
                    imvFileStatusOrClose -> libraryViewModel.setMultipleSelectMode(false)
                }

                menuBinding.apply {
                    when(v){
                        this.imvAnki ->{}
                        this.imvEditFile -> {
                           createFileViewModel.onClickEditFile(null)
                        }
                        imvDeleteFile -> libraryViewModel.onDelete()
                    }
                }
            }
        }
    }


    override fun onLongClickMain(item: LibraryRV) {
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.onClickSelectableItem(item,true)
    }
    override fun onClickEdit(item: LibraryRV) {
        createFileViewModel.onClickEditFile(item.file)
        createFileViewModel
        Toast.makeText(context, "onclick edit", Toast.LENGTH_SHORT).show()
    }
    override fun onClickDelete(item: LibraryRV) {
        Toast.makeText(context, "onClickDelete", Toast.LENGTH_SHORT).show()
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
class LibRVTouchListener(val view:View,
                         context: Context,
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
        rvBinding.apply {
            when(view){
                baseContainer -> {
                    btnSelect.isSelected = true
                    clickListener.onLongClickMain(item)
                }
            }

        }

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
