package com.example.tangochoupdated

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.drawable.Drawable
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.TransitionManager
import android.view.*
import android.view.View.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
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









    /**
     * Vertical View Holder Class
     */
    class LibraryViewHolder (private val binding: ItemCoverCardBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){

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
            folderBinding.txvFileTitle.text = item.id.toString()
            folderBinding.imvFileType.setImageDrawable(image)
            return folderBinding
        }
        fun setViewSelected(binding: ItemCoverCardBaseBinding,context: Context) {
            val selectedIcon = ContextCompat.getDrawable(context, R.drawable.circle_selected)
            binding.btnSelect.setImageDrawable(selectedIcon)

        }
        fun setViewUnselected(binding: ItemCoverCardBaseBinding,context: Context){
            val selectedIcon = ContextCompat.getDrawable(context, R.drawable.circle_select)
            binding.btnSelect.setImageDrawable(selectedIcon)
        }
        fun setViewSelectable(binding: ItemCoverCardBaseBinding,context: Context) {
            binding.btnDelete.visibility = GONE
            binding.btnEditWhole.visibility = GONE
            binding.stubTag.visibility = GONE
            binding.btnAddNewCard.visibility = GONE
            val selectedIcon = ContextCompat.getDrawable(context, R.drawable.circle_select)
            binding.btnSelect.setImageDrawable(selectedIcon)
            binding.btnSelect.visibility = VISIBLE


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

        fun bind(item: LibraryRV, clickListener: DataClickListener ){
            item.position = adapterPosition


            val mainFrameBinding = when (item.type){
              LibRVViewType.Folder, LibRVViewType.FlashCardCover -> {
                  val a = getFileBindingWithContent(item,context)
                  a.btnAdd.setOnClickListener {
                      clickListener.onClickAdd(item)
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

            if(item.selectable==true){
                setViewSelectable(baseBinding,context)
                if (item.selected){
                    setViewSelected(baseBinding,context)

                } else if(!item.selected) {
                    setViewUnselected(baseBinding,context)
                }
            }

            baseBinding.root.children.iterator().forEachRemaining {
                when(it.id){
                    baseBinding.btnEditWhole.id -> it.setOnClickListener {
                        clickListener.onClickEdit(item)
                    }
                    baseBinding.btnDelete.id -> it.setOnClickListener {
                        clickListener.onClickDelete(item)
                    }
                }

            }

            baseBinding.btnSelect.setOnClickListener {
                if (item.selected){
                    clickListener.onUnselect(item,binding.btnSelect)
                } else if(!item.selected) {
                    clickListener.onSelect(item,binding.btnSelect)
                }

            }

            baseBinding.stubMain.setOnTouchListener (object:MyTouchListener(context){
                override fun onSingleTap() {
                    super.onSingleTap()
                    if(item.leftSwiped){
                        binding.btnDelete.visibility = GONE
                        binding.btnEditWhole.visibility = GONE
                    } else if(item.selectable){
                        if (item.selected){
                            clickListener.onUnselect(item,binding.btnSelect)
                        } else if (!item.selected) {
                            clickListener.onSelect(item,binding.btnSelect)
                        }

                    }
                    else{
                        clickListener.onClickMain(item)
                    }
                }

                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    if(!item.leftSwiped&&!item.selectable){
                        baseBinding.root.layoutTransition = LayoutTransition()

                        baseBinding.root.layoutTransition.apply {
                            enableTransitionType(LayoutTransition.CHANGING)
                            setDuration(200)
                        }

                        binding.btnDelete.visibility = VISIBLE
                        binding.btnEditWhole.visibility = VISIBLE

                    }else return
                }

                override fun onLongClick() {
                    super.onLongClick()
                    clickListener.onLongClickMain()
                    clickListener.onSelect(item,binding.btnSelect)
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


    }

    /**
     * Horizontal View Holder
     */

    class GestureListener(
        val dataClickListener: DataClickListener,
        val binding: ItemCoverCardBaseBinding,
        val  item: LibraryRV,
        val viewId: Int,
        var leftSwiped:Boolean = false,
        var longPressed:Boolean = false) : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            if(leftSwiped){
                when (viewId){
                    binding.btnDelete.id -> dataClickListener.onClickDelete(item)
                    binding.btnEditWhole.id -> dataClickListener.onClickEdit(item)
                    else -> {
                        binding.btnDelete.visibility = VISIBLE
                        binding.btnEditWhole.visibility = VISIBLE
                        leftSwiped != leftSwiped
                    }

                }
                binding.btnDelete.visibility = VISIBLE
                binding.btnEditWhole.visibility = VISIBLE
                leftSwiped != leftSwiped
            }


            return true
        }


        private val SWIPE_VELOCITY_THRESHOLD = 400
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
                        binding.btnDelete.visibility = VISIBLE
                        binding.btnEditWhole.visibility = VISIBLE
                        leftSwiped != leftSwiped
//                        swipe right
                    } else {
                        binding.btnDelete.visibility = VISIBLE
                        binding.btnEditWhole.visibility = VISIBLE
                        leftSwiped != leftSwiped

                        if(viewId==binding.stubMain.id&&!leftSwiped){
                            binding.btnDelete.visibility = VISIBLE
                            binding.btnEditWhole.visibility = VISIBLE
                            leftSwiped != leftSwiped
                        }

                    }
                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {

                } else {

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
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LibraryRV, newItem: LibraryRV): Boolean {
        return oldItem.equals(newItem)
    }

}

/**
 * Interface that can be called as per your wish.
 * I usually assign it inside the Fragment/Activity from where I am using the above Adapter.
 * like
 *class MyFragment : Fragment(), DataClickListener
 */
interface DataClickListener {

    fun onLongClickMain(){}
    fun onSelect(item: LibraryRV, imageView: ImageView)
    fun onClickEdit(item: LibraryRV)
    fun onClickAdd(item: LibraryRV)
    fun onClickDelete(item: LibraryRV)
    fun onClickMain(item: LibraryRV)
    fun onUnselect(item: LibraryRV,imageView: ImageView)

}


