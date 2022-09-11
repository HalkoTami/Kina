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
import com.example.tangochoupdated.db.enumclass.LibraryFragment
import com.example.tangochoupdated.db.enumclass.Toast
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBase
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.listadapter.LibFragSearchRVListAdapter
import com.example.tangochoupdated.ui.listener.recyclerview.LibraryRVTouchListener
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

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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
        recyclerView.isNestedScrollingEnabled = true
        libNavCon =  requireActivity().findNavController(R.id.lib_frag_con_view)
        val topBarBinding = LibraryFragTopBarHomeBinding.inflate(inflater,container,false)

        val addListeners = LibraryAddListeners(libraryViewModel,deletePopUpViewModel,libNavCon)
        addListeners.fragChildMultiBaseAddCL(binding,requireActivity(),libNavCon)
        addListeners.homeTopBarAddCL(topBarBinding,requireActivity(),libNavCon)
        binding.frameLayTopBar.addView(topBarBinding.root)
        createCardViewModel.setParentFlashCardCover(null)
        createFileViewModel.setParentFile(null)
        createFileViewModel.makeAllBottomMenuClickable()
        libraryViewModel.apply {
            setLibraryFragment(LibraryFragment.Home)
            rvCover.observe(viewLifecycleOwner){
                binding.rvCover.visibility = if(it.visible) View.VISIBLE else View.GONE
            }
            libraryViewModel.clearFinalList()
            leftSwipedItemExists.observe(viewLifecycleOwner){
                setRVCover(LibraryViewModel.RvCover(it.not()))
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
                topBarBinding.txvInBoxCardAmount.apply {
                    text = it?.size.toString()
                    visibility = if(it?.size == 0) View.GONE else View.VISIBLE
                }
                createCardViewModel.setSisterCards(it?:return@observe)
            }
            selectedItems.observe(viewLifecycleOwner){
                binding.topBarMultiselectBinding.txvSelectingStatus.text = "${it.size}個　選択中"
            }
            fun pxToDp(px: Int, context: Context): Float {
                val metrics = context.resources.displayMetrics
                return px / metrics.density
            }

            binding.mainFrameLayout.viewTreeObserver.addOnGlobalLayoutListener {
                binding.rvCover.layoutParams.height = binding.mainFrameLayout.height+10
                topBarBinding.txvHome.text = binding.mainFrameLayout.height.toString()
            }



            binding.rvCover.
            setOnTouchListener(LibraryRVTouchListener(requireActivity(),recyclerView,libraryViewModel,binding.frameLayTest))
            val searchAdapter = LibFragSearchRVListAdapter(
                createFileViewModel,libNavCon,
                libraryViewModel,stringCardViewModel,createCardViewModel,searchViewModel,viewLifecycleOwner,deletePopUpViewModel,
                mainNavCon,
                requireActivity()
            )
//            val searchRv = binding.searchRvBinding.recyclerView
//            searchRv.adapter = searchAdapter
//            searchRv.layoutManager = LinearLayoutManager(requireActivity())
//            searchRv.isNestedScrollingEnabled = false
//            binding.bindingSearch.edtLibrarySearch.addTextChangedListener {
//                if(it.toString()!=""){
//                    binding.mainFrameLayout.visibility = View.GONE
//                    binding.frameLaySearchRv.visibility = View.VISIBLE
//                    searchViewModel.setSearchText(it.toString())
//                    searchViewModel.getFilesByWords(it.toString()).observe(viewLifecycleOwner){
//                        searchViewModel.setMatchedFiles(it)
//                        val a = mutableListOf<Any>()
//                        a.addAll(searchViewModel.returnMatchedCards())
//                        a.addAll(it)
//                        searchViewModel.setMatchedItems(a)
//
//                    }
//                    searchViewModel.getCardsByWords(it.toString()).observe(viewLifecycleOwner){
//                        searchViewModel.setMatchedCards(it)
//                        val a = mutableListOf<Any>()
//                        a.addAll(searchViewModel.returnMatchedFiles())
//                        a.addAll(it)
//                        searchViewModel.setMatchedItems(a)
//
//                    }
//                    searchViewModel.matchedItems.observe(viewLifecycleOwner){
//                        makeToast(requireActivity(),it.size.toString())
//                        searchAdapter.submitList(it)
//                    }
//
//                } else {
//                    binding.mainFrameLayout.visibility= View.VISIBLE
//                    binding.frameLaySearchRv.visibility = View.GONE
//                }
//            }

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


