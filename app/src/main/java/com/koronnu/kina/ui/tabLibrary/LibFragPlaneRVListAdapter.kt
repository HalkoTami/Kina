package com.koronnu.kina.ui.tabLibrary

import android.animation.ValueAnimator
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.data.model.enumClasses.LibRVState
import com.koronnu.kina.databinding.*
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.ui.EditFileViewModel
import com.koronnu.kina.ui.editCard.CreateCardViewModel
import com.koronnu.kina.ui.editCard.editCardContent.stringCard.CardTypeStringViewModel
import com.koronnu.kina.util.LeftScrollReceiver
import com.koronnu.kina.util.view_set_up.LibrarySetUpItems
import kotlin.math.absoluteValue


/**
 * Custom Data Class for this adapter
 */


class LibFragPlaneRVListAdapter(
    private val stringCardViewModel: CardTypeStringViewModel,
    private val createCardViewModel: CreateCardViewModel,
    private val createFileViewModel: EditFileViewModel,
    private val deletePopUpViewModel: DeletePopUpViewModel,
    private val mainNavController: NavController,
    private val libraryViewModel: LibraryBaseViewModel,
    private val parentLifecycleOwner: LifecycleOwner
) :
    ListAdapter<Any, LibFragPlaneRVListAdapter.LibFragFileViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragFileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragFileViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),libraryViewModel)
    }

    override fun onBindViewHolder(holder: LibFragFileViewHolder, position: Int) {
        holder.bind(getItem(position),createCardViewModel,
            stringCardViewModel, mainNavController,createFileViewModel,deletePopUpViewModel,parentLifecycleOwner)
    }

    class LibFragFileViewHolder (private val binding: LibraryFragRvItemBaseBinding,val libraryViewModel: LibraryBaseViewModel) :
        RecyclerView.ViewHolder(binding.root), LeftScrollReceiver {

        override val upDatingView: LinearLayoutCompat
            get() = binding.linLaySwipeShow
        override val libraryBaseViewModel: LibraryBaseViewModel
            get() = libraryViewModel
        fun bind(item: Any,
                 createCardViewModel: CreateCardViewModel,
                 stringCardViewModel: CardTypeStringViewModel,
                 mainNavController: NavController,
                 createFileViewModel: EditFileViewModel,
                 deletePopUpViewModel: DeletePopUpViewModel,
                 parentLifecycleOwner: LifecycleOwner
        ){
            fun libRVButtonsAddCL(item:Any
            ){ binding.apply {
                arrayOf(
                    root,
                    contentBindingFrame,
                    btnDelete,
                    btnSelect,
                    btnAddNewCard,
                    btnEditWhole
                ).onEach {
                    it.setOnTouchListener(
                    LibraryRVCL(item,libraryBaseViewModel, createFileViewModel,binding,deletePopUpViewModel,createCardViewModel,it)
                )
                }
            }
            }
            binding.apply {
                libraryViewModel = libraryBaseViewModel
                lifecycleOwner = parentLifecycleOwner
            }
            libraryBaseViewModel.makeAllUnSwiped.observe(parentLifecycleOwner){
                if(it) animateDisAppear()
            }
            binding.contentBindingFrame.removeAllViews()

            val context = binding.root.context
            val viewSetUp = LibrarySetUpItems()
            val contentView:View
            when(item){
                is File -> {
                    val fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
                    fileBinding.file = item
                    binding.btnAddNewCard.visibility = View.GONE
                    contentView = fileBinding.root

                    libRVButtonsAddCL(item)
                }
                is Card -> {
                    val stringCardBinding = ListItemLibraryRvCardStringBinding.inflate(LayoutInflater.from(context))
                    viewSetUp.setUpRVStringCardBinding(stringCardBinding, item.stringData, )
                    libRVButtonsAddCL(item)
                    binding.btnAddNewCard.visibility = View.VISIBLE
                    contentView = stringCardBinding.root
                }
                else -> return
            }
            binding.apply {
                btnSelect.apply {
                    setImageDrawable(
                        AppCompatResources.getDrawable(context, R.drawable.select_rv_item))
                }
                root.tag = LibRVState.Plane
                linLaySwipeShow.visibility = View.GONE
//                btnSelect.visibility = if(libraryViewModel.returnMultiSelectMode())  View.VISIBLE else View.GONE
            }
            binding.contentBindingFrame.addView(contentView)


        }

    }
}

