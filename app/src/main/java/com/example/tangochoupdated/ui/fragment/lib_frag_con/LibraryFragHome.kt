package com.example.tangochoupdated.ui.fragment.lib_frag_con


import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast.makeText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.enumclass.Toast
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.view_set_up.SearchViewModel
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBase
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.listadapter.LibFragSearchRVListAdapter
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.viewmodel.*
import java.io.File
import kotlin.math.absoluteValue


class LibraryFragHome : Fragment(){

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val stringCardViewModel: StringCardViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val searchViewModel:SearchViewModel by activityViewModels()
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()

    private var _binding: LibraryChildFragWithMulModeBaseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val mainNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).findNavController()
        libNavCon =  requireActivity().findNavController(R.id.lib_frag_con_view)
        _binding = LibraryChildFragWithMulModeBaseBinding.inflate(inflater, container, false)
        recyclerView = binding.vocabCardRV
        val adapter= LibFragPlaneRVListAdapter(
            createFileViewModel  = createFileViewModel,
            libraryViewModel  = libraryViewModel,
            context  = requireActivity(),
            stringCardViewModel  = stringCardViewModel,
            createCardViewModel  = createCardViewModel,
            parent = recyclerView as View,
            deletePopUpViewModel = deletePopUpViewModel,

            mainNavController = mainNavCon,
            libNavController = libNavCon,)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.isNestedScrollingEnabled = false
        libNavCon =  requireActivity().findNavController(R.id.lib_frag_con_view)
        val topBarBinding = LibraryFragTopBarHomeBinding.inflate(inflater,container,false)

        binding.rvCover.setOnTouchListener(object :MyTouchListener(requireActivity()){

            override fun onSingleTap(motionEvent:MotionEvent?) {
                super.onSingleTap(motionEvent)
                val view = recyclerView.findChildViewUnder(motionEvent!!.x,motionEvent!!.y)
                val lineLay = view?.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return
                val conLay = view?.findViewById<ConstraintLayout>(R.id.lib_rv_base_container) ?:return
                val btnSelect = view?.findViewById<ImageView>(R.id.btn_select) ?:return
                val btnDelete = view?.findViewById<ImageView>(R.id.btn_delete) ?:return
                val btnEditWhole = view?.findViewById<ImageView>(R.id.btn_edit_whole) ?:return
                val position = recyclerView.getChildAdapterPosition(view)
                val item = adapter.currentList[position] as com.example.tangochoupdated.db.dataclass.File
                conLay.apply {
                    when(view){
                        conLay       ->  {
                            if(libraryViewModel.returnLeftSwipedItemExists()==true){
                                libraryViewModel.makeAllUnSwiped()
                            }
                            else if(libraryViewModel.returnMultiSelectMode()==true){
                                libraryViewModel.onClickSelectableItem(item,btnSelect.isSelected.not())
                                btnSelect.isSelected = btnSelect.isSelected.not()
                            }else{
                                libraryViewModel.openNextFile(item,libNavCon)
                            }

                        }
                        btnSelect -> {
                            if(conLay.tag == LibRVState.SelectFileMoveTo) libraryViewModel.moveSelectedItemToFile(item)
                        }
                        btnDelete       -> {
                            deletePopUpViewModel.setDeletingItem(mutableListOf(item))
                            deletePopUpViewModel.setConfirmDeleteVisible(true)
                        }
                        btnEditWhole    -> createFileViewModel.onClickEditFileInRV(item)
                    }
                }
            }
            var startPosition :MotionEvent? = null
            var swipingDistance:Float = 1f
            override fun onScrollLeft(distanceX: Float, motionEvent: MotionEvent?) {
                super.onScrollLeft(distanceX, motionEvent)
                binding.frameLayTest.requestDisallowInterceptTouchEvent(true)

                startPosition = motionEvent
                swipingDistance = distanceX
                val view = recyclerView.findChildViewUnder(motionEvent!!.x,motionEvent!!.y)
                val lineLay = view?.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return
                val conLay = view?.findViewById<ConstraintLayout>(R.id.lib_rv_base_container) ?:return
                conLay.apply {
                    if(conLay.tag== LibRVState.Plane){
                        lineLay.layoutParams.width = 1
                        lineLay.requestLayout()
                        lineLay.children.iterator().forEach {
                            it.visibility = View.VISIBLE
                        }
                        lineLay.visibility = View.VISIBLE
                        conLay.tag = LibRVState.LeftSwiping

                    }else if(conLay.tag== LibRVState.LeftSwiping) {
                        if(conLay.tag!= LibRVState.LeftSwiping){
                            conLay.tag = LibRVState.LeftSwiping
                        }

                        lineLay.layoutParams.width = swipingDistance.toInt()/5 + 1
                        lineLay.requestLayout()

                    }

                }
            }
            override fun onLongClick(motionEvent: MotionEvent?) {
                super.onLongClick(motionEvent)
                val view = recyclerView.findChildViewUnder(motionEvent!!.x,motionEvent!!.y)
                val lineLay = view?.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return
                val conLay = view?.findViewById<ConstraintLayout>(R.id.lib_rv_base_container) ?:return
                val btnSelect = view?.findViewById<ImageView>(R.id.btn_select) ?:return
                val btnDelete = view?.findViewById<ImageView>(R.id.btn_delete) ?:return
                val btnEditWhole = view?.findViewById<ImageView>(R.id.btn_edit_whole) ?:return
                val position = recyclerView.getChildAdapterPosition(view)
                val item = adapter.currentList[position] as com.example.tangochoupdated.db.dataclass.File
                btnSelect.isSelected = true
                libraryViewModel.setMultipleSelectMode(true)
                libraryViewModel.onClickSelectableItem(item,true)
            }

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if(startPosition==null){
                    startPosition = event
                } else {
                    if((event?.actionMasked== MotionEvent.ACTION_UP)){
                        val view = recyclerView.findChildViewUnder(startPosition!!.x,startPosition!!.y)
                        val lineLay = view?.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return true
                        val conLay = view?.findViewById<ConstraintLayout>(R.id.lib_rv_base_container) ?:return true
                        if(conLay.tag== LibRVState.LeftSwiping){
                            if(lineLay.layoutParams.width <25){
                                Animation().animateLibRVLeftSwipeLay(lineLay,false)
                                conLay.tag = LibRVState.Plane
                            }
                            else if (lineLay.layoutParams.width>=25){
                                Animation().animateLibRVLeftSwipeLay(lineLay ,true)
                                conLay.tag = LibRVState.LeftSwiped
                                libraryViewModel.setLeftSwipedItemExists(true)
                            }

                        }

                    }
                }


                return super.onTouch(v, event)
            }

        }
        )

        val addListeners = LibraryAddListeners(libraryViewModel,deletePopUpViewModel,libNavCon)
        addListeners.fragChildMultiBaseAddCL(binding,requireActivity(),libNavCon)
        addListeners.homeTopBarAddCL(topBarBinding,requireActivity(),libNavCon)
        binding.frameLayTopBar.addView(topBarBinding.root)

        createCardViewModel.setParentFlashCardCover(null)
        createFileViewModel.setParentFile(null)
        createFileViewModel.makeAllBottomMenuClickable()
        libraryViewModel.apply {
            rvCover.observe(viewLifecycleOwner){
                binding.rvCover.visibility = if(it.visible) View.VISIBLE else View.GONE
            }
            libraryViewModel.clearFinalList()
            leftSwipedItemExists.observe(viewLifecycleOwner){
                setRVCover(LibraryViewModel.RvCover(0f,it.not()))
            }
            multipleSelectMode.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.root.visibility = if(it) View.VISIBLE else View.GONE
                topBarBinding.root.visibility = if(!it) View.VISIBLE else View.GONE
                LibraryFragmentBase().changeLibRVSelectBtnVisibility(recyclerView,it)
            }
            makeAllUnSwiped.observe(viewLifecycleOwner){
                if(it) LibraryFragmentBase().makeLibRVUnSwiped(recyclerView)
            }
            changeAllRVSelectedStatus.observe(viewLifecycleOwner){
                LibraryFragmentBase().changeLibRVAllSelectedState(recyclerView,it)
            }
            val emptyView = LibraryFragLayHomeRvEmptyBinding.inflate(inflater,container,false).root
            childFilesFromDB(null).observe(viewLifecycleOwner){
                adapter.submitList(it)
                setParentRVItems(it)
                if(it.isNullOrEmpty()){
                    binding.frameLayRvEmpty.addView(emptyView)
                } else {
                    binding.frameLayRvEmpty.removeView(emptyView)
                }

            }
            childCardsFromDB(null).observe(viewLifecycleOwner){
                createCardViewModel.setSisterCards(it)
                topBarBinding.txvInBoxCardAmount.apply {
                    text = it?.size.toString()
                    visibility = if(it?.size == 0) View.GONE else View.VISIBLE
                }
            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }
            val searchAdapter = LibFragSearchRVListAdapter(
                createFileViewModel,libNavCon,
                libraryViewModel,stringCardViewModel,createCardViewModel,searchViewModel,viewLifecycleOwner,deletePopUpViewModel,
                mainNavCon,
                requireActivity()
            )
            val searchRv = binding.searchRvBinding.recyclerView
            searchRv.adapter = searchAdapter
            searchRv.layoutManager = LinearLayoutManager(requireActivity())
            searchRv.isNestedScrollingEnabled = false
            binding.bindingSearch.edtLibrarySearch.addTextChangedListener {
                if(it.toString()!=""){
                    searchViewModel.setSearchText(it.toString())
                    searchViewModel.getFilesByWords(it.toString()).observe(viewLifecycleOwner){
                        binding.searchRvBinding.root.visibility = View.VISIBLE
                        searchAdapter.submitList(it)

                    }

                } else binding.searchRvBinding.root.visibility = View.GONE
            }

        }

//        LibrarySetUpFragment(libraryViewModel,deletePopUpViewModel).setUpFragLibHome(binding,myNavCon,requireActivity())






        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                if(!libraryViewModel.checkViewReset())
                    libNavCon.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}


