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
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.anki.AnkiFragmentDirections

class HomeFragment : Fragment(),DataClickListener {
    private val args: HomeFragmentArgs by navArgs()
    lateinit var navCon:NavController


    lateinit var adapter: LibraryListAdapter

    private val sharedViewModel: BaseViewModel by activityViewModels()

    private var _binding: FragmentLibraryHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var libraryViewModel: LibraryViewModel

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
        adapter = LibraryListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val owner = requireActivity()
        navCon = requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).id)


//　　　　DBからデータとってくる
        libraryViewModel.apply {
            setParentItemId(myId)
            parentFileFromDB(myId).observe(viewLifecycleOwner){
                setParentFileFromDB(it)
                sharedViewModel.setParentFile(it)

            }

            fileWithoutParentFromDB.observe(viewLifecycleOwner){
                setFileWithoutParentFromDB(it)
            }
            childFilesFromDB.observe(viewLifecycleOwner){
                setChildFilesFromDB(it)
            }
            childCardsFromDB.observe(viewLifecycleOwner){
                setChildCardsFromDB(it)
            }

        }
//        初期データ設定
        libraryViewModel.apply {
            setMenuStatus(false)
        }


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
                    libraryViewModel.topBarRightIMVOnClick()
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



    fun onClickInBox(){

    }


    override fun onLongClickMain() {
        libraryViewModel.setMultipleSelectMode(true)
        libraryViewModel.changeAllSelectableState(true)

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

        val action= HomeFragmentDirections.libraryToLibrary()
        action.parentItemId = intArrayOf(item.id)
        navCon.navigate(action)
        Toast.makeText(context, "onClickMain", Toast.LENGTH_SHORT).show()

    }

    override fun onSelect(item: LibraryRV,imageView: ImageView) {
        libraryViewModel.changeSelectedState(true,item.position)
        imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_selected))
        Toast.makeText(context, "onSelect", Toast.LENGTH_SHORT).show()
    }

    override fun onUnselect(item: LibraryRV,imageView: ImageView) {
        libraryViewModel.changeSelectedState(false,item.position)
        imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_select))
        Toast.makeText(context, "onUnselect", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
        }
