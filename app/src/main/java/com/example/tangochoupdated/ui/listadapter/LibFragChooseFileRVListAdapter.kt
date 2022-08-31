package com.example.tangochoupdated.ui.listadapter

import android.content.Context
import android.view.*
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpItems
import com.example.tangochoupdated.ui.viewmodel.DeletePopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel


/**
 * Custom Data Class for this adapter
 */


class LibFragChooseFileRVListAdapter(
    private val createFileViewModel: CreateFileViewModel,
    private val libraryViewModel: LibraryViewModel,
    private val context: Context,
    private val deletePopUpViewModel: DeletePopUpViewModel,
    private val navController: NavController) :
    ListAdapter<File, LibFragChooseFileRVListAdapter.LibFragChooseFileViewHolder>(FileDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragChooseFileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragChooseFileViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibFragChooseFileViewHolder, position: Int) {
        holder.bind(getItem(position),libraryViewModel,createFileViewModel,deletePopUpViewModel,navController )
    }

    class LibFragChooseFileViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: File,
                 libraryViewModel: LibraryViewModel,
                 createFileViewModel: CreateFileViewModel,
                 deletePopUpViewModel: DeletePopUpViewModel,
                 navController: NavController
        ){
            binding.contentBindingFrame.removeAllViews()
//            親レイアウトのclick listener
           LibrarySetUpItems(libraryViewModel,deletePopUpViewModel,navController).setUpRVBaseSelectFileMoveTo(
               rvItemBaseBinding = binding,
               item = item,
               context = context,
               createFileViewModel = createFileViewModel,
           )

        }

        }
    }
object FileDiffCallback : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.fileId == newItem.fileId
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem ==newItem
    }

}