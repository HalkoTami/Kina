package com.example.tangochoupdated

import android.opengl.Visibility
import android.text.Layout
import android.view.*
import android.view.View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
import android.view.View.VISIBLE
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
import com.example.tangochoupdated.databinding.ItemCoverCardStringBinding
import com.example.tangochoupdated.databinding.ItemCoverFileBinding
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.library.LibraryRVViewModel


/**
 * Custom Data Class for this adapter
 */


class LibraryListAdapter(val dataClickListener: DataClickListener, ) :
    ListAdapter<LibraryRV, LibraryListAdapter.LibraryViewHolder>(MyDiffCallback) {

    /**
     * This Function will help you out in choosing whether you want vertical or horizontal VIEW TYPE
     */


    /**
     * The View Type Selected above will help this function in choosing appropriate ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibraryViewHolder(ItemCoverCardBaseBinding.inflate(layoutInflater, parent, false),layoutInflater)
    }

    /**
     * The View Holder Created above are used here.
     */




    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position),dataClickListener)
    }

    /**
     * Vertical View Holder Class
     */
    class LibraryViewHolder (private val binding: ItemCoverCardBaseBinding,private val layoutInflater: LayoutInflater) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LibraryRV, clickListener: DataClickListener, ) {
            binding.btnDelete.setOnClickListener {
                clickListener.onTouchDelete(item.id,item.type)
            }
            binding.stubMain.setOnClickListener {
                clickListener.onTouchMain()
            }
            binding.btnSelect.setOnClickListener {
                clickListener.oncClickSelect()
            }
            binding.btnEditWhole.setOnClickListener {
                clickListener.onClickEdit()
            }
            when(item.type){
                LibRVViewType.Folder -> {

                    val folderBinding = ItemCoverFileBinding.inflate(layoutInflater)
                    val folderData = item.folderData!!
                    folderBinding.txvFileTitle.text = folderData.title
                    folderBinding.txvFileAmount.text=  "${folderData.containingFolder}個"
                    folderBinding.txvCardAmount.text= "${folderData.containingCard}枚"
                    folderBinding.txvTangoChoAmount.text = "${folderData.containingFlashCardCover}個"
                    binding.stubMain.addView(folderBinding.root)


                }

                else -> {return}
            }


//
//                TODO データに応じたレイアウトの振り分け！
//            TODO クリックリスナー！
            /**
             * change all your view data here
             * assign click listeners here
             *
             *  example -->
             *
             *  binding.xyz.setOnClickListener {
             *     clickListener.onClick(item)
             *  }
             */




        }

    }

    /**
     * Horizontal View Holder
     */

}

/**
 * This function checks the difference between 2 different Lists.
 * 1. Old List
 * 2. New List
 */
private object MyDiffCallback : DiffUtil.ItemCallback<LibraryRV>() {
    override fun areItemsTheSame(oldItem: LibraryRV, newItem: LibraryRV): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LibraryRV, newItem: LibraryRV): Boolean {
        return oldItem.id == newItem.id
    }

}

/**
 * Interface that can be called as per your wish.
 * I usually assign it inside the Fragment/Activity from where I am using the above Adapter.
 * like
 *class MyFragment : Fragment(), DataClickListener
 */
interface DataClickListener {
    fun onTouchWhole( id:Int, viewType: LibRVViewType)
    fun onTouchDelete(id: Int, viewType: LibRVViewType)
    fun onTouchMain()
    fun onLongClickMain()
    fun onClickEdit()
    fun onClickTags()
    fun oncClickSelect()
    fun onClickAdd()

}

/**
 * Your DataItem Class
 */




    private const val CARD_TYPE_STRING = 1
    private const val CARD_TYPE_MARKER = 2
    private const val CARD_TYPE_QUIZ = 3
    private const val CLASS_TYPE_FILE = 4
    private const val CLASS_TYPE_CARD = 5
    private const val FILE_TYPE_FILE = 6
    private const val FILE_TYPE_TANGO_CHO = 7

