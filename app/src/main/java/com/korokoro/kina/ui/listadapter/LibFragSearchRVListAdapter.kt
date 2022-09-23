package com.korokoro.kina.ui.listadapter

import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.databinding.*
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.listener.recyclerview.LibraryRVSearchCL
import com.korokoro.kina.ui.view_set_up.*
import com.korokoro.kina.ui.viewmodel.*


/**
 * Custom Data Class for this adapter
 */


class LibFragSearchRVListAdapter(
    private val createFileViewModel: EditFileViewModel,
    private val navController: NavController,
    private val libraryViewModel: LibraryBaseViewModel,
    private val stringCardViewModel: CardTypeStringViewModel,
    private val createCardViewModel: CreateCardViewModel,
    private val searchViewModel: SearchViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val deletePopUpViewModel: DeletePopUpViewModel,
    private val mainNavController: NavController,
    private val context: Context,
) :
    ListAdapter<Any, LibFragSearchRVListAdapter.LibFragSearchRVViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragSearchRVViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragSearchRVViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibFragSearchRVViewHolder, position: Int) {
        holder.bind(getItem(position),libraryViewModel,stringCardViewModel,createCardViewModel,
            searchViewModel,lifecycleOwner,mainNavController)
    }

    class LibFragSearchRVViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){
        lateinit var fileBinding:LibraryFragRvItemFileBinding
        fun bind(item: Any,
                 libraryViewModel: LibraryBaseViewModel,
                 stringCardViewModel: CardTypeStringViewModel,
                 createCardViewModel: CreateCardViewModel,
                 searchViewModel: SearchViewModel,
                 lifecycleOwner: LifecycleOwner,
                 mainNavController: NavController){
            binding.contentBindingFrame.removeAllViews()
//            親レイアウトのclick listener
            val viewSetUp = LibrarySetUpItems()
            val addL = LibraryAddListeners()
            val checkMatchTxv = mutableListOf<TextView>()
            val contentView= when(item){
                is File -> {
                    val fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
                    checkMatchTxv.addAll(viewSetUp.returnFileBindingTextViews(fileBinding))
                    viewSetUp.setUpRVFileBinding(fileBinding, item, context)
                    binding.btnAddNewCard.visibility = View.GONE
                    fileBinding.root
                }
                is Card -> {
                    val stringCardBinding = LibraryFragRvItemCardStringBinding.inflate(LayoutInflater.from(context))
                    checkMatchTxv.addAll(viewSetUp.returnStringCardTextViews(stringCardBinding))
                    viewSetUp.setUpRVStringCardBinding(stringCardBinding, item.stringData, )
                    addL.cardRVStringAddCL(stringCardBinding,item,createCardViewModel,stringCardViewModel,mainNavController)
                    binding.btnAddNewCard.visibility = View.VISIBLE
                    stringCardBinding.root
                }
                else -> return
            }
            fun addL(){
                arrayOf(
                    binding.libRvBaseContainer,

                    ).onEach { it.setOnClickListener(
                    LibraryRVSearchCL(item =  item,
                        createCardViewModel = createCardViewModel,
                        lVM = libraryViewModel,
                        rvBinding = binding,
                        navController = libraryViewModel.returnLibraryNavCon() ?:return, mainNavCon = mainNavController)
                )
                }
            }
            addL()
            binding.contentBindingFrame.addView(contentView)
            searchViewModel.searchingText.observe(lifecycleOwner){ search ->
                checkMatchTxv.onEach { txv->
                    viewSetUp.setColorByMatchedSearch(txv,txv.text.toString(),search?:"", ContextCompat.getColor(context, R.color.red))
                }
            }

        }

        }
    }
object SearchDiffCallback : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return if(oldItem is File && newItem is File){
            oldItem.fileId == newItem.fileId
        } else if(oldItem is Card && newItem is Card){
            oldItem.id == newItem.id
        } else false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return if(oldItem is File && newItem is File){
            oldItem == newItem
        } else if(oldItem is Card && newItem is Card){
            oldItem == newItem
        } else false
    }


}