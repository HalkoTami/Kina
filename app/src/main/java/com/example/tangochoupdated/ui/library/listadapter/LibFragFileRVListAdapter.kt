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
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.db.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryAddClickListeners
import com.example.tangochoupdated.ui.library.LibraryViewModel


/**
 * Custom Data Class for this adapter
 */


class LibFragFileRVListAdapter(
    private val createFileViewModel: CreateFileViewModel,
    private val libraryViewModel: LibraryViewModel,
    private val context: Context,
    private val chooseFileMoveTo:Boolean) :
    ListAdapter<File, LibFragFileRVListAdapter.LibFragFileViewHolder>(FileDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragFileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragFileViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibFragFileViewHolder, position: Int) {
        holder.bind(getItem(position),libraryViewModel,createFileViewModel,chooseFileMoveTo)
    }

    class LibFragFileViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){
        lateinit var fileBinding:LibraryFragRvItemFileBinding
        fun bind(item: File,
                 libraryViewModel: LibraryViewModel ,
                 createFileViewModel: CreateFileViewModel,
                 chooseFileMoveTo: Boolean){
            binding.contentBindingFrame.removeAllViews()
//            親レイアウトのclick listener
            fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
            binding.contentBindingFrame.addView(fileBinding.root)


            binding.contentBindingFrame.layoutParams.height = 200
            binding.contentBindingFrame.requestLayout()
            fileBinding.apply{

                txvFileTitle.text = item.title.toString()
                imvFileType.setImageDrawable(when(item.fileStatus){
                    FileStatus.FOLDER -> GetCustomDrawables().getFileIconByCol(item.colorStatus ?:ColorStatus.GRAY,context)
                    FileStatus.TANGO_CHO_COVER -> GetCustomDrawables().getFlashCardIconByCol(item.colorStatus ?:ColorStatus.GRAY,context)
                    else -> throw IllegalArgumentException()
                })
            }
            LibraryAddClickListeners().fragLibFileRVAddCL(binding,fileBinding,context,item,libraryViewModel,createFileViewModel,chooseFileMoveTo)
            if(chooseFileMoveTo){
                binding.btnSelect.setImageDrawable(AppCompatResources.getDrawable(context,
                when(item.fileStatus){
                    FileStatus.FOLDER -> R.drawable.icon_move_to_folder
                    FileStatus.TANGO_CHO_COVER -> R.drawable.icon_move_to_flashcard_cover
                    else -> return
                }))
            }
            binding.root.tag = LibRVState.Plane
            binding.btnAddNewCard.visibility = View.GONE
            binding.linLaySwipeShow.visibility = GONE

        }

        }
    }
private object FileDiffCallback : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.fileId == newItem.fileId
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem ==newItem
    }

}