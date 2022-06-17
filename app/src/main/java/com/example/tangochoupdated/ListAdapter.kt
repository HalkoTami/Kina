package com.example.tangochoupdated

import android.content.Context
import android.graphics.drawable.Drawable
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.TransitionManager
import android.view.*
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV


/**
 * Custom Data Class for this adapter
 */


class LibraryListAdapter(val dataClickListener: DataClickListener) :
    ListAdapter<LibraryRV, LibraryListAdapter.LibraryViewHolder>(MyDiffCallback) {

    /**
     * This Function will help you out in choosing whether you want vertical or horizontal VIEW TYPE
     */


    /**
     * The View Type Selected above will help this function in choosing appropriate ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibraryViewHolder(ItemCoverCardBaseBinding.inflate(layoutInflater, parent, false),parent.context)
    }

    /**
     * The View Holder Created above are used here.
     */




    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position),dataClickListener)
    }

    fun getBaseBindingStart(binding:ItemCoverCardBaseBinding,item: LibraryRV):ItemCoverCardBaseBinding{
        binding.btnSelect.visibility= GONE
        binding.btnDelete.visibility= GONE
        binding.btnEditWhole.visibility = GONE
        binding.stubMain.removeAllViews()

        when (item.type){
            LibRVViewType.Folder, LibRVViewType.FlashCardCover -> {
                binding.stubTag.visibility = GONE
                binding.btnAddNewCard.visibility = GONE
            }
            else -> {
                binding.btnAddNewCard.visibility = VISIBLE
                if(item.tag!=null){
                    binding.stubTag.visibility = VISIBLE
                } else {
                    binding.stubTag.visibility = INVISIBLE
                }
            }

        }
        return binding
    }
    fun setVisibilityOnSwipeLeft(type: LibRVViewType,binding:ItemCoverCardBaseBinding){
        binding.btnSelect.visibility = GONE
        binding.btnDelete.visibility = VISIBLE
        if(type==LibRVViewType.Folder ){
            binding.btnEditWhole.visibility = VISIBLE
        } else{
            binding.btnEditWhole.visibility = INVISIBLE
        }

    }





    fun getFileBindingWithContent(item: LibraryRV,context: Context):ItemCoverFileBinding{
        val folderBinding =ItemCoverFileBinding.inflate(LayoutInflater.from(context))
        val folderData = item.file!!
        val image: Drawable
        when (item.type) {
            LibRVViewType.Folder -> {
                folderBinding.txvFileAmount.text = "${folderData.childFoldersAmount}個"
                folderBinding.txvCardAmount.text = "${folderData.childCardsAmount}枚"
                folderBinding.txvTangoChoAmount.text =
                    "${folderData.childFlashCardCoversAmount}個"
                image = ContextCompat.getDrawable(context, R.drawable.icon_file)!!
            }
            LibRVViewType.FlashCardCover -> {
                folderBinding.txvCardAmount.text = "${folderData.childCardsAmount}枚"
                image = ContextCompat.getDrawable(context, R.drawable.icon_library)!!
            }
            else -> return folderBinding
        }
        folderBinding.txvFileTitle.text = folderData.title
        folderBinding.imvFileType.setImageDrawable(image)
        return folderBinding
    }
    fun setViewOnLongClickMain(binding: ItemCoverCardBaseBinding,context: Context) :ItemCoverCardBaseBinding{
        binding.btnDelete.visibility = GONE
        binding.btnEditWhole.visibility = GONE
        binding.stubTag.visibility = GONE
        binding.btnAddNewCard.visibility = GONE
        val selectedIcon = ContextCompat.getDrawable(context, R.drawable.circle_selected)
        binding.btnSelect.setImageDrawable(selectedIcon)
        binding.btnSelect.visibility = VISIBLE
        return binding

    }
    fun getStringCardBindingWithContent(item: LibraryRV,context: Context):ItemCoverCardStringBinding {
        val stringBinding =  ItemCoverCardStringBinding.inflate(LayoutInflater.from(context))
        val stringData = item.card?.stringData


        stringBinding.txvFrontTitle.text = stringData?.frontTitle
        stringBinding.txvFrontText.text = stringData?.frontText
        stringBinding.txvBackTitle.text = stringData?.backTitle!!
        stringBinding.txvBackText.text = stringData.backText
        return stringBinding

    }
    fun getTagBindingWithContent(item: LibraryRV,context: Context) :ItemCoverTagsBinding{
            val tagBinding = ItemCoverTagsBinding.inflate(LayoutInflater.from(context))
        if(item.tag!=null){
            tagBinding.txvTag.text = item.tag!!.onEach { "#${it.title} " }.toString()

        }
        return tagBinding
    }

    fun getChoiceCardBindingWithContent(
        item: LibraryRV,
        context: Context
    ): ItemCoverCardQuizBinding {
        val choiceCardQuizBinding = ItemCoverCardQuizBinding.inflate(LayoutInflater.from(context))
        choiceCardQuizBinding.txvQuestion.text = item.card?.quizData?.question
        choiceCardQuizBinding.txvAnswer.text = item.card?.quizData?.answerPreview
        return choiceCardQuizBinding


    }


    /**
     * Vertical View Holder Class
     */
    inner class LibraryViewHolder (private val binding: ItemCoverCardBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){



        fun bind(item: LibraryRV, clickListener: DataClickListener ){


          val mainFrameBinding = when (item.type){
              LibRVViewType.Folder, LibRVViewType.FlashCardCover -> {
                  val a = getFileBindingWithContent(item,context)
                  a.root.children.iterator().forEachRemaining {
                      it.setOnTouchListener { v, event ->
                      v.performClick()
                      GestureDetector(context,GestureListener(clickListener,v.id,item)).onTouchEvent(event)
                  }
                  }
                  a
              }
              LibRVViewType.StringCard -> getStringCardBindingWithContent(item,context)
              LibRVViewType.ChoiceCard -> getChoiceCardBindingWithContent(item,context)
              else -> null
          }
            val tagBinding = getTagBindingWithContent(item,context)
            val baseBinding = getBaseBindingStart(binding,item)
            baseBinding.stubMain.addView(mainFrameBinding!!.root)
            baseBinding.stubTag.addView(tagBinding.root)









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


    }


    /**
     * Horizontal View Holder
     */

    class GestureListener(val dataClickListener: DataClickListener, val viewId: Int,val  item: LibraryRV) : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        private val SWIPE_VELOCITY_THRESHOLD = 300
        private val SWIPE_THRESHOLD = 100
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffY = e2!!.y - e1!!.y
            val diffX = e2.x - e1.x
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {

//                        swipe right
                    } else {

                        dataClickListener.onLongClickMain(item.type,item.id)

                    }
                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    false
                } else {
                    false
                }
            }
            return true

        }

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

    fun onSwipeLeft(binding: ItemCoverCardBaseBinding,context: Context){
        val c = binding.root as ViewGroup
        val d = ItemCoverCardQuizBinding.inflate(LayoutInflater.from(context)).root as View
        val scene: Scene = Scene(c, d)
        TransitionManager.go(scene,ChangeBounds())
    }
    fun onLongClickMain(type: LibRVViewType, id: Int)
    fun onClickEdit(id: Int,viewId :Int)
    fun onClickAdd(type: LibRVViewType,id: Int)
    fun onClickDelete(type: LibRVViewType,id: Int)
    fun onClickMain(type: LibRVViewType,id: Int)

}


