package com.example.tangochoupdated

package com.example.tangocho

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


private object DiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

}
class RvData(val title:String = "hello")

class UserListAdapter() : ListAdapter<User, UserListAdapter.UserViewHolder>(DiffCallback) {


    class UserViewHolder(private val binding: ItemCoverFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User, viewLifecycleOwner: LifecycleOwner, viewModel: MainViewModel) {
            binding.run {
                user = item
                this.viewModel = viewModel

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UserListAdapter.UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserViewHolder(ItemCoverFileBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: UserListAdapter.UserViewHolder, position: Int) {
        holder.bind(getItem(position), viewLifecycleOwner, viewModel)
    }


}



class ContentAdapter(private val rows: Library) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    class FileViewHolder(binding: ItemCoverFileBinding) : RecyclerView.ViewHolder(binding.root){
        var title:TextView= binding.txvfiletitle

    }


    inner class TangochoViewHolder(binding: ItemCoverTangochoBinding) : RecyclerView.ViewHolder(binding.root){
        val title:

    }

    inner class StringCardViewHolder(binding: ItemCoverCardStringBinding) : RecyclerView.ViewHolder(binding.root)

    inner class QuizCardViewHolder(binding: ItemCoverCardQuizBinding) : RecyclerView.ViewHolder(binding.root)

    inner class MarkerCardViewHolder(binding: ItemCoverCardMarkerBinding) : RecyclerView.ViewHolder(binding.root)


    companion object {
        private const val TYPE_FILE = 0
        private const val TYPE_TANGOCHO = 1
        private const val TYPE_CARD_STRING = 2
        private const val TYPE_CARD_QUIZ = 3
        private const val TYPE_CARD_MARKER = 4
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        when (viewType){
            TYPE_FILE ->{
                val binding = ItemCoverFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return FileViewHolder(binding)
            }
            TYPE_TANGOCHO ->
                TangochoViewHolder(ItemCoverTangochoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_CARD_STRING ->
                StringCardViewHolder(ItemCoverCardStringBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_CARD_MARKER ->
                MarkerCardViewHolder(ItemCoverCardMarkerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_CARD_QUIZ ->
                QuizCardViewHolder(ItemCoverCardQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException()

        }


    }
    override fun getItemViewType(position: Int): Int =
        when (rows) {
            is IRow.FileRow -> TYPE_FILE
            is IRow.TangochoRow -> TYPE_TANGOCHO
            is IRow.StringCardRow -> TYPE_CARD_STRING
            is IRow.QuizCardRow -> TYPE_CARD_QUIZ
            is IRow.MarkerCardRow -> TYPE_CARD_MARKER
            else -> throw IllegalArgumentException()
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_FILE ->{ onBindFile(holder, rows[position] as IRow.FileRow)
            }
            TYPE_M -> onBindMessage(holder, rows[position] as ContentAdapter.MessageRow)
            TYPE_COLOUR -> onBindColour(holder, rows[position] as ContentAdapter.ColourRow)
            else -> throw IllegalArgumentException()
        }
    }

    interface FileRow {

    }

    private fun onBindFile(holder: RecyclerView.ViewHolder, row: IRow.FileRow) {
        val fileRow = holder as FileViewHolder
        fileRow.title.text=row.title



    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}

class CoverRow(
    val type: Int,
    val filecoverData:File?,
    val tangochoCoverData:Card?

)
class FileRow(val file:File) : CoverRow
class TangochoRow(val tangocho:Tangocho) : CoverRow
class StringCardRow(val StringCardCoverData:StringCard) : CoverRow
class QuizCardRow(val QuizCardCoverData:QuizCard): CoverRow
class MarkerCardRow(val MarkerCardCoverData:MarkerCard): CoverRow

