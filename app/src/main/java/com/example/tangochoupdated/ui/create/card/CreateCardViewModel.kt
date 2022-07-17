package com.example.tangochoupdated.ui.create.card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.File

class CreateCardViewModel(private val repository: MyRoomRepository) :ViewModel(){

//    DBからとってくる
    fun getParentFlashCardCover(fileId:Int?):LiveData<File?> = repository.getFileByFileId(fileId).asLiveData()
    private val _parentFlashCardCover = MutableLiveData<File?>()
    fun setParentFlashCardCover(file: File?){
        _parentFlashCardCover.value = file
    }

    fun  getParentCard(cardId:Int?): LiveData<Card?> = repository.getCardByCardId(cardId).asLiveData()

    private val _parentCard = MutableLiveData<Card?>()

    fun setParentCard(card: Card?){
        _parentCard.value = card
    }




}