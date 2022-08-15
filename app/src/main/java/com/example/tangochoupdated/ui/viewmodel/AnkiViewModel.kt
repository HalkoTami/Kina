package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnkiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Anki Fragment"
    }
    val text: LiveData<String> = _text
}