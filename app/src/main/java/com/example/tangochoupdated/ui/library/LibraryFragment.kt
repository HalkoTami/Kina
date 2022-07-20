package com.example.tangochoupdated.ui.library

import android.animation.*
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
import com.example.tangochoupdated.databinding.ItemCoverFileBinding
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.anki.AnkiFragmentDirections
import com.example.tangochoupdated.ui.create.card.CreateCardFragmentDirections
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel

class HomeFragment : Fragment(),DataClickListener,View.OnClickListener {
    private val args: HomeFragmentArgs by navArgs()
    lateinit var mynavCon:NavController

    lateinit var recyclerView:RecyclerView


    lateinit var adapter: LibraryListAdapter
    val  homeFragClickListenerItem = mutableListOf<View>()

    private val sharedViewModel: BaseViewModel by activityViewModels()
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val stringCardViewModel : StringCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()

    private var _binding: FragmentLibraryHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var leftSwiped = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        val repository = (requireActivity().application as RoomApplication).repository
//        val viewModelFactory = ViewModelFactory(repository)
//        libraryViewModel =
//            ViewModelProvider(
//                this, viewModelFactory
//            )[LibraryViewModel::class.java]


        _binding = FragmentLibraryHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedViewModel.setActiveTab(Tab.TabLibrary)


        val myId: Int? = args.parentItemId?.single()

        recyclerView = binding.vocabCardRV
        adapter = LibraryListAdapter(this,requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())



        val owner = requireActivity()
        mynavCon = requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).id)


        libraryViewModel.onStart()
//        createCardViewModel.setParentFlashCardCoverId(args.parentItemId?.single())


//　　　　DBからデータとってくる
        libraryViewModel.apply {
            setParentItemId(myId)
            setNavCon(mynavCon)
            parentFileFromDB(myId).observe(viewLifecycleOwner){
                setParentFileFromDB(it, myId == null)
//                createFileViewModel.setParentFile(it)
                sharedViewModel.setParentFile(it)
            }

//            fileWithoutParentFromDB().observe(viewLifecycleOwner){
//                setFileWithoutParentFromDB(it, myId == null)
//            }
            childFilesFromDB(myId).observe(viewLifecycleOwner){
                setChildFilesFromDB(it,myId == null)


            }
            childCardsFromDB(myId).observe(viewLifecycleOwner){
                setChildCardsFromDB(it,myId ==null)
                createCardViewModel.setSisterCards(it)

            }

            pAndGP(myId).observe(viewLifecycleOwner){
                setPAndG(it)
                createFileViewModel.setPAndG(it)
                binding.bindingSearch.edtLibrarySearch.hint = it?.size.toString()

            }
        }







//        初期データ設定


        binding.topMenuBarFrame.imvSwitchMenu.setPadding(10)

        binding.apply {
            topMenuBarFrame.apply {
                libraryViewModel.topBarLeftIMVDrawableId.observe(viewLifecycleOwner){
                    this.imvFileStatusOrClose.setImageDrawable(owner.getDrawable(it))
                }
                libraryViewModel.topText.observe(viewLifecycleOwner){
                    this.txvTitle.text = it
                }
                libraryViewModel.topBarRightIMVDrawableId.observe(viewLifecycleOwner){
                    this.imvSwitchMenu.setImageDrawable(owner.getDrawable(it))
                }
                libraryViewModel.menuViewMode.observe(viewLifecycleOwner){
                    when(it){
                        true -> this.layEnd.visibility = View.VISIBLE
                        false -> this.layEnd.visibility = View.GONE
                    }

                }
                homeFragClickListenerItem.apply {
                    add(imvSwitchMenu)
                    add(imvFileStatusOrClose)
                }
                menuBinding.root.visibility = View.VISIBLE

                menuBinding.apply {
                    homeFragClickListenerItem.apply {
                        add(imvAnki)
                        add(imvDeleteFile)
                        add(imvEditFile)
                    }
                }
            }


            libraryViewModel.fileEmptyText.observe(viewLifecycleOwner){
                binding.emptyBinding.txvCenter.text = it
            }
            libraryViewModel.fileEmptyStatus.observe(viewLifecycleOwner){
                when(it){
                    true -> {
                        binding.emptyBinding.root.visibility = View.VISIBLE
                    }
                    false ->{
                        binding.emptyBinding.root.visibility = View.GONE
                    }
                }
            }




        }
        libraryViewModel.myFinalList.observe(viewLifecycleOwner){
            adapter.submitList(it)

            createFileViewModel.setNewPosition(it.size+1)
        }
        CustomLayout(requireContext())




        homeFragClickListenerItem.onEach {
            it.setOnClickListener(this)
        }


        return root


    }



    override fun onClick(v: View?) {
        binding.apply {
            topMenuBarFrame.apply {
                when(v){
                    imvFileStatusOrClose -> libraryViewModel.onClickImvFileStatusOrClose(recyclerView)
                    this.imvSwitchMenu -> libraryViewModel.onClickimvSwitchMenu()
                }

                menuBinding.apply {
                    when(v){
                        this.imvAnki ->{}
                        this.imvEditFile -> {
                           createFileViewModel.onClickImvEditFile()
                        }
                    }
                }
            }
        }


    }
    fun clicked(v: View?):Boolean{
        Toast.makeText(context, "${v?.id}", Toast.LENGTH_SHORT).show()
        return true
    }

    fun onClickDelete(){
        libraryViewModel.onDelete()
        Toast.makeText(context, "onClickDelete", Toast.LENGTH_SHORT).show()
    }
    fun onClickAnki(){
        val action = AnkiFragmentDirections.toAnki()
        mynavCon.navigate(action)
    }
    fun onClickEdit(){

    }

    override fun onCickEditCard(item: LibraryRV) {
        stringCardViewModel.setStringData(item.card!!.stringData)
        createCardViewModel.onClickEditCard(item)

    }

    override fun onSwipeLeft(item: LibraryRV, rvBinding: ItemCoverCardBaseBinding, fileBinding:ItemCoverFileBinding) {
        if((rvBinding.btnDelete.visibility == View.VISIBLE) or
            (rvBinding.btnEditWhole.visibility == View.VISIBLE)) return
        else  {
            val a = mutableListOf<Animator>()
            a.add(animateWidth(rvBinding.btnDelete, 0, 70,300))
            when(item.type){
                LibRVViewType.FlashCardCover, LibRVViewType.Folder ->
                    a.add(animateWidth(rvBinding.btnEditWhole,0,70,300))
                else -> return
            }
            AnimatorSet().apply {
                playTogether(a)
                start()
            }
//            homeFragClickListenerItem.onEach {
//                when(it.id){
//                    rvBinding.btnDelete.id,rvBinding.btnEditWhole.id -> return
//                    else -> {
//                        it.setOnClickListener(null)
//                        it.setOnClickListener {
//                            mutableListOf<View>(rvBinding.btnDelete,rvBinding.btnEditWhole).onEach {
//                                animateWidth(it,it.layoutParams.width,0,300).start()
//                                allChildrenMutableList.onEach {
//                                    it.setOnClickListener(this)
//                                }
//                            }
//
//                        }
//
//                    }
//
//                }
//            }
            libraryViewModel.setLeftSwipedItemExists(true)
            fileBinding.btnAdd.visibility = View.GONE


        }
    }

    override fun onLongClickMain(item: LibraryRV, rvBinding: ItemCoverCardBaseBinding) {
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.addToSelectedItem(item)
        recyclerView.children.iterator().forEachRemaining {
            it.findViewById<ImageView>(R.id.btn_select).visibility = View.VISIBLE
        }

        Toast.makeText(context, "onclick edit", Toast.LENGTH_SHORT).show()

    }

    override fun onClickEdit(item: LibraryRV) {
        Toast.makeText(context, "onclick edit", Toast.LENGTH_SHORT).show()

    }

    override fun onClickAdd(item: LibraryRV) {
        Toast.makeText(context, "onClickAdd", Toast.LENGTH_SHORT).show()
    }

    override fun onClickDelete(item: LibraryRV) {
        Toast.makeText(context, "onClickDelete", Toast.LENGTH_SHORT).show()
    }

    override fun onClickAddNewCardByPosition(item: LibraryRV) {
        createCardViewModel.onClickRVAddNewCard(item)
    }

    override fun onClickMain(item: LibraryRV, rvBinding: ItemCoverCardBaseBinding) {
        libraryViewModel.onClickRVItem(rvBinding,item)

//
//        if(rvBinding.btnSelect.visibility == View.VISIBLE){
//            when(rvBinding.btnSelect.tag){
//                MyState.Selected -> {
//                    onUnselect(item,rvBinding)
//                }
//                else -> {
//                    onSelect(item,rvBinding)
//                }
//            }
//        } else if(libraryViewModel.checkReset()){
//            recyclerView.children.iterator().forEachRemaining {
//                mutableListOf<View>(it.findViewById(R.id.btn_edit_whole),
//                    it.findViewById(R.id.btn_delete)).onEach {
//                        if(it.visibility == View.VISIBLE){
//                            animateWidth(it,it.layoutParams.width,0,300).start()
//                        }
//                }
//            }
//            libraryViewModel.setLeftSwipedItemExists(false)
//            Toast.makeText(context, "all reset", Toast.LENGTH_SHORT).show()
//            return
//        } else{
//            val action= HomeFragmentDirections.libraryToLibrary()
//            action.parentItemId = intArrayOf(item.id)
//            navCon.navigate(action)
//            Toast.makeText(context, "onClickMain", Toast.LENGTH_SHORT).show()
//        }



    }
    enum class MyState{
        Selected, Unselected
    }

    override fun onSelect(item: LibraryRV, rvBinding: ItemCoverCardBaseBinding) {

//        rvBinding.btnSelect.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_selected))
//        rvBinding.btnSelect.tag = MyState.Selected
//        libraryViewModel.onclickSelectableItem(item,true)
//        Toast.makeText(context, "onSelect", Toast.LENGTH_SHORT).show()
    }


    override fun onUnselect(item: LibraryRV, rvBinding: ItemCoverCardBaseBinding) {

//        rvBinding.btnSelect.setImageDrawable(requireContext().getDrawable(R.drawable.circle_select))
//        rvBinding.btnSelect.tag = MyState.Unselected
//        libraryViewModel.onclickSelectableItem(item,false)
//        Toast.makeText(context, "onUnselect", Toast.LENGTH_SHORT).show()
    }

    override fun onScrollLeft(distanceX: Float, rvBinding: ItemCoverCardBaseBinding) {
//        binding.btnDelete.visibility = View.VISIBLE
//        binding.btnEditWhole.visibility = View.VISIBLE
//        binding.stubMain.layoutParams.width = distanceX.toInt()
//        libraryViewModel.makeItemLeftSwiped()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
        }
