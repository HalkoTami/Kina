package com.korokoro.kina.ui.listadapter

import android.content.Context
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.actions.makeToast
import com.korokoro.kina.databinding.*
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.listener.recyclerview.LibraryRVCL
import com.korokoro.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.korokoro.kina.ui.view_set_up.LibraryAddListeners
import com.korokoro.kina.ui.view_set_up.LibrarySetUpItems
import com.korokoro.kina.ui.viewmodel.*
import com.korokoro.kina.ui.customClasses.LibRVState


/**
 * Custom Data Class for this adapter
 */


class LibFragPlaneRVListAdapter(
    private val context: Context,
    private val stringCardViewModel: CardTypeStringViewModel,
    private val createCardViewModel: CreateCardViewModel,
    private val createFileViewModel: EditFileViewModel,
    private val deletePopUpViewModel: DeletePopUpViewModel,
    private val mainNavController: NavController,
    private val libraryViewModel: LibraryBaseViewModel,
) :
    ListAdapter<Any, LibFragPlaneRVListAdapter.LibFragFileViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragFileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragFileViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibFragFileViewHolder, position: Int) {
        holder.bind(getItem(position),libraryViewModel,createCardViewModel,
            stringCardViewModel, mainNavController,createFileViewModel,deletePopUpViewModel)
    }

    class LibFragFileViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(item: Any,
                 libraryViewModel: LibraryBaseViewModel,
                 createCardViewModel: CreateCardViewModel,
                 stringCardViewModel: CardTypeStringViewModel,
                 mainNavController: NavController,
                 createFileViewModel: EditFileViewModel,
                 deletePopUpViewModel: DeletePopUpViewModel,
        ){
            fun libRVButtonsAddCL(
            ){ binding.apply {
                arrayOf(
                    libRvBaseContainer,
                    btnDelete,
                    btnSelect,
                    btnAddNewCard,
                    btnEditWhole
                ).onEach { it.setOnClickListener(
                    LibraryRVCL(item,libraryViewModel, createFileViewModel,binding,deletePopUpViewModel,createCardViewModel)
                )
                }
            }
            }
            binding.contentBindingFrame.removeAllViews()
//            親レイアウトのclick listener

            val viewSetUp = LibrarySetUpItems()
            val addL = LibraryAddListeners()

            val contentView:View
            when(item){
                is File -> {
                    val fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
                    viewSetUp.setUpRVFileBinding(fileBinding, item, context)
                    binding.btnAddNewCard.visibility = View.GONE
                    contentView = fileBinding.root

                }
                is Card -> {
                    val stringCardBinding = LibraryFragRvItemCardStringBinding.inflate(LayoutInflater.from(context))
                    viewSetUp.setUpRVStringCardBinding(stringCardBinding, item.stringData, )
                    AnkiBoxFragViewSetUp().setUpOrder(stringCardBinding,item)
                    addL.cardRVStringAddCL(stringCardBinding,item,createCardViewModel,stringCardViewModel,mainNavController)
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
                if(libraryViewModel.returnMultiSelectMode()== true) btnSelect.visibility = View.VISIBLE
            }
            libRVButtonsAddCL()
            binding.contentBindingFrame.addView(contentView)
//            TODo


        }
    }

}