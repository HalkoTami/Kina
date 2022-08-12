//package com.example.tangochoupdated
//
//import android.content.Context
//import android.view.*
//import android.view.View.*
//import androidx.appcompat.content.res.AppCompatResources
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.example.tangochoupdated.databinding.*
//import com.example.tangochoupdated.db.enumclass.ColorStatus
//import com.example.tangochoupdated.db.enumclass.FileStatus
//import com.example.tangochoupdated.db.enumclass.LibRVState
//import com.example.tangochoupdated.db.rvclasses.LibRVViewType
//import com.example.tangochoupdated.db.rvclasses.LibraryRV
//import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
//import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
//import com.example.tangochoupdated.ui.library.LibraryViewModel
//import com.example.tangochoupdated.ui.library.clicklistener.LibRVClickListener
//import com.example.tangochoupdated.ui.library.listadapter.LibraryListAdapter
//
//
///**
// * Custom Data Class for this adapter
// */
//
//
//class LibraryListAdapter(
//    private val createFileViewModel: CreateFileViewModel,
//    private val createCardViewModel: CreateCardViewModel,
//    private val libraryViewModel: LibraryViewModel,
//    private val context: Context) :
//    ListAdapter<LibraryRV, LibraryListAdapter.LibraryViewHolder>(MyDiffCallback) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder{
//        val layoutInflater = LayoutInflater.from(parent.context)
//        return LibraryViewHolder(LibraryFragRvItemBaseBinding.inflate(layoutInflater, parent, false),context)
//    }
//
//    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
//        holder.bind(getItem(position),libraryViewModel,createCardViewModel,createFileViewModel)
//    }
//
//    class LibraryViewHolder (private val binding: LibraryFragRvItemBaseBinding,val context: Context) :
//        RecyclerView.ViewHolder(binding.root){
//        lateinit var fileBinding:LibraryFragRvItemFileBinding
//        fun bind(item: LibraryRV,
//                 libraryViewModel: LibraryViewModel ,
//                 createCardViewModel: CreateCardViewModel,
//                 createFileViewModel: CreateFileViewModel){
//            if(item.card!=null && item.card.libOrder!= adapterPosition) libraryViewModel.upDateCardPosition(adapterPosition,item.card)
//
////            親レイアウトのclick listener
//            val clickableViews = mutableListOf<View>()
//            fun addBaseBindingToClickableViews(){
//                binding.apply {
//                    clickableViews.addAll(arrayOf(
//                        baseContainer,
//                        btnDelete,
//                        btnSelect,
//                        btnAddNewCard,
//                        btnEditWhole
//                    ))
//                }
//            }
//            fun setTouchListenerToClickableViews(){
//                clickableViews.onEach { it.setOnTouchListener(
//                    LibRVClickListener(it,context,item,
//                        createFileViewModel,
//                        libraryViewModel,
//                        createCardViewModel, binding)
//                ) }
//            }
//
//
//            binding.contentBindingFrame.removeAllViews()
//
//
//            when (item.type){
//              LibRVViewType.Folder, LibRVViewType.FlashCardCover ->{
//                  fileBinding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
//                  binding.contentBindingFrame.addView(fileBinding.root)
//                  binding.contentBindingFrame.layoutParams.height = 200
//                  binding.contentBindingFrame.requestLayout()
//                  fileBinding.apply{
//
//                      txvFileTitle.text = item.file?.title.toString()
//                      imvFileType.setImageDrawable(when(item.type){
//                          LibRVViewType.Folder -> GetCustomDrawables().getFileIconByCol(item.file?.colorStatus ?:ColorStatus.GRAY,context)
//                          LibRVViewType.FlashCardCover -> GetCustomDrawables().getFlashCardIconByCol(item.file?.colorStatus ?:ColorStatus.GRAY,context)
//                          else -> throw IllegalArgumentException()
//                      })
//
//                  }
//
//
//              }
//              LibRVViewType.StringCard ->{
//                  val stringBinding = LibraryFragRvItemCardStringBinding.inflate(LayoutInflater.from(context))
//                  binding.contentBindingFrame.addView(stringBinding.root)
//
//                  val stringData = item.card?.stringData
////                  stringData?.frontTitle
////                  stringData?.backTitle ?:"裏"
//                  stringBinding.txvFrontTitle.text = item.card?.libOrder.toString()
//                  stringBinding.txvFrontText.text = stringData?.frontText ?:"null"
//                  stringBinding.txvBackTitle.text = item.card?.id.toString()
//                  stringBinding.txvBackText.text = stringData?.backText
//
//
//
//
//              }
//              else -> return
//          }
//
//
//            binding.btnAddNewCard.visibility = when(item.type){
//                LibRVViewType.MarkerCard,LibRVViewType.ChoiceCard,LibRVViewType.StringCard -> VISIBLE
//                else -> GONE
//            }
//            binding.baseContainer.tag = LibRVState.Plane
//            if(libraryViewModel.returnRVMode()==LibRVState.SelectFileMoveTo){
//                binding.btnSelect.visibility = VISIBLE
//                val a = AppCompatResources.getDrawable(context,if(
//                    libraryViewModel.returnParentFile()?.fileStatus == FileStatus.TANGO_CHO_COVER
//                ) R.drawable.icon_move_to_flashcard_cover
//                else R.drawable.icon_move_to_folder
//                )
//                binding.btnSelect.setImageDrawable(a)
//                binding.root.tag = LibRVState.SelectFileMoveTo
//                binding.btnSelect.alpha = 0.5f
//            }else binding.btnSelect.visibility = GONE
//            binding.linLaySwipeShow.visibility = GONE
//
//
//
//
//
////            clickableViews
//            addBaseBindingToClickableViews()
//            setTouchListenerToClickableViews()
//            }
//
//        }
//
//
//
//    }
//private object MyDiffCallback : DiffUtil.ItemCallback<LibraryRV>() {
//    override fun areItemsTheSame(oldItem: LibraryRV, newItem: LibraryRV): Boolean {
//        return oldItem.id == newItem.id
//    }
//
//    override fun areContentsTheSame(oldItem: LibraryRV, newItem: LibraryRV): Boolean {
//        return oldItem.equals(newItem)
//    }
//
//}