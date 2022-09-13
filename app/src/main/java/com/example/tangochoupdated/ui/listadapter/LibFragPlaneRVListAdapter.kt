package com.example.tangochoupdated.ui.listadapter

import android.content.Context
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.listener.recyclerview.LibraryRVCL
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpItems
import com.example.tangochoupdated.ui.viewmodel.*
import com.example.tangochoupdated.ui.viewmodel.customClasses.LibRVState


/**
 * Custom Data Class for this adapter
 */


class LibFragPlaneRVListAdapter(
    private val context: Context,
    private val stringCardViewModel: StringCardViewModel,
    private val createCardViewModel: CreateCardViewModel,
    private val createFileViewModel: CreateFileViewModel,
    private val deletePopUpViewModel: DeletePopUpViewModel,
    private val mainNavController: NavController,
    private val libraryViewModel: LibraryViewModel,
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
                 libraryViewModel: LibraryViewModel,
                 createCardViewModel: CreateCardViewModel,
                 stringCardViewModel: StringCardViewModel,
                 mainNavController: NavController,
                 createFileViewModel: CreateFileViewModel,
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

            if(item is Card){
                createCardViewModel.updateCardPosition(item,adapterPosition)
            }
            val viewSetUp = LibrarySetUpItems()
            val addL = LibraryAddListeners()

            val contentView= when(item){
                is File -> {
                    val fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
                    viewSetUp.setUpRVFileBinding(fileBinding, item, context)
                    binding.btnAddNewCard.visibility = View.GONE
                    fileBinding.root
                }
                is Card -> {
                    val stringCardBinding = LibraryFragRvItemCardStringBinding.inflate(LayoutInflater.from(context))
                    viewSetUp.setUpRVStringCardBinding(stringCardBinding, item.stringData, )
                    addL.cardRVStringAddCL(stringCardBinding,item,createCardViewModel,stringCardViewModel,mainNavController)
                    binding.btnAddNewCard.visibility = View.VISIBLE
                    stringCardBinding.root
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


        }
    }

}