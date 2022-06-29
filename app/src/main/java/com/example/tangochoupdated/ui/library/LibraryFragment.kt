package com.example.tangochoupdated.ui.library

import android.animation.*
import android.content.Context
import android.graphics.Insets.add
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.*
import android.util.AttributeSet
import android.util.Xml
import android.view.*
import android.view.animation.Animation
import android.view.animation.LayoutAnimationController
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.EmptyBinding

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
import com.example.tangochoupdated.databinding.MenuLayoutBinding
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
import kotlinx.coroutines.MainScope
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

    var myparentItem:LibraryRV? = null

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
        libraryViewModel.setParentItemId(myId)
        libraryViewModel.noParents.observe(viewLifecycleOwner){
            libraryViewModel.addValue(it)
        }
        libraryViewModel.setMenuView(false)







        binding.topMenuBarFrame.layEnd.visibility =View.GONE





        val recyclerView = binding.vocabCardRV
        adapter = LibraryListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val owner = requireActivity()





        libraryViewModel.apply {


            setMultipleSelectMode(false)

            myParentItem.observe(viewLifecycleOwner,){
                myparentItem = it
                multipleSelectMode.observe(owner){ multipleSelectMode->


                    binding.topMenuBarFrame.apply {
                        val topText:String
                        val imv1Draw:Drawable
                        val imvEndDrae:Drawable
                        if(multipleSelectMode){
                            imv1Draw= owner.getDrawable(R.drawable.icon_close)!!
                            topText = "選択中"
                            imv1.setOnClickListener {
                                changeMultiSelectMode(false)
                            }
                            imvEndDrae = owner.getDrawable(R.drawable.icon_dot)!!
                            imvEnd.setOnClickListener {
                                onclickMenuDot()
                            }
                        } else{
                            if (it   == null){
                                topText = "home"
                                imv1Draw = owner.getDrawable(R.drawable.icon_eye_opened)!!
                                imvEndDrae = owner.getDrawable(R.drawable.icon_inbox)!!
                                imvEnd.setOnClickListener {
                                    onClickInBox()
                                }

                            } else {
                                when(it.type){
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






                        }
                        txvTitle.text = topText
                        imv1.setImageDrawable(imv1Draw)
                        imvEnd.setImageDrawable(imvEndDrae)



                    }





                }

            }


            myFinalList.observe(viewLifecycleOwner){ containingList->

                if(containingList == null){
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




            menuViewMode.observe(owner){
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
        val a = binding.topMenuBarFrame.layEnd
        val b = ValueAnimator.ofInt(1,binding.topMenuBarFrame.menuBinding.root.layoutParams.width)


        b.duration = 300
        b.addUpdateListener{

            a.layoutParams.width = it.animatedValue as Int
            a.invalidate()
            a.requestLayout()
        }
        a.layoutParams.width = 1
        a.visibility = View.VISIBLE
        b.start()




        Toast.makeText(context, "onclick menu", Toast.LENGTH_SHORT).show()

    }
    fun makeVisible(layout:FrameLayout,visible: Boolean,maxWidth:Int?,maxHeight:Int?){
        binding.topMenuBarFrame.root.apply {
            if (layoutTransition == null){
                layoutTransition = LayoutTransition()
                layoutTransition.apply {
                    enableTransitionType(LayoutTransition.CHANGING)
                    setDuration(200)
                }
            }

            val a = binding.topMenuBarFrame.layEnd
            val b = binding.topMenuBarFrame.layEnd
            a.visibility = View.GONE
            b.visibility = View.VISIBLE
            val from = a as ViewGroup
            val to = b as View

            val anime = ChangeBounds()
            val scene:Scene = Scene(from,to)
            anime.apply {
                duration= 200

            }
            TransitionManager.go(scene,anime)

            layoutTransition = LayoutTransition()
            layoutTransition.apply {
                enableTransitionType(LayoutTransition.CHANGING)
                setDuration(200)

            }
            binding.topMenuBarFrame.layEnd.apply {
                if(visible){
                    if(maxWidth!=null){
                        layoutParams.width = 0
                        layoutParams.width = maxWidth
                    }
                    visibility= View.VISIBLE
                    if(maxHeight!=null){
                        layoutParams.height = 0
                        layoutParams.height = maxHeight
                    }

                } else{

                    if(maxWidth!=null){
                        layoutParams.width = maxWidth
                        layoutParams.width = 0
                    }
                    if(maxHeight!=null){
                        layoutParams.height = maxHeight
                        layoutParams.height = 0
                    }
                    visibility = View.GONE
                }
            }
        }

    }

    fun onClickInBox(){

    }
    fun changeMultiSelectMode(multiselectMode:Boolean){

        libraryViewModel.makeAllSelectable()

        libraryViewModel.setMultipleSelectMode(false)
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
        libraryViewModel.makeSelected(item.position)
        imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_selected))
        Toast.makeText(context, "onSelect", Toast.LENGTH_SHORT).show()
    }

    override fun onUnselect(item: LibraryRV,imageView: ImageView) {
        libraryViewModel.makeUnselected(item.position)
        imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.circle_select))
        Toast.makeText(context, "onUnselect", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
        }
