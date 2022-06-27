package com.example.tangochoupdated.ui.library

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.Transition
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.*

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
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




    lateinit var adapter:LibraryListAdapter

    private val sharedViewModel: BaseViewModel by activityViewModels()

    private var _binding: FragmentLibraryHomeBinding? = null
    var myparentItem:LibraryRV? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var libraryViewModel: LibraryViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLibraryHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        libraryViewModel =
            ViewModelProvider(this,
                ViewModelFactory((requireActivity().application as RoomApplication).repository)
            )[LibraryViewModel::class.java]







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






        val recyclerView = _binding?.vocabCardRV
        adapter = LibraryListAdapter(this)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)
        libraryViewModel.parentItem.apply {
            value = myparentItem
        }
        val owner = requireActivity()
        val myNowDraw = owner.getDrawable(R.drawable.icon_eye_opened)
        val dotMenu = owner.getDrawable(R.drawable.icon_dot)

        libraryViewModel.apply {
            


                getFinalList(myparentItem?.id).observe(owner){ containingList->
                    if(containingList?.size == 0){
                        val emptyMessageBinding = binding.layEmptyBinding.apply { 
                            root.visibility = View.VISIBLE
                            txvCenter.text = "${myparentItem?.file?.title}は空です"
                        }
                        binding.scrollView.addView(emptyMessageBinding.root)
                    } else {
                        adapter.submitList(containingList)
                    }
                }
                binding.topMenuBarFrame.apply {
                    val topText:String 
                    val imv1Draw:Drawable
                    val imvEndDrae:Drawable
                    if (parentItem.value == null){
                        topText = "home"
                        imv1Draw = owner.getDrawable(R.drawable.icon_eye_opened)!!
                        imvEndDrae = owner.getDrawable(R.drawable.icon_inbox)!!
                        imvEnd.setOnClickListener {
                            onClickInBox()
                        }
                        
                    } else{
                        when(myparentItem?.type){
                            LibRVViewType.Folder -> imv1Draw = owner.getDrawable(R.drawable.icon_file)!!
                            LibRVViewType.FlashCardCover -> imv1Draw = owner.getDrawable(R.drawable.icon_library_plane)!!
                            else -> illegalDecoyCallException("unknown Type")
                        }
                        topText= "${myparentItem?.file?.title}"

                        imvEnd.setOnClickListener {
                            onclickMenuDot()
                        }
                        imvEndDrae = owner.getDrawable(R.drawable.icon_dot)!!

                    }
                    txvTitle.text = topText
                    imv1.setImageDrawable(imv1Draw)
                    imvEnd.setImageDrawable(imvEndDrae)
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


    override fun onLongClickMain() {
        libraryViewModel.getFinalList(myparentItem?.id).observe(requireActivity()){
         it?.onEach { it.selectable = true }
        }
        adapter.notifyDataSetChanged()
        libraryViewModel.multipleSelectMode.apply { 
            value = true
        }

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
        Toast.makeText(context, "onClickMain", Toast.LENGTH_SHORT).show()
        libraryViewModel.parentItem.apply {
            value = item
        }
    }

    override fun onSelect(item: LibraryRV,imageView: ImageView) {
        libraryViewModel.getFinalList(myparentItem?.id).apply {
            value?.get(item.position)?.selected = true
        }
        imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_selected))
        Toast.makeText(context, "onSelect", Toast.LENGTH_SHORT).show()
    }

    override fun onUnselect(item: LibraryRV,imageView: ImageView) {

        libraryViewModel.getFinalList(myparentItem?.id).observe(requireActivity()) {
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
