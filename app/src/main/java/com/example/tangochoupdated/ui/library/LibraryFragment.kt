package com.example.tangochoupdated.ui.library

import android.animation.*
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.EmptyBinding

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.anki.AnkiFragmentDirections
import com.example.tangochoupdated.ui.planner.CreateFileViewModel

class HomeFragment : Fragment(),DataClickListener {
    private val args: HomeFragmentArgs by navArgs()
    lateinit var navCon:NavController

    lateinit var recyclerView:RecyclerView


    lateinit var adapter: LibraryListAdapter

    private val sharedViewModel: BaseViewModel by activityViewModels()
    private val createFileViewModel: CreateFileViewModel by activityViewModels()

    private var _binding: FragmentLibraryHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var libraryViewModel: LibraryViewModel
    var leftSwiped = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val repository = (requireActivity().application as RoomApplication).repository
        val viewModelFactory = ViewModelFactory(repository)
        libraryViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[LibraryViewModel::class.java]


        _binding = FragmentLibraryHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val myId: Int? = args.parentItemId?.single()

        recyclerView = binding.vocabCardRV
        adapter = LibraryListAdapter(this,requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())



        val owner = requireActivity()
        navCon = requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).id)

        libraryViewModel.onStart()


//　　　　DBからデータとってくる
        libraryViewModel.apply {
            setParentItemId(myId)


            fileWithoutParentFromDB().observe(viewLifecycleOwner){
                setFileWithoutParentFromDB(it, myId == null)
            }
            childFilesFromDB(myId).observe(viewLifecycleOwner){
                setChildFilesFromDB(it,myId == null)


            }
//            childCardsFromDB(myId).observe(viewLifecycleOwner){
//                setChildCardsFromDB(it,myId ==null)
//            }
            parentFileFromDB(myId).observe(viewLifecycleOwner){
                setParentFileFromDB(it, myId == null)
                createFileViewModel.setParentFile(it)
            }
            pAndGP(myId).observe(viewLifecycleOwner){
                setPAndG(it)
                createFileViewModel.setPAndG(it)
                var ids =" "
                var a = 0
                while(a<it!!.size){
                    val id = " ${it[a].fileId}, "
                    ids = "$ids $id"
                    a++

                }
                binding.bindingSearch.edtLibrarySearch.hint = ids
            }
        }
//        初期データ設定



        binding.apply {
            topMenuBarFrame.apply {
                libraryViewModel.topBarLeftIMVDrawableId.observe(viewLifecycleOwner){
                    this.imv1.setImageDrawable(owner.getDrawable(it))
                }
                libraryViewModel.topText.observe(viewLifecycleOwner){
                    this.txvTitle.text = it
                }
                libraryViewModel.topBarRightIMVDrawableId.observe(viewLifecycleOwner){
                    this.imvEnd.setImageDrawable(owner.getDrawable(it))
                }
                libraryViewModel.menuViewMode.observe(viewLifecycleOwner){
                    when(it){
                        true -> this.layEnd.visibility = View.VISIBLE
                        false -> this.layEnd.visibility = View.GONE
                    }

                }
                imv1.setOnClickListener {
                    recyclerView.children.iterator().forEachRemaining {
                        val a = it.findViewById<ImageView>(R.id.btn_select)
                        a.visibility = View.GONE
                        a.tag = MyState.Unselected
                    }
                    libraryViewModel.onClickLeftIcon()

                }
                imvEnd.setOnClickListener {
                    libraryViewModel.changeMenuStatus()
                }

                menuBinding.root.children.iterator().forEachRemaining {
                    when (it.id){
                       menuBinding.imv1.id ->{
                           it.setOnClickListener {
                               onClickAnki()
                           }
                       }
                        menuBinding.imv2.id -> {
                            it.setOnClickListener{
                                onClickEdit()
                            }
                        }
                        menuBinding.imv3.id ->{
                            it.setOnClickListener { onClickDelete() }
                        }
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







        return root


    }
    fun onClickDelete(){
        libraryViewModel.onDelete()
        Toast.makeText(context, "onClickDelete", Toast.LENGTH_SHORT).show()
    }
    fun onClickAnki(){
        val action = AnkiFragmentDirections.toAnki()
        navCon.navigate(action)
    }
    fun onClickEdit(){

    }

    override fun onSwipeLeft(item: LibraryRV, binding: ItemCoverCardBaseBinding) {
        if(item.selectable||leftSwiped){

            return
        } else{

            binding.btnDelete.visibility = View.VISIBLE
            binding.btnEditWhole.visibility = View.VISIBLE
            libraryViewModel.makeItemLeftSwiped(item.position)
            Toast.makeText(context, "onclick left swiped ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLongClickMain(item: LibraryRV,binding: ItemCoverCardBaseBinding) {
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.addToSelectedItem(item)
        recyclerView.children.iterator().forEachRemaining {
            it.findViewById<ImageView>(R.id.btn_select).visibility = View.VISIBLE
        }
        binding.btnSelect.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_selected))
        binding.btnSelect.tag = MyState.Selected

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

    override fun onClickMain(item: LibraryRV) {
        if(libraryViewModel.checkReset()){
            leftSwiped = false
            Toast.makeText(context, "all reset", Toast.LENGTH_SHORT).show()
            return
        } else{
            val action= HomeFragmentDirections.libraryToLibrary()
            action.parentItemId = intArrayOf(item.id)
            navCon.navigate(action)
            Toast.makeText(context, "onClickMain", Toast.LENGTH_SHORT).show()
        }



    }
    enum class MyState{
        Selected, Unselected
    }

    override fun onSelect(item: LibraryRV,binding: ItemCoverCardBaseBinding) {

        binding.btnSelect.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_selected))
        binding.btnSelect.tag = MyState.Selected
        libraryViewModel.onclickSelectableItem(item,true)
        Toast.makeText(context, "onSelect", Toast.LENGTH_SHORT).show()
    }


    override fun onUnselect(item: LibraryRV,binding: ItemCoverCardBaseBinding) {

        binding.btnSelect.setImageDrawable(requireContext().getDrawable(R.drawable.circle_select))
        binding.btnSelect.tag = MyState.Unselected
        libraryViewModel.onclickSelectableItem(item,false)
        Toast.makeText(context, "onUnselect", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
        }
