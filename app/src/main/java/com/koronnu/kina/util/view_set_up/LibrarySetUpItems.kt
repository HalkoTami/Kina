package com.koronnu.kina.util.view_set_up

import android.content.Context
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.databinding.*
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.source.local.entity.StringData
import com.koronnu.kina.data.source.local.entity.enumclass.FileStatus
import com.koronnu.kina.util.Animation
import com.koronnu.kina.ui.tabLibrary.LibFragPlaneRVListAdapter
import com.koronnu.kina.ui.tabLibrary.LibFragSearchRVListAdapter
import com.koronnu.kina.data.model.enumClasses.LibRVState


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
            val searchRV =  binding.searchRv
            searchRV.adapter = searchRVListAdapter
            searchRV.layoutManager = LinearLayoutManager(context)
            searchRV.isNestedScrollingEnabled = true
        }
        binding.flTpbLibrary.addView(topBarView)
        setUpSearchRV()
        setUpPlaneLibRV()
    }


    fun changeLibRVSelectBtnVisibility(rv: RecyclerView, visible: Boolean){
        rv.children.iterator().forEach { view ->

            view.findViewById<ImageView>(R.id.btn_select).apply {
                visibility =if(visible) View.VISIBLE else View.GONE
            }
            val parent = view.findViewById<ConstraintLayout>(R.id.lib_rv_base_container)
            parent.tag = if(visible) LibRVState.Selectable else LibRVState.Plane
        }
    }
    fun changeLibRVAllSelectedState(rv: RecyclerView, selected:Boolean){
        rv.children.iterator().forEach { view ->
            view.findViewById<ImageView>(R.id.btn_select).apply {
                isSelected = selected
            }
        }
    }
    fun changeStringBtnVisibility(rv: RecyclerView, multiMode:Boolean){
        rv.children.iterator().forEach { view ->
            view.findViewById<ImageView>(R.id.btn_add_new_card).visibility =
                if(multiMode) View.INVISIBLE else View.VISIBLE
        }
    }

    fun makeLibRVUnSwiped(rv: RecyclerView){
        rv.children.iterator().forEach { view ->
            val parent = view.findViewById<ConstraintLayout>(R.id.lib_rv_base_container)
            if(parent.tag == LibRVState.LeftSwiped){
                Animation().animateLibRVLeftSwipeLay(
                    view.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show),false,){}
            }
            view.findViewById<ImageView>(R.id.btn_select).visibility = View.GONE
            parent.tag = LibRVState.Plane
        }
    }

    fun returnFileBindingTextViews(fileBinding: LibraryFragRvItemFileBinding, ):Array<TextView>{
        return  arrayOf(
            fileBinding.txvFileTitle
        )
    }

    fun setUpRVStringCardBinding(
        stringBinding: ListItemLibraryRvCardStringBinding,
        stringData: StringData?
    ){
        val resources = stringBinding.root.resources
        stringBinding.apply {
            stringBinding.txvFrontTitle.text = stringData?.frontTitle ?:resources.getString(R.string.edtFrontTitle_default)
            stringBinding.txvFrontText.text = stringData?.frontText ?:String()
            stringBinding.txvBackTitle.text = stringData?.backTitle ?:resources.getString(R.string.edtBackTitle_default)
            stringBinding.txvBackText.text = stringData?.backText ?:String()
        }

    }
    fun returnStringCardTextViews(
        stringBinding: ListItemLibraryRvCardStringBinding,
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