package com.korokoro.kina.ui.view_set_up

import android.content.Context
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.databinding.*
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.dataclass.StringData
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.animation.Animation
import com.korokoro.kina.ui.listadapter.LibFragPlaneRVListAdapter
import com.korokoro.kina.ui.listadapter.LibFragSearchRVListAdapter
import com.korokoro.kina.ui.customClasses.LibRVState


class LibrarySetUpItems{



    fun setUpLibFragWithMultiModeBase(binding: LibraryChildFragWithMulModeBaseBinding,
                                      topBarView:View,
                                      searchRVListAdapter: LibFragSearchRVListAdapter,
                                      planeRVListAdapter: LibFragPlaneRVListAdapter,
                                      context: Context){
        fun setUpPlaneLibRV(){
            val mainRV = binding.vocabCardRV
            mainRV.adapter = planeRVListAdapter
            mainRV.layoutManager = LinearLayoutManager(context)
            mainRV.isNestedScrollingEnabled = true
        }
        fun setUpSearchRV(){
            val searchRV =  binding.searchRvBinding.recyclerView
            searchRV.adapter = searchRVListAdapter
            searchRV.layoutManager = LinearLayoutManager(context)
            searchRV.isNestedScrollingEnabled = true
        }
        binding.frameLayTopBar.addView(topBarView)
        setUpSearchRV()
        setUpPlaneLibRV()
    }


    fun changeLibRVSelectBtnVisibility(rv: RecyclerView, visible: Boolean){
        rv.children.iterator().forEach { view ->
            view.findViewById<ImageView>(R.id.btn_select).apply {
                visibility =if(visible) View.VISIBLE else View.GONE
            }
        }
    }
    fun changeLibRVAllSelectedState(rv: RecyclerView, selected:Boolean){
        rv.children.iterator().forEach { view ->
            view.findViewById<ImageView>(R.id.btn_select).apply {
                isSelected = selected
            }
        }
    }
    fun changeStringBtnVisibility(rv: RecyclerView, visible:Boolean){
        rv.children.iterator().forEach { view ->
            arrayOf(
                view.findViewById<FrameLayout>(R.id.btn_edt_front),
                view.findViewById(R.id.btn_edt_back),
                view.findViewById<ImageView>(R.id.btn_add_new_card))
                .onEach {
                    it.visibility = if(visible)View.VISIBLE else View.GONE
                }
        }
    }

    fun makeLibRVUnSwiped(rv: RecyclerView){
        rv.children.iterator().forEach { view ->
            val parent = view.findViewById<ConstraintLayout>(R.id.lib_rv_base_container)
            if(parent.tag == LibRVState.LeftSwiped){
                Animation().animateLibRVLeftSwipeLay(
                    view.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show),false)
            }
            view.findViewById<ImageView>(R.id.btn_select).visibility = View.GONE
            parent.tag = LibRVState.Plane
        }
    }
    fun setUpRVFileBinding(fileBinding: LibraryFragRvItemFileBinding,
                           file: File,
                           context:Context){
        fileBinding.apply {
            txvFileTitle.text = file.title.toString()
            imvFileType.setImageDrawable(when(file.fileStatus){
                FileStatus.FOLDER -> GetCustomDrawables(context).getFolderIconByCol(file.colorStatus )
                FileStatus.FLASHCARD_COVER -> GetCustomDrawables(context).getFlashCardIconByCol(file.colorStatus )
                else -> return
            })
        }
    }
    fun returnFileBindingTextViews(fileBinding: LibraryFragRvItemFileBinding, ):Array<TextView>{
        return  arrayOf(
            fileBinding.txvFileTitle
        )
    }

    fun setUpRVStringCardBinding(
        stringBinding: LibraryFragRvItemCardStringBinding,
        stringData: StringData?
    ){
        stringBinding.apply {
            stringBinding.txvFrontTitle.text = stringData?.frontTitle ?:"表"
            stringBinding.txvFrontText.text = stringData?.frontText ?:""
            stringBinding.txvBackTitle.text = stringData?.backTitle ?:"裏"
            stringBinding.txvBackText.text = stringData?.backText ?:""
        }

    }
    fun returnStringCardTextViews(
        stringBinding: LibraryFragRvItemCardStringBinding,
    ):Array<TextView>{

        return arrayOf(
            stringBinding. txvFrontText,
            stringBinding. txvFrontTitle,
            stringBinding. txvBackTitle,
            stringBinding. txvBackText
        )

    }




    fun setColorByMatchedSearch(view: TextView, fulltext: String, subtext: String, color: Int) {
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