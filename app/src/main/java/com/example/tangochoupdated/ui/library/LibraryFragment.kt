package com.example.tangochoupdated.ui.library

import android.animation.*
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.EmptyBinding

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.delay

class HomeFragment : Fragment(),DataClickListener {
    private val args: HomeFragmentArgs by navArgs()


    lateinit var adapter: LibraryListAdapter

    private val sharedViewModel: BaseViewModel by activityViewModels()

    private var _binding: FragmentLibraryHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var libraryViewModel: LibraryViewModel
    var menuViewStatus = false
    var multipleSelectModeStatus = false


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

        libraryViewModel.apply {
            setParentItemId(myId)

            setMenuStatus(false)
            menuViewMode.observe(viewLifecycleOwner) {
                menuViewStatus = it
                binding.topMenuBarFrame.layEnd.apply {
                    val b = ValueAnimator.ofInt(
                        1,
                        binding.topMenuBarFrame.menuBinding.root.layoutParams.width
                    )
                    b.duration = 300
                    b.addUpdateListener { animator ->

                        layoutParams.width = animator.animatedValue as Int
                        invalidate()
                        requestLayout()
                    }
                    when {
                        menuViewStatus -> {
                            layoutParams.width = 1
                            visibility = View.VISIBLE
                            b.start()

                        }
                        !menuViewStatus -> {

                            b.reverse()
                            b.doOnEnd { visibility = View.GONE }

                        }
                    }

                }
            }
            setMultipleSelectMode(false)
            multipleSelectMode.observe(viewLifecycleOwner) {
                multipleSelectModeStatus = it
            }
            card.observe(viewLifecycleOwner) {
                setValueToCards(it)
                chooseValuesOfFinalList()
            }
            file.observe(viewLifecycleOwner) {
                setValueToFiles(it)
                chooseValuesOfFinalList()
            }
            noParents.observe(viewLifecycleOwner) {
                setValueToNoParents(it)
                chooseValuesOfFinalList()
            }

            parentItem.observe(viewLifecycleOwner) {
                setValueToMyParentItem(it)

            }

            selectedItems.observe(viewLifecycleOwner) {
                if (multipleSelectModeStatus) {
                    binding.topMenuBarFrame.txvTitle.text = "${it.size}個　選択中"
                }

            }
            myFinalList.observe(viewLifecycleOwner) { containingList ->
                setValueToSelectedItem(containingList)


                if (containingList.isNullOrEmpty()) {
                    val a = EmptyBinding.inflate(layoutInflater)
                    a.apply {
                        txvCenter.text = "${myId}は空です"
                        root.visibility = View.VISIBLE
                    }
                    binding.scrollView.removeAllViewsInLayout()
                    binding.scrollView.addView(a.root)

                } else {
                    adapter.submitList(containingList)
                    adapter.notifyDataSetChanged()

                }

            }
            multipleSelectMode.observe(viewLifecycleOwner) {

                when (multipleSelectModeStatus) {

                    true -> {


                        binding.topMenuBarFrame.apply {

                            imv1.apply {
                                setImageDrawable(owner.getDrawable(R.drawable.icon_close)!!)
                                setOnClickListener {
                                    libraryViewModel.setMultipleSelectMode(false)
                                    changeAllSelectableState(false)
                                }
                            }
                            if (myId == null) {
                                imvEnd.apply {
                                    setImageDrawable(owner.getDrawable(R.drawable.icon_dot)!!)
                                    setOnClickListener {

                                        setMenuStatus(!menuViewStatus)
                                        Toast.makeText(
                                            context,
                                            "onclick menu",
                                            Toast.LENGTH_SHORT
                                        ).show()


                                    }
                                }
                            }
                        }
                    }


                    false -> {
                        when (myId) {
                            null -> {
                                binding.topMenuBarFrame.apply {
                                    txvTitle.text = "home"
                                    imv1.setImageDrawable(owner.getDrawable(R.drawable.icon_eye_opened)!!)
                                    imvEnd.apply {
                                        setImageDrawable(owner.getDrawable(R.drawable.icon_inbox)!!)
                                        setOnClickListener {

                                        }
                                    }
                                }


                            }
                            else -> {
                                myParentItem.observe(viewLifecycleOwner) { parent ->
                                    binding.topMenuBarFrame.apply {
                                        txvTitle.text =
                                            parent.single().file?.title.toString()
                                        imv1.setImageDrawable(
                                            when (parent.single().type) {
                                                LibRVViewType.Folder -> owner.getDrawable(R.drawable.icon_file)!!
                                                LibRVViewType.FlashCardCover -> owner.getDrawable(
                                                    R.drawable.icon_library_plane
                                                )!!
                                                else -> illegalDecoyCallException("unknown Type")
                                            }
                                        )
                                        imvEnd.apply {
                                            setImageDrawable(owner.getDrawable(R.drawable.icon_dot)!!)
                                            setOnClickListener {

                                            }
                                        }
                                    }


                                }
                            }
                        }

                    }
                }


            }
            myParentItem.observe(viewLifecycleOwner) { myParentItem ->
                when {
                    myId == null -> binding.topMenuBarFrame.menuBinding.imv1.setOnClickListener {
                        onDelete()
                    }
                }

            }

        }










        return root


    }
    fun onDelete(){
        Toast.makeText(context, "onClickDelete", Toast.LENGTH_SHORT).show()
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
        val navCon = requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.viewPager).id)
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
