package com.example.tangochoupdated.ui.observer

import android.content.Context
import android.widget.Toast

class CommonOb {
    fun observeToast(context: Context,text:String,show:Boolean){
        if(show) Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
    }

}