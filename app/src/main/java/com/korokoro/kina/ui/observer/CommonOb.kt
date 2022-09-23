package com.korokoro.kina.ui.observer

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.actions.changeViewVisibility
import com.korokoro.kina.actions.makeToast
import com.korokoro.kina.ui.customClasses.MakeToastFromVM
import com.korokoro.kina.ui.view_set_up.LibrarySetUpItems
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel

class CommonOb {

    fun toastObserver(context: Context) = Observer<MakeToastFromVM>{
        if(it.show) makeToast(context,it.text)
    }

    fun rvCoverObserver(rvCover:View) = Observer<LibraryBaseViewModel.RvCover>{
        changeViewVisibility(rvCover,it.visible)
    }
    fun leftSwipedItemExistsObserver(libraryBaseViewModel: LibraryBaseViewModel) = Observer<Boolean>{
        libraryBaseViewModel.setRVCover(LibraryBaseViewModel.RvCover(it.not()))
    }
    fun makeAllUnSwipedObserver(recyclerView:RecyclerView) = Observer<Boolean>{
        if(it) LibrarySetUpItems().makeLibRVUnSwiped(recyclerView)
    }

}