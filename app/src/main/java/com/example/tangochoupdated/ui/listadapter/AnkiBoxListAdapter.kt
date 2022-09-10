package com.example.tangochoupdated.ui.listadapter

import android.content.Context
import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.AnkiBoxFragments
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel


/**
 * Custom Data Class for this adapter
 */


class AnkiBoxListAdapter(
    private val context: Context,
    private val ankiBoxFragViewModel: AnkiBoxFragViewModel,
    private  val tab:AnkiBoxFragments,
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
                                 val ankiBoxFragViewModel: AnkiBoxFragViewModel,
                                 val tab: AnkiBoxFragments,
                                 val lifecycleOwner: LifecycleOwner) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: Any, ){
            val content =
            when(item){
                is File -> {
                    val binding = AnkiHomeFragRvItemFileBinding.inflate(LayoutInflater.from(context))
                    AnkiBoxFragViewSetUp().setUpRVFileBinding(binding, item,tab, ankiBoxVM = ankiBoxFragViewModel ,context,lifecycleOwner)

                    binding.root
                }
                is Card -> {
                    val binding = AnkiHomeFragRvItemCardBinding.inflate(LayoutInflater.from(context))
                    binding.apply {
                       AnkiBoxFragViewSetUp().setUpRVStringCardContent(binding.stringContentBinding,item.stringData)
                        binding.stringContentBinding.apply {
                            txvFrontTitle.text = "order " + item.libOrder
                            txvBackTitle.text = "id " + item.id
                        }
                    }
                    binding.root
                }
                else -> return
            }
            binding.root.addView(content)




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