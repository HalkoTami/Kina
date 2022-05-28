package com.example.tangochoupdated

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Embedded
import com.example.tangochoupdated.databinding.ItemCoverCardStringBinding
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileOrCard
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import kotlin.coroutines.coroutineContext


/**
 * Custom Data Class for this adapter
 */

class LibraryListAdapter(val clickListener: DataClickListener) :
    ListAdapter<FileOrCard, RecyclerView.ViewHolder>(ListCheckDiffCallback()) {

    /**
     * This Function will help you out in choosing whether you want vertical or horizontal VIEW TYPE
     */
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
             CLASS_TYPE_CARD-> when(getItem(position).card!!.cardStatus){
                 CardStatus.STRING-> CARD_TYPE_STRING
                 CardStatus.QUIZ-> CARD_TYPE_QUIZ
                 CardStatus.MARKER-> CARD_TYPE_MARKER
             }
            CLASS_TYPE_FILE -> when(getItem(position).file!!.fileStatus) {
                FileStatus.FILE -> FILE_TYPE_FILE
                FileStatus.TANGO_CHO -> FILE_TYPE_TANGO_CHO
            }
            else -> {return 0}
        }
    }

    /**
     * The View Type Selected above will help this function in choosing appropriate ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CARD_TYPE_STRING -> StringCardViewHolder.from(parent)
            CARD_TYPE_QUIZ -> QuizCardViewHolder.from(parent)
            CARD_TYPE_MARKER -> MarkerCardViewHolder.from(parent)
            FILE_TYPE_FILE -> FileViewholder.from(parent)
            FILE_TYPE_TANGO_CHO -> TangochoViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    /**
     * The View Holder Created above are used here.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HorizontalViewHolder -> {
                val item = getItem(position) as LibCoverData.CardClass
                holder.bind(item.card, clickListener)
            }
            is VerticalViewHolder -> {
                val item = getItem(position) as LibCoverData.FileClass
                holder.bind(item.yourData, clickListener)
            }
        }
    }

    /**
     * Vertical View Holder Class
     */
    class StringCardViewHolder private constructor(val binding: ItemCoverCardStringBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Card, clickListener: DataClickListener) {
            binding.root.setOnClickListener {
                clickListener.onTouch(it, item.id, CARD_TYPE_STRING)
            }
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
            binding.txvFrontText.text = item.stringData?.frontText
            binding.txvFrontTitle.text= item.stringData?.frontTitle
            binding.txvBackTitle.text= item.stringData?.backTitle
            binding.txvBackText.text = item.stringData?.backTitle



        }

        companion object {
            fun from(parent: ViewGroup): StringCardViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =  ItemCoverCardStringBinding.inflate(layoutInflater, parent, false)
                return StringCardViewHolder(view)
            }
        }
    }

    /**
     * Horizontal View Holder
     */
    class HorizontalViewHolder private constructor(val binding: <REPLACE_WITH_BINDING_OBJECT>) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: YourData, clickListener: DataClickListener) {
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

        companion object {
            fun from(parent: ViewGroup): HorizontalViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                val binding =  <REPLACE_WITH_BINDING_OBJECT>.inflate(layoutInflater, parent, false)
                return HorizontalViewHolder(binding)
            }
        }
    }
}

/**
 * This function checks the difference between 2 different Lists.
 * 1. Old List
 * 2. New List
 */
class ListCheckDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        var a = arrayListOf<FileOrCard>()
        a.add(FileOrCard.FileCover(File()))
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

/**
 * Interface that can be called as per your wish.
 * I usually assign it inside the Fragment/Activity from where I am using the above Adapter.
 * like
 *class MyFragment : Fragment(), DataClickListener
 */
interface DataClickListener {
    fun onTouchWhole(view: View, dataId: Int, viewType: Int)
    fun onTouchDelete(id: Int, dataId: Int, viewType: Int)
}

/**
 * Your DataItem Class
 */
sealed class LibCoverData {
    data class CardClass(val cardData: Card) : LibCoverData(){
        override val customPosition: Int
            get() = cardData.id
        override val card: Card?
            get() = cardData
        override val file: File?
            get() = null
        override val viewType: Int
        get() = CLASS_TYPE_CARD


    }

    data class FileClass(val fileData: File) : LibCoverData() {

        override val customPosition: Int
            get() = fileData.fileId
        override val viewType: Int
            get() = CLASS_TYPE_FILE
        override val card: Card?
            get() = null
        override val file: File?
            get() = fileData
    }
    abstract val customPosition:Int
    abstract val viewType: Int
    abstract val card:Card?
    abstract val file:File?
}

sealed class FileOrCard {
    abstract val libraryCard: Card?
    abstract val file: File?
    data class FileCover(val dbFile: File): FileOrCard(){
        override val libraryCard: Card?
            get() = null
        override val file: File?
            get() = dbFile
    }
    data class CardCover(val dbCard: Card):FileOrCard(){
        override val file: File?
            get() = null
        override val libraryCard: Card?
            get() = dbCard

    }
}


    private const val CARD_TYPE_STRING = 1
    private const val CARD_TYPE_MARKER = 2
    private const val CARD_TYPE_QUIZ = 3
    private const val CLASS_TYPE_FILE = 4
    private const val CLASS_TYPE_CARD = 5
    private const val FILE_TYPE_FILE = 6
    private const val FILE_TYPE_TANGO_CHO = 7

