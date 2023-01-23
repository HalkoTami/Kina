package com.koronnu.kina.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class ObserveOnce<T>(val liveData: LiveData<T>, val unit: (it: T) -> Unit){
        fun commit(){
            liveData.observeForever(object: Observer<T> {
                override fun onChanged(t: T) {
                    liveData.removeObserver(this)
                    unit(t)
                }
            })
        }
    }