package com.koronnu.kina.ui.listadapter

import android.content.Context
import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.*
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.customClasses.enumClasses.AnkiBoxFragments
import com.koronnu.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.koronnu.kina.ui.viewmodel.AnkiBoxViewModel


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
                                 val lifecycleOwner: LifecycleOwner) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: Any, ){

            binding.frameLayAnkiBoxRvContent.removeAllViews()
            val content =
            when(item){
                is File -> {
                    val fileBinding = AnkiHomeFragRvItemFileBinding.inflate(LayoutInflater.from(context))
                    AnkiBoxFragViewSetUp().setUpRVFileBinding(fileBinding, item,tab!!, ankiBoxVM = ankiBoxFragViewModel ,context,lifecycleOwner)
                    binding.frameLayAnkiBoxRvContent.layoutParams.height = context.resources.getDimensionPixelSize(
                        R.dimen.anki_box_rv_item_height)
                    binding.frameLayAnkiBoxRvContent.requestLayout()
                    fileBinding.root
                }
                is Card -> {
                    val cardBinding = AnkiHomeFragRvItemCardBinding.inflate(LayoutInflater.from(context))
                    AnkiBoxFragViewSetUp().setUpRVCard(cardBinding,item,lifecycleOwner,ankiBoxFragViewModel)
                    binding.frameLayAnkiBoxRvContent.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    binding.frameLayAnkiBoxRvContent.requestLayout()
                    changeViewVisibility(cardBinding.checkboxAnkiRv,(tab==null).not())
                    cardBinding.root
                }
                else -> return
            }
            binding.frameLayAnkiBoxRvContent.addView(content)




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