package com.example.tangochoupdated.ui.library

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Insets.add
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.Transition
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.EmptyBinding

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.anki.AnkiViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.math.abs

class HomeFragment : Fragment(),DataClickListener{
    private val args: HomeFragmentArgs by navArgs()




    lateinit var adapter:LibraryListAdapter

    private val sharedViewModel: BaseViewModel by activityViewModels()

    private var _binding: FragmentLibraryHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var libraryViewModel: LibraryViewModel

    lateinit var myparentItem:LibraryRV

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val repository = (requireActivity().application as RoomApplication).repository
        val viewModelFactory = ViewModelFactory(repository)
        libraryViewModel =
            ViewModelProvider(this,viewModelFactory
            )[LibraryViewModel::class.java]
        _binding = FragmentLibraryHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val myId:Int? = args.parentItemId?.single()







        binding.topMenuBarFrame.layEnd.visibility =View.GONE












//        sharedViewModel.finalList().observe(requireActivity()){
//            adapter.submitList(it)
//            sharedViewModel.libOrder = adapter.itemCount
//            libraryViewModel.selectedAmount.apply {
//                value = it.filter { it.selected }.size
//            }
//
//        }
//        libraryViewModel.selectedAmount.observe(requireActivity()){
//            binding.topMenuBarFrame.txvTitle.text = "${it.toString()}"
//        }






        val recyclerView = binding.vocabCardRV
        adapter = LibraryListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val owner = requireActivity()




        libraryViewModel.apply {


            multipleSelectMode.apply {
                value = false
            }






            getFinalList(args.parentItemId?.size).observe(requireActivity()){containingList->

                if(containingList.isEmpty()){
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
            multipleSelectMode.observe(owner){ multipleSelectMode->

                binding.topMenuBarFrame.apply {
                    val topText:String
                    val imv1Draw:Drawable
                    val imvEndDrae:Drawable

                    if (args.parentItemId == null&&!multipleSelectMode){
                        topText = "home"
                        imv1Draw = owner.getDrawable(R.drawable.icon_eye_opened)!!
                        imvEndDrae = owner.getDrawable(R.drawable.icon_inbox)!!
                        imvEnd.setOnClickListener {
                            onClickInBox()
                        }

                    } else{
                        if(multipleSelectMode){
                            imv1Draw= owner.getDrawable(R.drawable.icon_close)!!
                            topText = "選択中"
                            imv1.setOnClickListener {
                                changeMultiSelectMode(false)
                            }
                        } else{
                            val a = parentFile(1).value
                            imv1Draw = owner.getDrawable(R.drawable.icon_file)!!
//                            when(a!!.type){
//                                LibRVViewType.Folder -> imv1Draw = owner.getDrawable(R.drawable.icon_file)!!
//                                LibRVViewType.FlashCardCover -> imv1Draw = owner.getDrawable(R.drawable.icon_library_plane)!!
//                                else -> illegalDecoyCallException("unknown Type")
//                            }
                            topText= "${a?.title}"
                        }



                        imvEnd.setOnClickListener {
                            onclickMenuDot()
                        }
                        imvEndDrae = owner.getDrawable(R.drawable.icon_dot)!!

                    }


                    txvTitle.text = topText
                    imv1.setImageDrawable(imv1Draw)
                    imvEnd.setImageDrawable(imvEndDrae)
                }





            }



            menuOpened.observe(owner){
                if (it){
                    binding.topMenuBarFrame.layEnd.apply {
                        if (this.layoutTransition == null){
                            layoutTransition = LayoutTransition()
                            layoutTransition.apply {
                                enableTransitionType(LayoutTransition.CHANGING)
                                setDuration(200)
                            }
                        }
                        visibility = View.VISIBLE
                        layoutParams.width = 0
                        layoutParams.width = 170

                    }
                } else{
                    binding.topMenuBarFrame.layEnd.visibility = View.GONE
                }
            }
        }

        return root


    }

    fun onclickMenuDot(){
        libraryViewModel.menuOpened.apply { 
            value != value
        }
    }

    fun onClickInBox(){

    }
    fun changeMultiSelectMode(multiselectMode:Boolean){

            libraryViewModel.getFinalList(args.parentItemId?.single()).observe(requireActivity()) {
                it?.onEach { it.selectable = multiselectMode }
                adapter.notifyDataSetChanged()
            }
        libraryViewModel.multipleSelectMode.apply{
            value = multiselectMode
        }
    }


    override fun onLongClickMain() {
        changeMultiSelectMode(true)

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
        libraryViewModel.getFinalList(null).observe(requireActivity()) {
            it?.get(item.position)?.selected = true
        }
        imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_selected))
        Toast.makeText(context, "onSelect", Toast.LENGTH_SHORT).show()
    }

    override fun onUnselect(item: LibraryRV,imageView: ImageView) {
        libraryViewModel.getFinalList(null).observe(requireActivity()) {
            it?.get(item.position)?.selected = false
        }
        imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_select))
        Toast.makeText(context, "onUnselect", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
        }
