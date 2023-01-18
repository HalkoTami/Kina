package com.koronnu.kina.util

import android.content.Context
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.actions.makeToast
import com.koronnu.kina.data.model.normalClasses.MakeToastFromVM
import com.koronnu.kina.util.view_set_up.LibrarySetUpItems

class CommonOb {

    fun toastObserver(context: Context) = Observer<MakeToastFromVM>{
        if(it.show) makeToast(context,it.text)
    }

}