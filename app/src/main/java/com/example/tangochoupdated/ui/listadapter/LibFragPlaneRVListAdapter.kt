package com.example.tangochoupdated.ui.listadapter

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.StringCardViewModel
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpItems
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel


/**
 * Custom Data Class for this adapter
 */


class LibFragPlaneRVListAdapter(
    private val createFileViewModel: CreateFileViewModel,
    private val libraryViewModel: LibraryViewModel,
    private val context: Context,
    private val stringCardViewModel: StringCardViewModel,
    private val createCardViewModel: CreateCardViewModel
) :
    ListAdapter<Any, LibFragPlaneRVListAdapter.LibFragFileViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragFileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragFileViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibFragFileViewHolder, position: Int) {
        holder.bind(getItem(position),libraryViewModel,createFileViewModel,createCardViewModel,stringCardViewModel)
    }

    class LibFragFileViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(item: Any,
                 libraryViewModel: LibraryViewModel,
                 createFileViewModel: CreateFileViewModel,
                 createCardViewModel: CreateCardViewModel,
                 stringCardViewModel: StringCardViewModel
        ){
            binding.contentBindingFrame.removeAllViews()
//            親レイアウトのclick listener

            LibrarySetUpItems(libraryViewModel).setUpRVBasePlane(
                rvItemBaseBinding = binding,
                item = item,
                context = context,
                createCardViewModel = createCardViewModel,
                createFileViewModel = createFileViewModel,
                stringCardViewModel = stringCardViewModel

            )

        }
    }

}