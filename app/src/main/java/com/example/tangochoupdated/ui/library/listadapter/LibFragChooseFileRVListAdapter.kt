package com.example.tangochoupdated.ui.library.listadapter

import android.content.Context
import android.view.*
import android.view.View.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.GetCustomDrawables
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryAddListeners
import com.example.tangochoupdated.ui.library.LibrarySetUpItems
import com.example.tangochoupdated.ui.library.LibraryViewModel


/**
 * Custom Data Class for this adapter
 */


class LibFragChooseFileRVListAdapter(
    private val createFileViewModel: CreateFileViewModel,
    private val libraryViewModel: LibraryViewModel,
    private val context: Context, ) :
    ListAdapter<File, LibFragChooseFileRVListAdapter.LibFragChooseFileViewHolder>(ChooseFileDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragChooseFileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragChooseFileViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibFragChooseFileViewHolder, position: Int) {
        holder.bind(getItem(position),libraryViewModel,createFileViewModel)
    }

    class LibFragChooseFileViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: File,
                 libraryViewModel: LibraryViewModel,
        createFileViewModel: CreateFileViewModel){
            binding.contentBindingFrame.removeAllViews()
//            親レイアウトのclick listener
           LibrarySetUpItems(libraryViewModel).setUpRVBaseSelectFileMoveTo(
               rvItemBaseBinding = binding,
               item = item,
               context = context,
               createFileViewModel = createFileViewModel
           )

        }

        }
    }
private object ChooseFileDiffCallback : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.fileId == newItem.fileId
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem ==newItem
    }

}