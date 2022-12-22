package com.koronnu.kina.ui.listadapter

import android.content.Context
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.customClasses.enumClasses.LibRVState
import com.koronnu.kina.databinding.*
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.tabLibrary.LibraryBaseViewModel
import com.koronnu.kina.ui.listener.recyclerview.LibraryRVCL
import com.koronnu.kina.ui.view_set_up.LibraryAddListeners
import com.koronnu.kina.ui.view_set_up.LibrarySetUpItems
import com.koronnu.kina.ui.viewmodel.*


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
) :
    ListAdapter<Any, LibFragPlaneRVListAdapter.LibFragFileViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragFileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragFileViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),parent.context)
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
                    LibraryRVCL(item,libraryViewModel, createFileViewModel,binding,deletePopUpViewModel,createCardViewModel,it)
                )
                }
            }
            }
//            fun addCardCL(item: Card){
//                binding.apply {
//                    arrayOf(
//                        root,
//                        contentBindingFrame,
//                        btnDelete,
//                        btnSelect,
//                        btnAddNewCard,
//                        btnEditWhole
//                    ).onEach {
//                        it.setOnTouchListener(
//                            LibraryRVCLNewCard(item,libraryViewModel, createFileViewModel,binding,deletePopUpViewModel,createCardViewModel,it)
//                        )
//                    }
//                }
//            }
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

                    libRVButtonsAddCL(item)
                }
                is Card -> {
                    val stringCardBinding = ListItemLibraryRvCardStringBinding.inflate(LayoutInflater.from(context))
                    viewSetUp.setUpRVStringCardBinding(stringCardBinding, item.stringData, )

//                    addCardCL(item)
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
                if(libraryViewModel.returnMultiSelectMode()== true) btnSelect.visibility = View.VISIBLE
            }

            binding.contentBindingFrame.addView(contentView)
//            TODo


        }

    }

}