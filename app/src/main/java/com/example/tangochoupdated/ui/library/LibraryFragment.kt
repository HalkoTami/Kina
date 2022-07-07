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
import androidx.recyclerview.widget.LinearLayoutManager
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

        val recyclerView = binding.vocabCardRV
        adapter = LibraryListAdapter(this,requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val owner = requireActivity()
        navCon = requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).id)

        libraryViewModel.onStart()


//　　　　DBからデータとってくる
        libraryViewModel.apply {
            setParentItemId(15)


            fileWithoutParentFromDB().observe(viewLifecycleOwner){
                setFileWithoutParentFromDB(it, myId == null)
            }
            childFilesFromDB(myId).observe(viewLifecycleOwner){
                setChildFilesFromDB(it,myId == null)
            }
            childCardsFromDB(myId).observe(viewLifecycleOwner){
                setChildCardsFromDB(it,myId ==null)
            }
            parentFileFromDB(myId).observe(viewLifecycleOwner){
                setParentFileFromDB(it, myId == null)
                createFileViewModel.setParentFile(it)

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
            adapter.notifyDataSetChanged()
            createFileViewModel.setNewPosition(it.size+1)
        }
        binding.topMenuBarFrame.imv1.setOnClickListener{
            libraryViewModel.onClickLeftIcon()
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
            libraryViewModel.makeItemLeftSwiped(item.position)
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnEditWhole.visibility = View.VISIBLE
        }
    }

    override fun onLongClickMain(item: LibraryRV) {
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.onclickSelectableItem(item.position,true)


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
            Toast.makeText(context, "all reset", Toast.LENGTH_SHORT).show()
            return
        } else{
            val action= HomeFragmentDirections.libraryToLibrary()
            action.parentItemId = intArrayOf(item.id)
            navCon.navigate(action)
            Toast.makeText(context, "onClickMain", Toast.LENGTH_SHORT).show()
        }



    }

    override fun onSelect(item: LibraryRV,imageView: ImageView) {
        libraryViewModel.onclickSelectableItem(item.position,true)

        Toast.makeText(context, "onSelect", Toast.LENGTH_SHORT).show()
    }

    override fun onUnselect(item: LibraryRV,imageView: ImageView) {
        libraryViewModel.onclickSelectableItem(item.position,false)

        Toast.makeText(context, "onUnselect", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
        }
