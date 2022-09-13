package com.example.tangochoupdated.ui.listadapter

import android.content.Context
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.listener.recyclerview.LibraryRVChooseFileMoveToCL
import com.example.tangochoupdated.ui.view_set_up.LibraryAddListeners
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.view_set_up.LibrarySetUpItems
import com.example.tangochoupdated.ui.viewmodel.ChooseFileMoveToViewModel
import com.example.tangochoupdated.ui.viewmodel.DeletePopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel


/**
 * Custom Data Class for this adapter
 */


class LibFragChooseFileRVListAdapter(
    private val context: Context,
    private val chooseFileMoveToViewModel: ChooseFileMoveToViewModel,
    private val libraryViewModel: LibraryViewModel) :
    ListAdapter<File, LibFragChooseFileRVListAdapter.LibFragChooseFileViewHolder>(FileDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragChooseFileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragChooseFileViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibFragChooseFileViewHolder, position: Int) {
        holder.bind(getItem(position),chooseFileMoveToViewModel,libraryViewModel)
    }

    class LibFragChooseFileViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: File,
                 chooseFileMoveToViewModel: ChooseFileMoveToViewModel,
                 libraryViewModel: LibraryViewModel
        ){
            fun addCL(){ binding.apply {
                arrayOf(
                    libRvBaseContainer,
                    btnSelect,
                ).onEach { it.setOnClickListener(
                    LibraryRVChooseFileMoveToCL(
                        item,
                        libraryViewModel,
                        binding,
                        chooseFileMoveToViewModel
                    )
                )
                }
            }
            }
            binding.contentBindingFrame.removeAllViews()
//            親レイアウトのclick listener
            val addListeners = LibraryAddListeners()
            val viewSetUp = LibrarySetUpItems()
            val fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
            viewSetUp.setUpRVFileBinding(fileBinding,item,context)
            binding.contentBindingFrame.addView(fileBinding.root)
            binding.btnSelect.apply {
                setImageDrawable(
                    AppCompatResources.getDrawable(context,
                    when(item.fileStatus){
                        FileStatus.FOLDER -> R.drawable.icon_move_to_folder
                        FileStatus.TANGO_CHO_COVER -> R.drawable.icon_move_to_flashcard_cover
                        else -> return
                    }))
                visibility = View.VISIBLE
            }
            addCL()

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