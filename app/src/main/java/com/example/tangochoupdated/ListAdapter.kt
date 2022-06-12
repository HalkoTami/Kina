package com.example.tangochoupdated

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.*
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
import com.example.tangochoupdated.databinding.ItemCoverCardStringBinding
import com.example.tangochoupdated.databinding.ItemCoverFileBinding
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV


/**
 * Custom Data Class for this adapter
 */


class LibraryListAdapter(val dataClickListener: DataClickListener, val context:Context) :
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
        holder.bind(getItem(position),dataClickListener,context )
    }

    /**
     * Vertical View Holder Class
     */
    class LibraryViewHolder (private val binding: ItemCoverCardBaseBinding,private val layoutInflater: LayoutInflater) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LibraryRV, clickListener: DataClickListener, context: Context) {
            fun setFileVisibility(type:LibRVViewType,item: LibraryRV){
                binding.stubTag.visibility = GONE
                binding.btnAddNewCard.visibility = GONE

                val folderBinding = ItemCoverFileBinding.inflate(layoutInflater)
                val folderData = item.file!!
                val image:Drawable
                when(type){
                    LibRVViewType.Folder ->{
                        folderBinding.txvFileAmount.text=  "${folderData.childFoldersAmount}個"
                        folderBinding.txvCardAmount.text= "${folderData.childCardsAmount}枚"
                        folderBinding.txvTangoChoAmount.text = "${folderData.childFlashCardCoversAmount}個"
                        image = ContextCompat.getDrawable(context,R.drawable.icon_file)!!
                    }
                    LibRVViewType.FlashCardCover ->{
                        folderBinding.txvCardAmount.text= "${folderData.childCardsAmount}枚"
                        image = ContextCompat.getDrawable(context,R.drawable.icon_library)!!
                    }
                    else -> return
                }
                folderBinding.txvFileTitle.text = folderData.title
                folderBinding.imvFileType.setImageDrawable(image)

                folderBinding.root.setOnClickListener {
                    clickListener.onClickWholeFolder()
                }
                folderBinding.btnAdd.setOnClickListener {
                    clickListener.onClickAddFolder()
                }
                binding.stubMain.addView(folderBinding.root)
            }
            fun onLongClickMain(){
                val selectedtIcon = ContextCompat.getDrawable(context,R.drawable.circle_selected)
                clickListener.onLongClickMain(item.type, item.id)
                binding.btnSelect.setImageDrawable(selectedtIcon)
            }
            fun setStringCardVisibility(item:LibraryRV){
                val stringCardBinding = ItemCoverCardStringBinding.inflate(layoutInflater)
                val stringData = item.card?.stringData


                stringCardBinding.txvFrontTitle.text = stringData?.frontTitle
                stringCardBinding.txvFrontText .text = stringData?.frontText
                stringCardBinding.txvBackTitle .text = stringData?.backTitle!!
                stringCardBinding.txvBackText.text = stringData.backText

                stringCardBinding.btnEdtBack.setOnClickListener {
                    clickListener.onClickEdit()
                }
                stringCardBinding.btnEdtFront.setOnClickListener {
                    clickListener.onClickEditFront()
                }
                binding.stubMain.addView(stringCardBinding.root)
            }

            binding.btnSelect.visibility= GONE
            binding.btnDelete.visibility= GONE
            binding.btnDelete.visibility = GONE
            when(item.type){
                LibRVViewType.Folder ->  setFileVisibility(LibRVViewType.Folder,item)



                LibRVViewType.FlashCardCover -> setFileVisibility(LibRVViewType.FlashCardCover,item)


                else -> {return}
            }
            binding.stubMain.setOnTouchListener(object: OnSwipeTouchListener(context){
                override fun onLongClick() {
                    onLongClickMain()
                    super.onLongClick()
                }
            })




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
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: LibraryRV, newItem: LibraryRV): Boolean {
        return oldItem.file!!.fileId == newItem.file!!.fileId
    }

}

/**
 * Interface that can be called as per your wish.
 * I usually assign it inside the Fragment/Activity from where I am using the above Adapter.
 * like
 *class MyFragment : Fragment(), DataClickListener
 */
interface DataClickListener {
    fun onClickWholeFolder()

    fun onTouchWhole( )
    fun onTouchDelete()
    fun onTouchMain()
    fun onLongClickMain(type: LibRVViewType, id: Int)
    fun onClickEdit()
    fun onClickTags()
    fun oncClickSelect()
    fun onClickAdd()
    fun onClickAddFolder()
    fun onClickEditBack()
    fun onClickEditFront()

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

