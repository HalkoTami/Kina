package com.koronnu.kina.ui.viewmodel

import androidx.lifecycle.*
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.ActivityData
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.enumclass.ActivityStatus
import com.koronnu.kina.db.enumclass.DBTable
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FlipTypeAndCheckViewModel(val repository: MyRoomRepository) : ViewModel() {

    fun getActivityData(cardId: Int):LiveData<List<ActivityData>> = repository.getCardActivity(cardId).asLiveData()

   private val _keyBoardVisible = MutableLiveData<Boolean>()
    fun setKeyBoardVisible(boolean: Boolean){
        val before = _keyBoardVisible.value
        if(before != boolean){
            _keyBoardVisible.value = boolean
        } else return

    }
    val keyBoardVisible :LiveData<Boolean> = _keyBoardVisible

    private val _typedAnswers = MutableLiveData<MutableMap<Int,String>>()
    private fun setTypedAnswers(map:MutableMap<Int,String>){
        _typedAnswers.value = map
    }
    private fun returnTypedAnswers():MutableMap<Int,String>{
        return _typedAnswers.value ?: mutableMapOf()
    }
    val typedAnswers :LiveData<MutableMap<Int,String>> = _typedAnswers
    fun addAnswer(cardId:Int ,answer:String){
        val a = returnTypedAnswers()
        a[cardId] = answer
        setTypedAnswers(a)
    }
    private fun getAnswer(cardId: Int):String{
        val b = returnTypedAnswers()
        return b[cardId] ?:""
    }

    fun checkAnswer(card:Card,answerIsBack:Boolean){
        val activityStatus=
        if(!answerIsBack){
            if(card.stringData?.frontText==getAnswer(card.id))
                ActivityStatus.RIGHT_FRONT_CONTENT_TYPED
            else ActivityStatus.WRONG_FRONT_CONTENT_TYPED
        } else {
            if(card.stringData?.backText==getAnswer(card.id))
                ActivityStatus.RIGHT_BACK_CONTENT_TYPED
            else ActivityStatus.WRONG_BACK_CONTENT_TYPED
        }
        viewModelScope.launch {
            val a =SimpleDateFormat("dd/M/yyyy hh:mm:ss",Locale.JAPAN)
            val datetime = a.format(Date())
            repository.insert(ActivityData(0,
                activityTokenId = card.id,
                activityStatus = activityStatus,
                dateTime = datetime,
                idTokenTable = DBTable.TABLE_CARD))
        }
    }

}