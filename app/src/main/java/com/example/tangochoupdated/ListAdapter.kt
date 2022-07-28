package com.example.tangochoupdated

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.*
import android.view.View.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.LibRVState
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.library.LibRVClickListener


/**
 * Custom Data Class for this adapter
 */


class LibraryListAdapter(val dataClickListener: DataClickListener,
val mycontext: Context) :
    ListAdapter<LibraryRV, LibraryListAdapter.LibraryViewHolder>(MyDiffCallback) {



    /**
     * This Function will help you out in choosing whether you want vertical or horizontal VIEW TYPE
     */


    /**
     * The View Type Selected above will help this function in choosing appropriate ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibraryViewHolder(ItemCoverCardBaseBinding.inflate(layoutInflater, parent, false),mycontext)
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


        lateinit var fileBinding:ItemCoverFileBinding


        fun bind(item: LibraryRV, clickListener: DataClickListener ){
            item.position = adapterPosition

//            親レイアウトのclick listener
            val clickableViews = mutableListOf<View>()


            fun getStrokeColResId(colorStatus: ColorStatus):Int{
                return when(colorStatus){
                    ColorStatus.GRAY -> R.color.gray
                    ColorStatus.RED ->R.color.rvStrokeRed
                    ColorStatus.BLUE -> R.color.rvStrokeBlue
                    ColorStatus.YELLOW -> R.color.rvStrokeYellow
                }
            }
            binding.stubMain.removeAllViews()
            val drawable = binding.stubMain.background as GradientDrawable
            drawable.mutate()
            drawable.setStroke(8,
                context.getColor(getStrokeColResId(
                item.card?.colorStatus ?:item.file?.colorStatus ?:ColorStatus.GRAY)))


            when (item.type){
              LibRVViewType.Folder, LibRVViewType.FlashCardCover ->{
                  fileBinding = ItemCoverFileBinding.inflate(LayoutInflater.from(context))
                  binding.stubMain.addView(fileBinding.root)
                  fileBinding.apply{

                      txvFileTitle.text = item.file?.title.toString()
                      item.file?.apply {
                          txvFileAmount.apply {
                              if(childFoldersAmount == 0) visibility = INVISIBLE
                              else text = "$childFoldersAmount 個"
                          }
                          txvTangoChoAmount.apply {
                              if(childFlashCardCoversAmount == 0) visibility = INVISIBLE
                              else text = "$childFlashCardCoversAmount 個"
                          }
                          txvCardAmount.apply {
                              if(childCardsAmount == 0) visibility = INVISIBLE
                              else text = "$childCardsAmount 個"
                          }


                      }

                      imvFileType.setImageDrawable(when(item.type){
                          LibRVViewType.Folder -> context.getDrawable(R.drawable.icon_file)
                          LibRVViewType.FlashCardCover -> context.getDrawable(R.drawable.icon_library_plane)
                          else -> throw IllegalArgumentException()
                      })
                      clickableViews.add(btnAdd)

                  }


              }
              LibRVViewType.StringCard ->{
                  val stringBinding = ItemCoverCardStringBinding.inflate(LayoutInflater.from(context))
                  binding.stubMain.addView(stringBinding.root)

                  val stringData = item.card?.stringData
//                  stringData?.frontTitle
//                  stringData?.backTitle ?:"裏"
                  stringBinding.txvFrontTitle.text = item.card?.libOrder.toString()
                  stringBinding.txvFrontText.text = stringData?.frontText ?:"null"
                  stringBinding.txvBackTitle.text = item.card?.id.toString()
                  stringBinding.txvBackText.text = stringData?.backText




              }
              LibRVViewType.ChoiceCard -> {
                  val choiceBinding = ItemCoverCardQuizBinding.inflate(LayoutInflater.from(context))
                  binding.stubMain.addView(choiceBinding.root)

                  choiceBinding.txvQuestion.text = item.card?.quizData?.question
                  choiceBinding.txvAnswer.text = item.card?.quizData?.answerPreview

              }
              LibRVViewType.MarkerCard-> null
          }
            if(item.tag!= null){

                val tagBinding = ItemCoverTagsBinding.inflate(LayoutInflater.from(context))
                binding.stubTag.addView(tagBinding.root)
                tagBinding.txvTag.text = item.tag.onEach { "#${it.title} " }.toString()
                binding.stubTag.visibility = VISIBLE
            } else{
                binding.stubTag.visibility = GONE
            }

            binding.btnAddNewCard.visibility = when(item.type){
                LibRVViewType.MarkerCard,LibRVViewType.ChoiceCard,LibRVViewType.StringCard -> VISIBLE
                else -> GONE
            }
            binding.baseContainer.tag = LibRVState.Plane
            binding.btnSelect.visibility = GONE
//
            binding.btnDelete.visibility =  GONE
            binding.btnEditWhole.visibility =  GONE

//            ボタンのclicklitener
            clickableViews.add(binding.baseContainer)
            binding.apply {
                clickableViews.addAll(arrayOf(
                    btnDelete,btnSelect,btnEditWhole,btnAddNewCard,
                ))

            }


            clickableViews.onEach {
                it.setOnTouchListener(LibRVClickListener(it,context,item,clickListener,binding))
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


    }






    /**
     * Horizontal View Holder
     */








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
//    fun onSwipeLeft(item :LibraryRV, rvBinding:ItemCoverCardBaseBinding, fileBinding: ItemCoverFileBinding)

//    fun onSelect(item: LibraryRV, rvBinding: ItemCoverCardBaseBinding)
//fun onCickEditCard(item: LibraryRV)
//    fun onClickAdd(item: LibraryRV)
//    fun onClickMain(item: LibraryRV, rvBinding: ItemCoverCardBaseBinding)
//    fun onScrollLeft(distanceX:Float, rvBinding: ItemCoverCardBaseBinding)
//
    fun onClickSelectableItem(item: LibraryRV,selected:Boolean)
    fun onClickDelete(item: LibraryRV)
    fun onClickEdit(item: LibraryRV)
    fun openNext(item: LibraryRV)
    fun onClickAddNewCardByPosition(item:LibraryRV)
    fun onLongClickMain(item: LibraryRV)


}

