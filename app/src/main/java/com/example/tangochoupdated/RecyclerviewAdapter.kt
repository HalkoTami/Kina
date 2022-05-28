package com.example.tangochoupdated

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.ItemCoverCardMarkerBinding
import com.example.tangochoupdated.databinding.ItemCoverCardQuizBinding
import com.example.tangochoupdated.databinding.ItemCoverCardStringBinding
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.CardStatusConverter

class CardListAdapter : androidx.recyclerview.widget.ListAdapter<Map<File,Card>, CardListAdapter.CardViewHolder>(CardsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType){
             CardStatus.STRING.ordinal->{
                val binding = ItemCoverCardStringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ContentAdapter.FileViewHolder(binding)
            }
            ContentAdapter.TYPE_TANGOCHO ->
                TangochoViewHolder(ItemCoverTangochoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ContentAdapter.TYPE_CARD_STRING ->
                StringCardViewHolder(ItemCoverCardStringBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ContentAdapter.TYPE_CARD_MARKER ->
                MarkerCardViewHolder(ItemCoverCardMarkerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ContentAdapter.TYPE_CARD_QUIZ ->
                QuizCardViewHolder(ItemCoverCardQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException()

        }
        return RoomViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        when (holder.itemViewType) {
            CardStatus.STRING.ordinal ->{ onBindStringCard(holder, getItem(position))
            }
            TYPE_M -> onBindMessage(holder, rows[position] as ContentAdapter.MessageRow)
            TYPE_COLOUR -> onBindColour(holder, rows[position] as ContentAdapter.ColourRow)
            else -> throw IllegalArgumentException()
        }
    }


    inner class StringCardViewHolder(binding: ItemCoverCardStringBinding) : RecyclerView.ViewHolder(binding.root){
        var title:TextView= binding.txvFrontText
    }

    inner class QuizCardViewHolder(binding: ItemCoverCardQuizBinding) : RecyclerView.ViewHolder(binding.root)

    inner class MarkerCardViewHolder(binding: ItemCoverCardMarkerBinding) : RecyclerView.ViewHolder(binding.root)

    class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            wordItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): WordViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return WordViewHolder(view)
            }
        }
    }

    private fun onBindStringCard(holder: RecyclerView.ViewHolder, card: Card) {
        val fileRow = holder as ContentAdapter.FileViewHolder
        fileRow.title.text=row.title



    }

    class CardsComparator : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.word == newItem.word
        }
    }

    override fun getItemViewType(position: Int): Int =
      getItem(position).cardStatus.ordinal
    /// TODO:

}
