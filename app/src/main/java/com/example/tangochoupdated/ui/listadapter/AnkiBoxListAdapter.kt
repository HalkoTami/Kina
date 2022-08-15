package com.example.tangochoupdated.ui.listadapter

import android.content.Context
import android.view.*
import android.view.View.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel


/**
 * Custom Data Class for this adapter
 */


class AnkiBoxListAdapter(
    private val context: Context) :
    ListAdapter<Any, AnkiBoxListAdapter.AnkiBoxItemViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnkiBoxItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AnkiBoxItemViewHolder(AnkiHomeFragRvItemBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: AnkiBoxItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AnkiBoxItemViewHolder (private val binding: AnkiHomeFragRvItemBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: Any, ){
            binding.imvFileType.setImageDrawable(
                AppCompatResources.getDrawable(context,
                when(item.fileStatus){
                    FileStatus.TANGO_CHO_COVER -> R.drawable.icon_flashcard
                    FileStatus.FOLDER -> R.drawable.icon_file
                    else -> return
                })
            )
            binding.txvFileTitle.text = item.title


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