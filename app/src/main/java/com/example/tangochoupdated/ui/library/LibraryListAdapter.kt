package com.example.tangochoupdated

import android.content.Context
import android.view.*
import android.view.View.*
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.db.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibRVClickListener
import com.example.tangochoupdated.ui.library.LibraryViewModel


/**
 * Custom Data Class for this adapter
 */


class LibraryListAdapter(
    private val createFileViewModel: CreateFileViewModel,
    private val createCardViewModel: CreateCardViewModel,
    private val libraryViewModel: LibraryViewModel,
    private val context: Context) :
    ListAdapter<LibraryRV, LibraryListAdapter.LibraryViewHolder>(MyDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        return LibraryViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position),libraryViewModel,createCardViewModel,createFileViewModel)
    }

    class LibraryViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root){
        lateinit var fileBinding:LibraryFragRvItemFileBinding
        fun bind(item: LibraryRV,
                 libraryViewModel: LibraryViewModel ,
                 createCardViewModel: CreateCardViewModel,
                 createFileViewModel: CreateFileViewModel){
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
            binding.contentBindingFrame.removeAllViews()


            when (item.type){
              LibRVViewType.Folder, LibRVViewType.FlashCardCover ->{
                  fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
                  binding.contentBindingFrame.addView(fileBinding.root)
                  binding.contentBindingFrame.layoutParams.height = 200
                  binding.contentBindingFrame.requestLayout()
                  fileBinding.apply{

                      txvFileTitle.text = item.file?.title.toString()
                      imvFileType.setImageDrawable(when(item.type){
                          LibRVViewType.Folder -> AppCompatResources.getDrawable(context,R.drawable.icon_file)
                          LibRVViewType.FlashCardCover -> AppCompatResources.getDrawable(context,R.drawable.icon_library_plane)
                          else -> throw IllegalArgumentException()
                      })

                  }


              }
              LibRVViewType.StringCard ->{
                  val stringBinding = LibraryFragRvItemCardStringBinding.inflate(LayoutInflater.from(context))
                  binding.contentBindingFrame.addView(stringBinding.root)

                  val stringData = item.card?.stringData
//                  stringData?.frontTitle
//                  stringData?.backTitle ?:"裏"
                  stringBinding.txvFrontTitle.text = item.card?.libOrder.toString()
                  stringBinding.txvFrontText.text = stringData?.frontText ?:"null"
                  stringBinding.txvBackTitle.text = item.card?.id.toString()
                  stringBinding.txvBackText.text = stringData?.backText




              }
              else -> return
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
            clickableViews.apply {
                addAll(arrayOf(
                binding.baseContainer,
                binding.btnDelete,
                binding.btnSelect,
                binding.btnAddNewCard,
                ))
                onEach { it.setOnTouchListener(
                    LibRVClickListener(it,context,item,
                        createFileViewModel,
                        libraryViewModel,
                        createCardViewModel, binding)) }
            }
            }

        }


    }
private object MyDiffCallback : DiffUtil.ItemCallback<LibraryRV>() {
    override fun areItemsTheSame(oldItem: LibraryRV, newItem: LibraryRV): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LibraryRV, newItem: LibraryRV): Boolean {
        return oldItem.equals(newItem)
    }

}