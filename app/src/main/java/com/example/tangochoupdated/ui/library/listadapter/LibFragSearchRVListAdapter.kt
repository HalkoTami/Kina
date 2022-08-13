package com.example.tangochoupdated.ui.library.listadapter

import android.content.Context
import android.view.*
import android.view.View.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.GetCustomDrawables
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.*


/**
 * Custom Data Class for this adapter
 */


class LibFragSearchRVListAdapter(
    private val createFileViewModel: CreateFileViewModel,
    private val libraryViewModel: LibraryViewModel,
    private val stringCardViewModel: StringCardViewModel,
    private val createCardViewModel: CreateCardViewModel,
    private val searchViewModel: SearchViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
) :
    ListAdapter<Any, LibFragSearchRVListAdapter.LibFragSearchRVViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragSearchRVViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragSearchRVViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibFragSearchRVViewHolder, position: Int) {
        holder.bind(getItem(position),libraryViewModel,stringCardViewModel,createCardViewModel,searchViewModel,lifecycleOwner)
    }

    class LibFragSearchRVViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){
        lateinit var fileBinding:LibraryFragRvItemFileBinding
        fun bind(item: Any,
                 libraryViewModel: LibraryViewModel ,
                 stringCardViewModel: StringCardViewModel,
                 createCardViewModel: CreateCardViewModel,
                 searchViewModel: SearchViewModel,
                 lifecycleOwner: LifecycleOwner){
            binding.contentBindingFrame.removeAllViews()
//            親レイアウトのclick listener

            fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
            binding.contentBindingFrame.addView(fileBinding.root)
            LibrarySetUpItems(libraryViewModel).setUpRVSearchBase(
                rvItemBaseBinding = binding,
                stringCardViewModel = stringCardViewModel,
                createCardViewModel = createCardViewModel,
                context = context,
                item = item,
                searchViewModel = searchViewModel,
                lifecycleOwner = lifecycleOwner
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