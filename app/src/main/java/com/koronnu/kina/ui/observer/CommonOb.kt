package com.koronnu.kina.ui.observer

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.makeToast
import com.koronnu.kina.customClasses.MakeToastFromVM
import com.koronnu.kina.ui.view_set_up.LibrarySetUpItems
import com.koronnu.kina.ui.viewmodel.LibraryBaseViewModel

class CommonOb {

    fun toastObserver(context: Context) = Observer<MakeToastFromVM>{
        if(it.show) makeToast(context,it.text)
    }

    fun makeAllUnSwipedObserver(recyclerView:RecyclerView) = Observer<Boolean>{
        if(it) LibrarySetUpItems().makeLibRVUnSwiped(recyclerView)
    }

}