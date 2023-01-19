package com.koronnu.kina.ui.tabAnki.ankiBox

import android.animation.ValueAnimator
import android.content.Context
import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.*
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.model.enumClasses.AnkiBoxFragments
import com.koronnu.kina.data.model.normalClasses.ParentFileAncestors
import com.koronnu.kina.data.source.local.entity.ActivityData
import com.koronnu.kina.data.source.local.entity.enumclass.ActivityStatus
import com.koronnu.kina.ui.tabLibrary.SearchDiffCallback
import com.koronnu.kina.util.view_set_up.AnkiBoxFragViewSetUp
import com.koronnu.kina.util.view_set_up.CommonViewSetUp
import com.koronnu.kina.util.view_set_up.GetCustomDrawables
import com.koronnu.kina.util.view_set_up.LibrarySetUpItems
import kotlin.math.abs


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
        fun getPercentage(per:Double):Int{
            return per.times(100).toInt()
        }
        fun setUpRVFileBinding(binding:ListItemAnkiBoxRvFileBinding,
                               file: File, tab: AnkiBoxFragments,
                               ankiBoxVM: AnkiBoxViewModel,
                               context:Context,
                               lifecycleOwner: LifecycleOwner
        ){
            fun getAnimation(list:List<Card>): ValueAnimator {
                val remPer = list.filter { it.remembered }.size.toDouble()/list.size
                val rememberedProgressBar = binding.rememberedProgressBarBinding.progressbarRemembered
                val progressBefore =rememberedProgressBar.progress
                val imvEndIcon = binding.rememberedProgressBarBinding.imvRememberedEndIcon
                val a = ValueAnimator.ofFloat(progressBefore.toFloat(),getPercentage(remPer).toFloat())
                a.addUpdateListener {
                    val value = it.animatedValue as Float
                    rememberedProgressBar.progress = value.toInt()
                    val translation = rememberedProgressBar.width*value/100-imvEndIcon.width/2
                    imvEndIcon.translationX = if(translation<0) 0f else translation
                }
                a.duration = abs(progressBefore-getPercentage(remPer)).times(30).toLong()
                return a
            }
            val getDraw = GetCustomDrawables(context)
            val descendantsCardsObserver = Observer<List<Card>>{ list->
                binding.apply {
                    arrayOf(txvAnkiBoxCardAmount,imvContainingCardsIcon
                    ).onEach { it.visibility = if(list.isEmpty()) View.INVISIBLE else View.VISIBLE }
                    binding.txvAnkiBoxCardAmount.text = list.size.toString()
                }
                getAnimation(list).start()

            }
            val descendantsFoldersObserver = Observer<List<File>>{ list->
                binding.apply {
                    arrayOf(txvAnkiBoxFolderAmount,imvContainingFoldersIcon
                    ).onEach { it.visibility = if(list.isEmpty()) View.INVISIBLE else View.VISIBLE }
                    binding.txvAnkiBoxFolderAmount.text = list.size.toString()
                }
            }
            val descendantsFlashCardsObserver = Observer<List<File>>{ list->
                binding.apply {
                    arrayOf(txvAnkiBoxFlashCardAmount,imvContainingFlashcardsIcon
                    ).onEach { it.visibility = if(list.isEmpty()) View.INVISIBLE else View.VISIBLE }
                    binding.txvAnkiBoxFlashCardAmount.text = list.size.toString()
                }
            }
            val ancestorsObserver = Observer<List<File>>{ ancestors ->
                val a = ParentFileAncestors(
                    gGrandPFile  = if(ancestors.size>=3) ancestors[2] else null,
                    gParentFile = if(ancestors.size>=2) ancestors[1] else null,
                    ParentFile =  null
                )
                binding.ancestorsBinding.apply {
                    CommonViewSetUp().apply {
                        setUpEachAncestor(lineLayGGFile,txvGGrandParentFileTitle,imvGGrandParentFile,a.gGrandPFile)
                        setUpEachAncestor(lineLayGPFile,txvGrandParentFileTitle,imvGrandParentFile,a.gParentFile)
                        setUpEachAncestor(lineLayParentFile,txvParentFileTitle,imvParentFile,a.ParentFile)
                        binding.frameAnkiBoxRvAncestors.visibility =
                            if(ancestors.size == 1) View.GONE
                            else View.VISIBLE
                    }

                }
            }

            binding.apply {
                ankiBoxVM.ankiBoxFileIds.observe(lifecycleOwner){
                    imvChbIsInAnkiBox.isSelected = (it.contains(file.fileId))
                }
                imvFileType.setImageDrawable(
                    getDraw.getFileIconByFile(file)
                )
                txvFileTitle.text = file.title

                arrayOf(root,imvChbIsInAnkiBox).onEach { it.setOnClickListener(AnkiBoxFileRVCL(
                    file,
                    ankiBoxVM = ankiBoxVM,
                    binding = binding,
                    tab = tab)) }
                ankiBoxVM.ankiBoxFileAncestorsFromDB(file.fileId)       .observe(lifecycleOwner,ancestorsObserver)
                ankiBoxVM.getAnkiBoxRVCards(file.fileId)                .observe(lifecycleOwner,descendantsCardsObserver)
                ankiBoxVM.getAnkiBoxRVDescendantsFolders(file.fileId)   .observe(lifecycleOwner,descendantsFoldersObserver)
                ankiBoxVM.getAnkiBoxRVDescendantsFlashCards(file.fileId)   .observe(lifecycleOwner,descendantsFlashCardsObserver)

            }
        }
        fun bind(item: Any, ){

            binding.flAnkiBoxRvContent.removeAllViews()
            val content =
            when(item){
                is File -> {
                    val fileBinding = ListItemAnkiBoxRvFileBinding.inflate(LayoutInflater.from(context))
                    setUpRVFileBinding(fileBinding, item,tab!!, ankiBoxVM = ankiBoxFragViewModel ,context,lifecycleOwner)
                    binding.flAnkiBoxRvContent.layoutParams.height = context.resources.getDimensionPixelSize(
                        R.dimen.anki_box_rv_item_height)
                    binding.flAnkiBoxRvContent.requestLayout()
                    fileBinding.root
                }
                is Card -> {
                    val cardBinding = ListItemAnkiBoxRvCardBinding.inflate(LayoutInflater.from(context))
                    setUpRVCard(cardBinding,item,lifecycleOwner,ankiBoxFragViewModel)
                    binding.flAnkiBoxRvContent.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    binding.flAnkiBoxRvContent.requestLayout()
                    changeViewVisibility(cardBinding.imvChbIsInAnkiBox,(tab==null).not())
                    cardBinding.root
                }
                else -> return
            }
            binding.flAnkiBoxRvContent.addView(content)




        }
        fun setUpRVCard(cardBinding: ListItemAnkiBoxRvCardBinding, card: Card, lifecycleOwner: LifecycleOwner, ankiBoxVM: AnkiBoxViewModel){
            val resources = cardBinding.root.context.resources
            LibrarySetUpItems().setUpRVStringCardBinding(cardBinding.bindingListItemCardString,card.stringData)

            fun setOnCL(){
                arrayOf(cardBinding.imvChbIsInAnkiBox,).onEach { it.setOnClickListener(
                    AnkiBoxRVStringCardCL(card,cardBinding,ankiBoxVM)) }
            }
            fun getStringByRemembered(remembered:Boolean):String{
                return resources.getString(if(remembered) R.string.remembered else R.string.not_remembered)
            }
            fun getStringByLastLooked(lastLooked: ActivityData?,):String{

                return resources.getString(R.string.lastLookedDate,lastLooked?.dateTime ?:resources.getString(R.string.no_data))
            }
            setOnCL()
            ankiBoxVM.ankiBoxItems.observe(lifecycleOwner){
                cardBinding.imvChbIsInAnkiBox.isSelected = it.contains(card)
            }
            ankiBoxVM.getCardActivityFromDB(card.id).observe(lifecycleOwner){
                val lastLooked =it.findLast { it.activityStatus == ActivityStatus.CARD_OPENED }
                cardBinding.txvAnkiRvLastFlipped.text = getStringByLastLooked(lastLooked)
            }
            cardBinding.apply {
                imvAnkiRvCardRemembered.isSelected = card.remembered
                txvAnkiRvRememberedStatus.text = getStringByRemembered(card.remembered)
            }



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