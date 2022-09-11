package com.example.tangochoupdated.ui.listadapter

import android.content.Context
import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.view_set_up.*
import com.example.tangochoupdated.ui.viewmodel.*


/**
 * Custom Data Class for this adapter
 */


class LibFragSearchRVListAdapter(
    private val createFileViewModel: CreateFileViewModel,
    private val navController: NavController,
    private val libraryViewModel: LibraryViewModel,
    private val stringCardViewModel: StringCardViewModel,
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
            searchViewModel,lifecycleOwner,deletePopUpViewModel,navController,mainNavController)
    }

    class LibFragSearchRVViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){
        lateinit var fileBinding:LibraryFragRvItemFileBinding
        fun bind(item: Any,
                 libraryViewModel: LibraryViewModel,
                 stringCardViewModel: StringCardViewModel,
                 createCardViewModel: CreateCardViewModel,
                 searchViewModel: SearchViewModel,
                 lifecycleOwner: LifecycleOwner,
                 deletePopUpViewModel: DeletePopUpViewModel,
                 mainNavController: NavController,

                 navController: NavController){
            binding.contentBindingFrame.removeAllViews()
//            親レイアウトのclick listener


            LibrarySetUpItems(libraryViewModel,deletePopUpViewModel, navController ).setUpRVSearchBase(
                rvItemBaseBinding = binding,
                stringCardViewModel = stringCardViewModel,
                createCardViewModel = createCardViewModel,
                context = context,
                item = item,
                searchViewModel = searchViewModel,
                lifecycleOwner = lifecycleOwner,
                mainNavController = mainNavController
            )



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