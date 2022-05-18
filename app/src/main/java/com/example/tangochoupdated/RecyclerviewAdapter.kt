package com.example.tangochoupdated

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class CardListAdapter : androidx.recyclerview.widget.ListAdapter<Card, CardListAdapter.RoomViewHolder>(WordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        when (viewType){
            TYPE_FILE ->{
                val binding = ItemCoverFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    class WordsComparator : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.word == newItem.word
        }
    }

    override fun getItemViewType(position: Int): Int =
      when (getItem(position).cardStatus.ordinal) {
          is 1 ->
      }
    /// TODO:

}