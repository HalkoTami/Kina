package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.ItemColorPaletBinding
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FragmentTree
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.enumclass.StartFragment
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.listener.popUp.EditFilePopUpCL
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
            val activeFragment = returnActiveFragment() ?: FragmentTree()
             activeFragment.startFragment = StartFragment.Library
            setActiveFragment(activeFragment)
        }

        binding.editFileBinding.apply {
            createFileViewModel.apply {
                var previousColor: ColorStatus? =null
                filePopUpUIData.observe(viewLifecycleOwner){
                    binding.frameLayEditFile.visibility = it.visibility
                    if(txvFileTitle.text != it.txvLeftTopText) txvFileTitle.text = it.txvLeftTopText
                    if(txvHint.text!=it.txvHintText) txvHint.text=it.txvHintText

                    if(previousColor!=it.colorStatus){
                        changeColPalletCol(previousColor,false,colPaletBinding)
                        changeColPalletCol(it.colorStatus,true,colPaletBinding)
                        previousColor = it.colorStatus
                    }
                    if(imvFileType.tag!= it.drawableId){
                        imvFileType.setImageDrawable(AppCompatResources.getDrawable(requireActivity(),it.drawableId))
                        imvFileType.tag = it.drawableId
                    }
                    edtCreatefile.apply {
                        if(text.toString()!=it.edtTitleText) text= SpannableStringBuilder(it.edtTitleText)
                        if(hint!= it.edtTitleHint) hint = it.edtTitleHint
                    }
            }


            }
        }
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
                confirmDeleteView.observe(viewLifecycleOwner){
                    binding.confirmDeletePopUpBinding.apply {
                        binding.frameLayConfirmDelete.visibility = if(it.visible) View.VISIBLE else View.GONE
                        txvConfirmDeleteOnlyParent.text = it.confirmText
                    }
                }
                confirmDeleteWithChildrenView.observe(viewLifecycleOwner){
                    binding.confirmDeleteChildrenPopUpBinding.apply {
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
            binding.editFileBinding.apply {
                colPaletBinding.apply {
                    arrayOf(
                        imvColBlue,imvColGray,imvColRed,imvColYellow,imvIconPalet,btnClose,btnFinish,root
                    ).onEach {
                        it.setOnClickListener(EditFilePopUpCL(binding.editFileBinding,binding.frameLayEditFile,binding.background,createFileViewModel)) }
                }
            }

//            observe(viewLifecycleOwner){
//                val visibility = it.visible
//                if(!visibility){
//                    binding.confirmDeletePopUpBinding.root.visibility = View.GONE
//                    binding.confirmDeleteChildrenPopUpBinding.root.visibility = View.GONE
//                } else {
//                    when(it.confirmMode){
//                        ConfirmMode.DeleteItem-> binding.confirmDeletePopUpBinding.root.visibility = View.VISIBLE
//                        ConfirmMode.DeleteWithChildren -> binding.confirmDeleteChildrenPopUpBinding.root.visibility = View.VISIBLE
//                    }
//                }
//            }

//            deletingItemChildrenFiles.observe(viewLifecycleOwner){ list ->
//                val folderAmount = list?.filter { it.fileStatus == FileStatus.FOLDER }?.size ?:0
//                val flashcardCoverAmount = list?.filter { it.fileStatus == FileStatus.TANGO_CHO_COVER }?.size ?:0
//                binding.confirmDeleteChildrenPopUpBinding.apply {
//                    txvContainingFolder.text = "${folderAmount}個"
//                    txvContainingFlashcard.text = "${flashcardCoverAmount}個"
//                }
//            }
//            deletingItemChildrenCards.observe(viewLifecycleOwner){ list->
//                val cardAmount = list?.size
//                binding.confirmDeleteChildrenPopUpBinding.txvContainingCard.text = "${cardAmount}枚"
//            }
        }



//        libraryViewModel.action.observe(viewLifecycleOwner){
//            if(libraryViewModel.returnParentFile()?.fileStatus!=FileStatus.TANGO_CHO_COVER){
//                myNavCon.navigate(it)
//            }
//            Toast.makeText(requireActivity(), "action called ", Toast.LENGTH_SHORT).show()
//        }
        val addListeners = LibraryAddListeners(libraryViewModel,deletePopUpViewModel,libNavCon)
        addListeners.confirmDeletePopUpAddCL(binding.confirmDeletePopUpBinding,binding.confirmDeleteChildrenPopUpBinding)

//        LibrarySetUpFragment(libraryViewModel,deletePopUpViewModel).setUpFragLibBase(binding)
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
                libraryViewModel.onClickBack()
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
                imageView.background = a
                imageView.elevation = 10f
            }
            false -> {
                a.setStroke(5, ContextCompat.getColor(requireActivity(), R.color.ofwhite))
                a.setColor(col)
                imageView.alpha = 0.4f
                imageView.elevation = 0f
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
            val parent = view.findViewById<ConstraintLayout>(R.id.base_container)
            if(parent.tag == LibRVState.LeftSwiped){
                Animation().animateLibRVLeftSwipeLay(
                    view.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show),false)
            }
            view.findViewById<ImageView>(R.id.btn_select).visibility = View.GONE
            parent.tag = LibRVState.Plane
        }
    }

}


