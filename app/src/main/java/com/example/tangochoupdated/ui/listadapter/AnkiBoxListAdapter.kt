package com.example.tangochoupdated.ui.listadapter

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxChildrenFragViewSetUp


/**
 * Custom Data Class for this adapter
 */


class AnkiBoxListAdapter(
    private val ankiBoxViewSetUp: AnkiBoxChildrenFragViewSetUp,
    private val context: Context) :
    ListAdapter<Any, AnkiBoxListAdapter.AnkiBoxItemViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnkiBoxItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AnkiBoxItemViewHolder(AnkiHomeFragRvItemBinding.inflate(layoutInflater, parent, false),context,ankiBoxViewSetUp)
    }

    override fun onBindViewHolder(holder: AnkiBoxItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AnkiBoxItemViewHolder (private val binding: AnkiHomeFragRvItemBinding,val context: Context,val ankiBoxViewSetUp: AnkiBoxChildrenFragViewSetUp) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: Any, ){
            val content =
            when(item){
                is File -> {
                    val binding = AnkiHomeFragRvItemFileBinding.inflate(LayoutInflater.from(context))
                    ankiBoxViewSetUp.setUpRVFileBinding(binding, item,ankiBoxViewSetUp.tab)

                    binding.root
                }
                is Card -> {
                    val binding = AnkiHomeFragRvItemCardBinding.inflate(LayoutInflater.from(context))
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