package com.koronnu.kina.ui.tabAnki.flip

import android.content.res.Resources
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.koronnu.kina.R
import com.koronnu.kina.RoomApplication
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.data.source.local.entity.ActivityData
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.enumclass.ActivityStatus
import com.koronnu.kina.data.source.local.entity.enumclass.DBTable
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FlipTypeAndCheckViewModel(val repository: MyRoomRepository,
                                val resources: Resources) : ViewModel() {

    companion object{
        val Factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val resources = application.resources
                return FlipTypeAndCheckViewModel(repository,resources) as T
            }
        }
    }
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
        return b[cardId] ?:String()
    }

    fun checkAnswer(card: Card, answerIsBack:Boolean){
        val backContentIsCorrect = card.stringData?.backText==getAnswer(card.id)
        val frontContentIsCorrect = card.stringData?.frontText==getAnswer(card.id)
        val answerFront = !answerIsBack
        val activityStatus=
        if(!answerIsBack){
            if(frontContentIsCorrect)
                ActivityStatus.RIGHT_FRONT_CONTENT_TYPED
            else ActivityStatus.WRONG_FRONT_CONTENT_TYPED
        } else {
            if(backContentIsCorrect)
                ActivityStatus.RIGHT_BACK_CONTENT_TYPED
            else ActivityStatus.WRONG_BACK_CONTENT_TYPED
        }
        val answerCorrect = (answerFront&&frontContentIsCorrect)||(answerIsBack&&backContentIsCorrect)
        viewModelScope.launch {
            val a =SimpleDateFormat(resources.getString(R.string.activityData_dateFormat),Locale.JAPAN)
            val datetime = a.format(Date())
            repository.insert(ActivityData(0,
                activityTokenId = card.id,
                activityStatus = activityStatus,
                dateTime = datetime,
                idTokenTable = DBTable.TABLE_CARD))
            if(answerCorrect)
                repository.updateCardRememberedStatus(card,true)
        }
    }

}