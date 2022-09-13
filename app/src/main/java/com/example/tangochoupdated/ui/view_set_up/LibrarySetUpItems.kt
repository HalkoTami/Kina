package com.example.tangochoupdated.ui.view_set_up

import android.content.Context
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.dataclass.StringData
import com.example.tangochoupdated.db.enumclass.CardStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.listadapter.LibFragPlaneRVListAdapter
import com.example.tangochoupdated.ui.listadapter.LibFragSearchRVListAdapter
import com.example.tangochoupdated.ui.viewmodel.customClasses.LibRVState
import com.example.tangochoupdated.ui.viewmodel.*


class LibrarySetUpItems(){

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
                view.findViewById<ImageView>(R.id.btn_edt_front),
                view.findViewById<ImageView>(R.id.btn_edt_back),
                view.findViewById(R.id.btn_add_new_card))
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
                FileStatus.TANGO_CHO_COVER -> GetCustomDrawables(context).getFlashCardIconByCol(file.colorStatus )
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