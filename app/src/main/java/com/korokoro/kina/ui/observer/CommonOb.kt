package com.korokoro.kina.ui.observer

import android.content.Context
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.actions.makeToast
import com.korokoro.kina.customClasses.MakeToastFromVM
import com.korokoro.kina.ui.view_set_up.LibrarySetUpItems

class CommonOb {

    fun toastObserver(context: Context) = Observer<MakeToastFromVM>{
        if(it.show) makeToast(context,it.text)
    }

    fun makeAllUnSwipedObserver(recyclerView:RecyclerView) = Observer<Boolean>{
        if(it) LibrarySetUpItems().makeLibRVUnSwiped(recyclerView)
    }

}