package com.koronnu.kina.ui.listadapter

import android.content.Context
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.*
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.listener.recyclerview.LibraryRVChooseFileMoveToCL
import com.koronnu.kina.ui.view_set_up.LibrarySetUpItems
import com.koronnu.kina.ui.viewmodel.ChooseFileMoveToViewModel
import com.koronnu.kina.ui.viewmodel.LibraryBaseViewModel


/**
 * Custom Data Class for this adapter
 */


class LibFragChooseFileRVListAdapter(
    private val context: Context,
    private val chooseFileMoveToViewModel: ChooseFileMoveToViewModel,
    private val libraryViewModel: LibraryBaseViewModel) :
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
                 libraryViewModel: LibraryBaseViewModel
        ){
            fun addCL(){ binding.apply {
                arrayOf(
                    libRvBaseContainer,
                    rvBaseFrameLayLeft,
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
            fun setUpViewFirst(){
                binding.apply {
                    contentBindingFrame.removeAllViews()
                    arrayOf(btnDelete,btnEditWhole,btnAddNewCard).onEach {
                        changeViewVisibility(it,false)
                    }
                }
            }
            fun setLeftContent(){
                binding.apply {
                    btnSelect.apply {
                        setImageDrawable(
                            AppCompatResources.getDrawable(context,
                                when(item.fileStatus){
                                    FileStatus.FOLDER -> R.drawable.icon_move_to_folder
                                    FileStatus.FLASHCARD_COVER -> R.drawable.icon_move_to_flashcard_cover
                                    else -> return
                                }
                            )
                        )
                    }
                    val movingItemAlreadyInRvItem = chooseFileMoveToViewModel.getMovingItemsParentFileId == item.fileId
                    arrayOf(btnSelect,txvMove).onEach { changeViewVisibility(it, movingItemAlreadyInRvItem.not()) }
                }
            }
           setUpViewFirst()
//            親レイアウトのclick listener
            val viewSetUp = LibrarySetUpItems()
            val fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
            viewSetUp.setUpRVFileBinding(fileBinding,item,context)
            binding.contentBindingFrame.addView(fileBinding.root)
            addCL()
                setLeftContent()
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