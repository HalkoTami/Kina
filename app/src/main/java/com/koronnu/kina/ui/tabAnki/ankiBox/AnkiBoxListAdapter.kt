package com.koronnu.kina.ui.tabAnki.ankiBox

import android.content.Context
import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.util.changeViewVisibility
import com.koronnu.kina.databinding.*
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.model.enumClasses.AnkiBoxFragments
import com.koronnu.kina.data.source.local.entity.ActivityData
import com.koronnu.kina.data.source.local.entity.enumclass.ActivityStatus
import com.koronnu.kina.ui.tabLibrary.SearchDiffCallback
import com.koronnu.kina.util.view_set_up.LibrarySetUpItems


/**
 * Custom Data Class for this adapter
 */


class AnkiBoxListAdapter(
    private val context: Context,
    private val ankiBoxFragViewModel: AnkiBoxViewModel,
    private  val tab: AnkiBoxFragments?,
    private val lifecycleOwner: LifecycleOwner) :
    ListAdapter<Any, AnkiBoxListAdapter.AnkiBoxItemViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnkiBoxItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AnkiBoxItemViewHolder(AnkiHomeFragRvItemBinding.inflate(layoutInflater, parent, false),context,ankiBoxFragViewModel,tab,lifecycleOwner)
    }

    override fun onBindViewHolder(holder: AnkiBoxItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AnkiBoxItemViewHolder (private val binding: AnkiHomeFragRvItemBinding,
                                 val context: Context,
                                 val ankiBoxFragViewModel: AnkiBoxViewModel,
                                 val tab: AnkiBoxFragments?,
                                 val viewLifecycleOwner: LifecycleOwner) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: Any, ){

            binding.flAnkiBoxRvContent.removeAllViews()
            val content =
            when(item){
                is File -> {
                    val fileBinding = ListItemAnkiBoxRvFileBinding.inflate(LayoutInflater.from(context))
                    fileBinding.apply {
                        file = item
                        ankiBoxViewModel = ankiBoxFragViewModel
                        lifecycleOwner = viewLifecycleOwner
                    }
                    ankiBoxFragViewModel.ankiBoxFileAncestorsFromDB(item.fileId).observe(viewLifecycleOwner){
                        fileBinding.ancestors = it
                    }
                    ankiBoxFragViewModel.getAnkiBoxRVCards(item.fileId)                .observe(viewLifecycleOwner){
                        fileBinding.descendantsCardsAmount = it.size
                        fileBinding.progress =
                            (it.filter { it.remembered }.size.toDouble()/it.size*100).toInt()
                    }
                    ankiBoxFragViewModel.getAnkiBoxRVDescendantsFolders(item.fileId)   .observe(viewLifecycleOwner){
                        fileBinding.descendantsFolderAmount = it.size
                    }
                    ankiBoxFragViewModel.getAnkiBoxRVDescendantsFlashCards(item.fileId)   .observe(viewLifecycleOwner){
                        fileBinding.descendantsFlashCardCoversAmount = it.size
                    }
                    binding.flAnkiBoxRvContent.layoutParams.height = context.resources.getDimensionPixelSize(
                        R.dimen.anki_box_rv_item_height)
                    binding.flAnkiBoxRvContent.requestLayout()
                    fileBinding.root
                }
                is Card -> {
                    val cardBinding = ListItemAnkiBoxRvCardBinding.inflate(LayoutInflater.from(context))
                    setUpRVCard(cardBinding,item,viewLifecycleOwner,ankiBoxFragViewModel)
                    binding.flAnkiBoxRvContent.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    binding.flAnkiBoxRvContent.requestLayout()
                    changeViewVisibility(cardBinding.imvChbIsInAnkiBox,(tab==null).not())
                    cardBinding.root
                }
                else -> return
            }
            binding.flAnkiBoxRvContent.addView(content)




        }
        fun setUpRVCard(cardBinding: ListItemAnkiBoxRvCardBinding, card: Card, lifecycleOwner: LifecycleOwner, ankiBoxVM: AnkiBoxViewModel){
            val resources = cardBinding.root.context.resources
            LibrarySetUpItems().setUpRVStringCardBinding(cardBinding.bindingListItemCardString,card.stringData)

            fun setOnCL(){
                arrayOf(cardBinding.imvChbIsInAnkiBox,).onEach { it.setOnClickListener(
                    AnkiBoxRVStringCardCL(card,cardBinding,ankiBoxVM)) }
            }
            fun getStringByRemembered(remembered:Boolean):String{
                return resources.getString(if(remembered) R.string.remembered else R.string.not_remembered)
            }
            fun getStringByLastLooked(lastLooked: ActivityData?,):String{

                return resources.getString(R.string.lastLookedDate,lastLooked?.dateTime ?:resources.getString(R.string.no_data))
            }
            setOnCL()
            ankiBoxVM.ankiBoxItems.observe(lifecycleOwner){
                cardBinding.imvChbIsInAnkiBox.isSelected = it.contains(card)
            }
            ankiBoxVM.getCardActivityFromDB(card.id).observe(lifecycleOwner){
                val lastLooked =it.findLast { it.activityStatus == ActivityStatus.CARD_OPENED }
                cardBinding.txvAnkiRvLastFlipped.text = getStringByLastLooked(lastLooked)
            }
            cardBinding.apply {
                imvAnkiRvCardRemembered.isSelected = card.remembered
                txvAnkiRvRememberedStatus.text = getStringByRemembered(card.remembered)
            }



        }

        }



    }
private object CardDiffCallback : DiffUtil.ItemCallback<Card>() {
    override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
        return  oldItem == newItem
    }


}