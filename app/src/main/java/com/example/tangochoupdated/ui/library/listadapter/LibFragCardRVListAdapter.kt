package com.example.tangochoupdated.ui.library.listadapter

import android.content.Context
import android.view.*
import android.view.View.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.GetCustomDrawables
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.library.LibraryAddClickListeners
import com.example.tangochoupdated.ui.library.LibraryViewModel


/**
 * Custom Data Class for this adapter
 */


class LibFragCardRVListAdapter(
    private val createCardViewModel: CreateCardViewModel,
    private val libraryViewModel: LibraryViewModel,
    private val context: Context) :
    ListAdapter<Card, LibFragCardRVListAdapter.LibFragCardViewHolder>(CardDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibFragCardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibFragCardViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibFragCardViewHolder, position: Int) {
        holder.bind(getItem(position),libraryViewModel,createCardViewModel)
    }

    class LibFragCardViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: Card,
                 libraryViewModel: LibraryViewModel ,
                 createFileViewModel: CreateCardViewModel){

//            親レイアウトのclick listener
            binding.contentBindingFrame.removeAllViews()

            val stringBinding = LibraryFragRvItemCardStringBinding.inflate(LayoutInflater.from(context))
            binding.contentBindingFrame.addView(stringBinding.root)
            LibraryAddClickListeners().fragLibCardRVAddCL(binding,context,item,libraryViewModel,createFileViewModel)



            val stringData = item.stringData
//                  stringData?.frontTitle
//                  stringData?.backTitle ?:"裏"
            stringBinding.txvFrontTitle.text = item.libOrder.toString()
            stringBinding.txvFrontText.text = stringData?.frontText ?:"null"
            stringBinding.txvBackTitle.text = item.id.toString()
            stringBinding.txvBackText.text = stringData?.backText

            binding.root.tag = LibRVState.Plane
            binding.btnAddNewCard.visibility = View.VISIBLE
            binding.linLaySwipeShow.visibility = GONE

            if(libraryViewModel.returnMultiSelectMode()== true) binding.btnSelect.visibility = View.VISIBLE

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