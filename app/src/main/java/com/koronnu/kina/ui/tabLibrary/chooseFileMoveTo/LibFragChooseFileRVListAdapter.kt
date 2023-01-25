package com.koronnu.kina.ui.tabLibrary.chooseFileMoveTo

import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.actions.changeMulVisibility
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.setClickListeners
import com.koronnu.kina.databinding.*
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.source.local.entity.enumclass.FileStatus


/**
 * Custom Data Class for this adapter
 */


class LibFragChooseFileRVListAdapter(
    private val chooseFileMoveToViewModel: ChooseFileMoveToViewModel, ) :
    ListAdapter<File, LibFragChooseFileRVListAdapter.LibFragChooseFileViewHolder>(FileDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragChooseFileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragChooseFileViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: LibFragChooseFileViewHolder, position: Int) {
        holder.bind(getItem(position),chooseFileMoveToViewModel)
    }

    override fun onViewAttachedToWindow(holder: LibFragChooseFileViewHolder) {
        super.onViewAttachedToWindow(holder)
        chooseFileMoveToViewModel.doAfterRVAttached()
    }


    class LibFragChooseFileViewHolder (private val binding: LibraryFragRvItemBaseBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: File,
                 chooseFileMoveToViewModel: ChooseFileMoveToViewModel,
        ){
            val context = binding.root.context
            val fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
            fileBinding.file = item
            fun addCL(){
                binding.apply {
                    val clickListener = LibraryRVChooseFileMoveToCL(item, binding, chooseFileMoveToViewModel)
                    val clickableItems = arrayOf(
                        contentBindingFrame as View,
                        rvBaseFrameLayLeft)
                    setClickListeners(clickableItems,clickListener)
                }
            }
            fun setUpViewFirst(){
                binding.apply {
                    contentBindingFrame.removeAllViews()
                    changeMulVisibility(arrayOf(btnDelete,btnEditWhole,btnAddNewCard),false)
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
                    arrayOf(btnSelect,txvMove).onEach { changeViewVisibility(it, chooseFileMoveToViewModel.checkRvItemMoveBtnVisible(item)) }
                }
            }
//            fun setUpFileContent(){
////                fileBinding.apply {
////                    txvFileTitle.text = item.title
////                    imvFileType.setImageDrawable(DrawableConverter(context).convertColoredFileStatusIcon(item))
////                }
//            }
           setUpViewFirst()
//            親レイアウトのclick listener
            binding.contentBindingFrame.addView(fileBinding.root)
            addCL()
            setLeftContent()
//            setUpFileContent()
        }

        }

    }


object FileDiffCallBack : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.fileId == newItem.fileId
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return newItem == oldItem
    }

}