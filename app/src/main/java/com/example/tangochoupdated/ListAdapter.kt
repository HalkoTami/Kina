package com.example.tangochoupdated

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.*
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.*
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
        return LibraryViewHolder(ItemCoverCardBaseBinding.inflate(layoutInflater, parent, false))
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
    class LibraryViewHolder (private val binding: ItemCoverCardBaseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LibraryRV, clickListener: DataClickListener, context: Context) {
            fun setVisibilityStart(){
                binding.btnSelect.visibility= GONE
                binding.btnDelete.visibility= GONE
                binding.btnDelete.visibility = GONE
            }
            fun setFileVisibility(type:LibRVViewType,item: LibraryRV){
                binding.stubTag.visibility = GONE
                binding.btnAddNewCard.visibility = GONE

                val folderBinding = ItemCoverFileBinding.bind(binding.root)
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
            fun setVisibilityOnLongClickMain(){
                setVisibilityStart()
                binding.stubTag.visibility = GONE
                binding.btnAddNewCard.visibility = GONE
                val selectedIcon = ContextCompat.getDrawable(context,R.drawable.circle_selected)
                binding.btnSelect.setImageDrawable(selectedIcon)

            }
            fun setVisibilityOnSwipeLeft(type: LibRVViewType){
                binding.btnSelect.visibility = GONE
                binding.btnDelete.visibility = VISIBLE
                if(type==LibRVViewType.Folder || type == LibRVViewType.FlashCardCover){
                    binding.btnEditWhole.visibility = VISIBLE
                } else{
                    binding.btnEditWhole.visibility = INVISIBLE
                }

            }
            fun setStringCard(item:LibraryRV){
                val stringCardBinding = ItemCoverCardStringBinding.bind(binding.root)
                val stringData = item.card?.stringData


                stringCardBinding.txvFrontTitle.text = stringData?.frontTitle
                stringCardBinding.txvFrontText .text = stringData?.frontText
                stringCardBinding.txvBackTitle .text = stringData?.backTitle!!
                stringCardBinding.txvBackText.text = stringData.backText

                stringCardBinding.root.setOnClickListener {
                    clickListener.onClickEdit(item.id,it.id)
                }
                binding.stubMain.addView(stringCardBinding.root)
            }

            fun setChoiceCard(item: LibraryRV){
                val choiceCardBinding = ItemCoverCardQuizBinding.bind(binding.root)
                choiceCardBinding.txvQuestion.text = item.card?.quizData?.question
                choiceCardBinding.txvAnswer.text = item.card?.quizData?.answerPreview
                choiceCardBinding.root.setOnClickListener {

                    clickListener.onClickEdit(item.id,it.id)
                }

            }
            fun setTag(item: LibraryRV){
                if (item.tag!=null){
                    val tagBinding = ItemCoverTagsBinding.bind(binding.root)
                    tagBinding.txvTag.text = item.tag.onEach { "#${it.title} " }.toString()
                    tagBinding.root.setOnClickListener {
                        clickListener.onClickEdit(item.id,it.id)
                    }
                    binding.stubTag.addView(tagBinding.root)
                } else {
                    binding.stubTag.visibility = INVISIBLE
                }
            }




            when(item.type){
                LibRVViewType.Folder ->  setFileVisibility(LibRVViewType.Folder,item)

                LibRVViewType.FlashCardCover -> setFileVisibility(LibRVViewType.FlashCardCover,item)

                LibRVViewType.StringCard -> setStringCard(item)

                LibRVViewType.ChoiceCard -> setChoiceCard(item)

                else -> return
            }
            setTag(item)
            binding.root.setOnTouchListener { v, event ->
                v.performClick()
                object: OnSwipeTouchListener(context){
                    override fun onLongClick() {
                        super.onLongClick()
                        setVisibilityOnLongClickMain()
                        clickListener.onLongClickMain(item.type,item.id)
                    }
                }.onTouch(v,event)
            }
            binding.stubMain.setOnTouchListener { v, event ->
                v.performClick()
                object : OnSwipeTouchListener(context){
                    override fun onSwipeLeft() {
                        super.onSwipeLeft()
                        setVisibilityOnSwipeLeft(item.type)
                    }
                }.onTouch(v, event)
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
    fun onClickEdit(id: Int,viewId :Int)
    fun onClickTags()
    fun oncClickSelect()
    fun onClickAdd()
    fun onClickAddFolder()
    fun onClickEditBack()
    fun onClickEditFront()

}


