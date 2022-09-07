package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.ItemColorPaletBinding
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.enumclass.MainFragment
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.listener.popUp.EditFilePopUpCL
import com.example.tangochoupdated.ui.observer.CommonOb
import com.example.tangochoupdated.ui.observer.LibraryOb
import com.example.tangochoupdated.ui.view_set_up.ColorPalletViewSetUp
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.viewmodel.*


class LibraryFragmentBase : Fragment(){

    private lateinit var libNavCon:NavController
    private lateinit var recyclerView:RecyclerView
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val createCardViewModel: CreateCardViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()
    private val deletePopUpViewModel: DeletePopUpViewModel by activityViewModels()
    private val chooseFileMoveToViewModel:ChooseFileMoveToViewModel by activityViewModels()
    private var _binding: LibraryFragBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LibraryFragBinding.inflate(inflater, container, false)

        val a = childFragmentManager.findFragmentById(binding.libFragConView.id) as NavHostFragment
        libNavCon = a.navController

        baseViewModel.apply {
            setActiveFragment(MainFragment.Library)
        }


        chooseFileMoveToViewModel.apply {
            showToast.observe(viewLifecycleOwner){
                CommonOb().observeToast(requireActivity(),returnToastText(),it)
            }
        }

//        binding.editFileBinding.apply {
//            createFileViewModel.apply {
//                editFilePopUpVisible.observe(viewLifecycleOwner){
//                    binding.frameLayEditFile.visibility = if(it)View.VISIBLE else View.GONE
//                    binding.background.visibility = if(it)View.VISIBLE else View.GONE
//                }
//                filePopUpUIData.observe(viewLifecycleOwner){
//                    LibraryOb().observeEditFilePopUp(binding.editFileBinding,it,requireActivity())
//                }
//
//
//            }
//        }
//                UIへデータ反映
//        bindingCreateFile.apply {
//            var start = true
//            lastInsetedFileId.observe(this@MainActivity){
//                if(start){
//                    start = false
//                    return@observe
//                } else createFileViewModel.setLastInsertedFileId(it)
//            }
//        }

        libraryViewModel.apply {

            deletePopUpViewModel.apply {
                showToast.observe(viewLifecycleOwner){
                    CommonOb().observeToast(requireActivity(),returnToastText(),it)
                }
                confirmDeleteView.observe(viewLifecycleOwner){
                    binding.confirmDeletePopUpBinding.apply {
                        binding.background.visibility = if(it.visible) View.VISIBLE else View.GONE
                        binding.frameLayConfirmDelete.visibility = if(it.visible) View.VISIBLE else View.GONE
                        txvConfirmDeleteOnlyParent.text = it.confirmText
                    }
                }
                confirmDeleteWithChildrenView.observe(viewLifecycleOwner){
                    binding.confirmDeleteChildrenPopUpBinding.apply {
                        binding.background.visibility = if(it.visible) View.VISIBLE else View.GONE
                        binding.frameLayConfirmDeleteWithChildren.visibility =  if(it.visible) View.VISIBLE else View.GONE
                        txvContainingFolder.text = "${it.containingFolder}個"
                        txvContainingFlashcard.text = "${it.containingFlashCardCover}個"
                        txvContainingCard.text = "${it.containingCards}枚"
                    }

                }
                deletingItem.observe(viewLifecycleOwner){ list ->
                    if (list.isEmpty().not()) {
                        setDeleteText(list)
                        setDeleteWithChildrenText(list)
                        val fileIds = mutableListOf<Int>()
                        list.onEach { when(it ){
                            is File -> fileIds.add(it.fileId)
                        } }
                        getAllDescendantsByFileId(fileIds).observe(viewLifecycleOwner) {
                            setDeletingItemChildrenFiles(it)
                            setContainingFilesAmount(it)
                        }
                        getCardsByMultipleFileId(fileIds).observe(viewLifecycleOwner){
                            setDeletingItemChildrenCards(it)
                            setContainingCardsAmount(it)
                        }
                    }


                }
            }
//            binding.editFileBinding.apply {
//                colPaletBinding.apply {
//                    arrayOf(
//                        imvColBlue,imvColGray,imvColRed,imvColYellow,imvIconPalet,btnClose,btnFinish,root,binding.background
//                    ).onEach {
//                        it.setOnClickListener(EditFilePopUpCL(binding.editFileBinding,binding.frameLayEditFile,binding.background,createFileViewModel)) }
//                }
//            }

        }


        val addListeners = LibraryAddListeners(libraryViewModel,deletePopUpViewModel,libNavCon)
        addListeners.confirmDeletePopUpAddCL(binding.confirmDeletePopUpBinding,binding.confirmDeleteChildrenPopUpBinding)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
    fun changeColPalletCol(colorStatus: ColorStatus?, selected:Boolean?, colorPaletBinding: ItemColorPaletBinding){
        val imageView:ImageView
        val col:Int
        colorPaletBinding.apply {
            when(colorStatus) {
                ColorStatus.GRAY -> {
                    col = ContextCompat.getColor(requireActivity(), R.color.gray)
                    imageView = this.imvColGray
                }
                ColorStatus.BLUE -> {
                    col = ContextCompat.getColor(requireActivity(), R.color.blue)
                    imageView = this.imvColBlue
                }
                ColorStatus.YELLOW -> {
                    col = ContextCompat.getColor(requireActivity(), R.color.yellow)
                    imageView = this.imvColYellow
                }
                ColorStatus.RED -> {
                    col = ContextCompat.getColor(requireActivity(), R.color.red)
                    imageView = this.imvColRed
                }
                else -> return
            }
        }
        val a = imageView.drawable as GradientDrawable
        a.mutate()

        when(selected){
            true -> {
                a.setStroke(5, ContextCompat.getColor(requireActivity(), R.color.black))
                a.setColor(col)
                imageView.alpha = 1f
//                imageView.background = a
                imageView.elevation = 10f
            }
            false -> {
                a.setStroke(5, ContextCompat.getColor(requireActivity(), R.color.ofwhite))
                a.setColor(col)
                imageView.alpha = 0.4f
//                imageView.elevation = 0f
            }
            else -> return
        }
        imageView.setImageDrawable(a)

    }

    fun makeAllColPaletUnselected(colorPaletBinding: ItemColorPaletBinding){
        changeColPalletCol(ColorStatus.RED,false,colorPaletBinding)
        changeColPalletCol(ColorStatus.YELLOW,false,colorPaletBinding)
        changeColPalletCol(ColorStatus.BLUE,false,colorPaletBinding)
        changeColPalletCol(ColorStatus.GRAY,false,colorPaletBinding)
    }
    fun changeLibRVSelectBtnVisibility(rv:RecyclerView,visible: Boolean){
        rv.children.iterator().forEach { view ->
            view.findViewById<ImageView>(R.id.btn_select).apply {
                visibility =if(visible) View.VISIBLE else View.GONE
            }
        }
    }
    fun changeLibRVAllSelectedState(rv:RecyclerView,selected:Boolean){
        rv.children.iterator().forEach { view ->
            view.findViewById<ImageView>(R.id.btn_select).apply {
                isSelected = selected
            }
        }
    }
    fun changeStringBtnVisibility(rv:RecyclerView,visible:Boolean){
        rv.children.iterator().forEach { view ->
            arrayOf(
                view.findViewById<ImageView>(R.id.btn_edt_front),
                view.findViewById<ImageView>(R.id.btn_edt_back),
                view.findViewById(R.id.btn_add_new_card))
                .onEach {
                    it.visibility = if(visible)View.VISIBLE else View.GONE
                }
        }
    }

    fun makeLibRVUnSwiped(rv:RecyclerView){
        rv.children.iterator().forEach { view ->
            val parent = view.findViewById<ConstraintLayout>(R.id.lib_rv_base_container)
            if(parent.tag == LibRVState.LeftSwiped){
                Animation().animateLibRVLeftSwipeLay(
                    view.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show),false)
            }
            view.findViewById<ImageView>(R.id.btn_select).visibility = View.GONE
            parent.tag = LibRVState.Plane
        }
    }

}


