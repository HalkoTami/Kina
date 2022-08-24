package com.example.tangochoupdated.ui.view_set_up

import android.content.Context
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.LibraryFragRvItemBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragRvItemCardStringBinding
import com.example.tangochoupdated.databinding.LibraryFragRvItemFileBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.dataclass.StringData
import com.example.tangochoupdated.db.enumclass.CardStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.StringCardViewModel
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel


class LibrarySetUpItems(val libVM: LibraryViewModel){
    private val addL = LibraryAddListeners(libVM)

    fun setUpRVFileBinding(fileBinding: LibraryFragRvItemFileBinding,
                           file: File,
                           context:Context){
        fileBinding.apply {
            txvFileTitle.text = file.title.toString()
            imvFileType.setImageDrawable(when(file.fileStatus){
                FileStatus.FOLDER -> GetCustomDrawables(context).getFolderIconByCol(file.colorStatus )
                FileStatus.TANGO_CHO_COVER -> GetCustomDrawables(context).getFlashCardIconByCol(file.colorStatus )
                else -> return
            })
        }
    }
    fun setUpRVStringCardBinding(
        stringBinding: LibraryFragRvItemCardStringBinding,
        stringData: StringData?,
        item: Card,
        createCardViewModel: CreateCardViewModel,
        stringCardViewModel: StringCardViewModel,
    ){
        stringBinding.apply {
            stringBinding.txvFrontTitle.text = stringData?.frontTitle.toString()
            stringBinding.txvFrontText.text = stringData?.frontText.toString()
            stringBinding.txvBackTitle.text = stringData?.backTitle.toString()
            stringBinding.txvBackText.text = stringData?.backText.toString()
        }
        addL.cardRVStringAddCL(
            binding = stringBinding,
            item = item,
            createCardViewModel = createCardViewModel,
            stringCardViewModel = stringCardViewModel
        )

    }
    fun setUpRVBaseFile(
        rvItemBaseBinding: LibraryFragRvItemBaseBinding,
        item: File,
        context: Context,
    ):Array<TextView>{
        val binding = LibraryFragRvItemFileBinding.inflate(LayoutInflater.from(context))
        setUpRVFileBinding(binding,item,context)
        rvItemBaseBinding. contentBindingFrame.apply{
            layoutParams.height = 200
            requestLayout()
            addView(binding.root)
        }
        return arrayOf(binding.txvFileTitle)


    }
    fun setUpRVBaseSelectFileMoveTo(
        rvItemBaseBinding: LibraryFragRvItemBaseBinding,
        item: File,
        context: Context,
        createFileViewModel: CreateFileViewModel,
    ){
        addL.fileRVAddCL(
            rvItemBaseBinding,
            context,
            item,
            createFileViewModel,
            true)
        setUpRVBaseFile(rvItemBaseBinding, item, context)

        rvItemBaseBinding.btnSelect.apply {
            setImageDrawable(AppCompatResources.getDrawable(context,
                when(item.fileStatus){
                    FileStatus.FOLDER -> R.drawable.icon_move_to_folder
                    FileStatus.TANGO_CHO_COVER -> R.drawable.icon_move_to_flashcard_cover
                    else -> return
                }))
            visibility = View.VISIBLE
        }

    }
    fun setUpRVBasePlane(
        rvItemBaseBinding: LibraryFragRvItemBaseBinding,
        item: Any,
        context: Context,
        createCardViewModel: CreateCardViewModel,
        createFileViewModel: CreateFileViewModel,
        stringCardViewModel: StringCardViewModel,
    ){
        when(item){
            is File -> {
                setUpRVBaseFile(rvItemBaseBinding, item, context)
                addL.fileRVAddCL(
                    rvItemBaseBinding,
                    context,
                    item,
                    createFileViewModel,
                    false)
                rvItemBaseBinding.btnAddNewCard.visibility = View.GONE
            }
            is Card -> {
                setUpRVBaseCard(rvItemBaseBinding, item, context, createCardViewModel,  stringCardViewModel)
                addL.cardRVAddCL( rvItemBaseBinding,
                    context,
                    item,
                    createCardViewModel)
                rvItemBaseBinding.btnAddNewCard.visibility = View.VISIBLE
            }
        }
        rvItemBaseBinding.apply {
            btnSelect.apply {
                setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.select_rv_item))
            }

            root.tag = LibRVState.Plane
            linLaySwipeShow.visibility = View.GONE
            if(libVM.returnMultiSelectMode()== true) btnSelect.visibility = View.VISIBLE
        }


    }

    fun setUpRVBaseCard(
        rvItemBaseBinding: LibraryFragRvItemBaseBinding,
        item: Card,
        context: Context,
        createCardViewModel: CreateCardViewModel,
        stringCardViewModel: StringCardViewModel,
    ):ArrayList<TextView>{
        val checkMatchTxv = arrayListOf<TextView>()
        val cardContent = when(item.cardStatus){
            CardStatus.STRING -> {
                val binding = LibraryFragRvItemCardStringBinding.inflate(LayoutInflater.from(context))
                binding.apply{
                    checkMatchTxv.addAll(arrayOf(txvBackTitle,txvFrontTitle,txvBackText,txvBackText))
                }
                setUpRVStringCardBinding(binding,item.stringData,item,createCardViewModel,stringCardViewModel)
                binding.txvFrontTitle.text = "order "+ item.libOrder.toString()
                binding.txvBackTitle.text ="id " + item.id.toString()
                binding.root
            }
            CardStatus.CHOICE -> TODO()
            CardStatus.MARKER -> TODO()
        }
        rvItemBaseBinding.contentBindingFrame.addView(cardContent)


        return checkMatchTxv

    }



    fun setUpRVSearchBase(rvItemBaseBinding: LibraryFragRvItemBaseBinding,
                          item:Any,
                          context: Context,
                          createCardViewModel: CreateCardViewModel,
                          stringCardViewModel: StringCardViewModel,
                          searchViewModel: SearchViewModel,
                          lifecycleOwner: LifecycleOwner){

        val checkMatchTxv = mutableListOf<TextView>()
        when(item){
            is File ->{
                checkMatchTxv.addAll(setUpRVBaseFile(rvItemBaseBinding, item, context))
            }
            is Card -> {
                checkMatchTxv.addAll(setUpRVBaseCard(
                    rvItemBaseBinding,
                    item,
                    context,
                    createCardViewModel,
                    stringCardViewModel
                ))
            }
            else -> return
        }
        addL.searchRVAddCL(
            rvItemBaseBinding,
            item,
            createCardViewModel,
            libVM)
        rvItemBaseBinding.btnAddNewCard.visibility = View.GONE
        rvItemBaseBinding.linLaySwipeShow.visibility = View.GONE
        searchViewModel.searchingText.observe(lifecycleOwner){ search ->
            checkMatchTxv.onEach { txv->
                setColor(txv,txv.text.toString(),search,ContextCompat.getColor(context,R.color.red))

            }
        }
    }

    private fun setColor(view: TextView, fulltext: String, subtext: String, color: Int) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE)
        val str = view.text as Spannable
        val i = fulltext.indexOf(subtext)
        if(i<0) return else
        str.setSpan(
            ForegroundColorSpan(color),
            i,
            i + subtext.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }





}