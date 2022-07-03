package com.example.tangochoupdated.ui.library

import android.animation.*
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.*

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
        navCon = requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.viewPager).id)


//        libraryViewModel.apply {
//            setParentItemId(myId)
//
//            setMenuStatus(View.GONE)
//            fileWithoutParent.observe(viewLifecycleOwner){
//                setValueToFileWithoutParent(it)
//                chooseValuesOfFinalList()
//            }
////            menuViewMode.observe(viewLifecycleOwner) {
////                menuViewStatus = it
////                binding.topMenuBarFrame.layEnd.apply {
////                    val b = ValueAnimator.ofInt(
////                        1,
////                        binding.topMenuBarFrame.menuBinding.root.layoutParams.width
////                    )
////                    b.duration = 300
////                    b.addUpdateListener { animator ->
////
////                        layoutParams.width = animator.animatedValue as Int
////                        invalidate()
////                        requestLayout()
////                    }
////                    when {
////                        menuViewStatus -> {
////                            layoutParams.width = 1
////                            visibility = View.VISIBLE
////                            b.start()
////
////                        }
////                        !menuViewStatus -> {
////
////                            b.reverse()
////                            b.doOnEnd { visibility = View.GONE }
////
////                        }
////                    }
////
////                }
////            }
//            setMultipleSelectMode(false)
//            multipleSelectMode.observe(viewLifecycleOwner) {
//                when {
//                    it == true -> {
//                        binding.topMenuBarFrame.menuBinding.apply {
//                            imv1.setOnClickListener { onClickAnki() }
//                        }
//                    }
//                    else -> {
//                        when (myId) {
//                            null -> {
//                                binding.topMenuBarFrame.apply {
//                                    txvTitle.text = "home"
//                                    imv1.setImageDrawable(owner.getDrawable(R.drawable.icon_eye_opened)!!)
//                                    imvEnd.apply {
//                                        setImageDrawable(owner.getDrawable(R.drawable.icon_inbox)!!)
//                                        setOnClickListener {
//
//                                        }
//                                    }
//                                }
//
//
//                            }
//                            else -> {
//                                myParentItem.observe(viewLifecycleOwner) { parent ->
//                                    binding.topMenuBarFrame.apply {
//                                        txvTitle.text =
//                                            parent.file?.title.toString()
//                                        imv1.setImageDrawable(
//                                            when (parent.type) {
//                                                LibRVViewType.Folder -> owner.getDrawable(R.drawable.icon_file)!!
//                                                LibRVViewType.FlashCardCover -> owner.getDrawable(
//                                                    R.drawable.icon_library_plane
//                                                )!!
//                                                else -> illegalDecoyCallException("unknown Type")
//                                            }
//                                        )
//                                        imvEnd.apply {
//                                            setImageDrawable(owner.getDrawable(R.drawable.icon_dot)!!)
//                                            setOnClickListener {
//
//                                            }
//                                        }
//                                    }
//
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            card.observe(viewLifecycleOwner) {
//                setValueToCards(it)
//                chooseValuesOfFinalList()
//            }
//            file.observe(viewLifecycleOwner) {
//                setValueToFiles(it)
//                chooseValuesOfFinalList()
//            }
////            noParents.observe(viewLifecycleOwner) {
////                setValueToNoParents(it)
////                chooseValuesOfFinalList()
////            }
//
//            parentFile.observe(viewLifecycleOwner) {
//                getParentItem(it)
//
//            }
//
//
//            myFinalList.observe(viewLifecycleOwner) { containingList ->
//                setValueToSelectedItem()
//
//
//                if (containingList.isNullOrEmpty()) {
//                    val a = EmptyBinding.inflate(layoutInflater)
//                    a.apply {
//                        txvCenter.text = "${myId}は空です"
//                        root.visibility = View.VISIBLE
//                    }
//                    binding.scrollView.removeAllViewsInLayout()
//                    binding.scrollView.addView(a.root)
//
//                } else {
//                    adapter.submitList(containingList)
//                    adapter.notifyDataSetChanged()
//
//                }
//
//            }
//            selectedItems.observe(viewLifecycleOwner) { list->
//                binding.topMenuBarFrame.txvTitle.text = "${list.size}個　選択中"
//                binding.topMenuBarFrame.menuBinding.imv3.setOnClickListener {
//                    onClickDelete()
//                }
//
//            }
//            parentFile.observe(viewLifecycleOwner) {
//                getParentItem(it)
//
//            }
//            topText.observe(viewLifecycleOwner){
//                binding.topMenuBarFrame.txvTitle.text = it
//            }
//
//        }
//        初期データ設定
        libraryViewModel.apply {
            setParentItemId(myId)
            setMenuStatus(false)
            fileWithoutParent.observe(viewLifecycleOwner){
                setValueToFileWithoutParent(it)
                chooseValuesOfFinalList()
            }
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

                menuBinding.apply {
                    imv3.setOnClickListener {
                        libraryViewModel.deleteItem()
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
        libraryViewModel.deleteItem()
        Toast.makeText(context, "onClickDelete", Toast.LENGTH_SHORT).show()
    }
    fun onClickAnki(){
        val action = AnkiFragmentDirections.toAnki()
        navCon.navigate(action)
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
