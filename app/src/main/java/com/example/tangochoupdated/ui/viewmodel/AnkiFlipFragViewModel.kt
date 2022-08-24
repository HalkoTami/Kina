package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.AnimationAttributes
import com.example.tangochoupdated.db.enumclass.AnimationController
import com.example.tangochoupdated.db.enumclass.CountFlip
import com.example.tangochoupdated.db.enumclass.FlipAction
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringCheckAnswerFragmentDirections
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringFragmentDirections
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringTypeAnswerFragment
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringTypeAnswerFragmentDirections
import kotlinx.coroutines.launch

class AnkiFlipFragViewModel(val repository: MyRoomRepository) : ViewModel() {



    private val _countFlip = MutableLiveData<CountFlip>()
    fun setCountFlip(countFlip: CountFlip){
        _countFlip.value = countFlip

    }
    fun returnCountFlip():CountFlip?{
        return _countFlip.value
    }
    val countFlip:LiveData<CountFlip> = _countFlip

    private val _flipAction = MutableLiveData<FlipAction>()
    fun setFlipAction (flipAction: FlipAction){
        _flipAction.value = flipAction
    }
    fun returnFlipAction():FlipAction{
        return _flipAction.value ?:FlipAction.LookStringFront
    }
    val flipAction :LiveData<FlipAction> = _flipAction


    private val _countDownAnim = MutableLiveData<AnimationController>()
    fun setCountDownAnim(animationController: AnimationController){
        _countDownAnim.value = animationController
    }
    fun returnCountDownAnim():AnimationController?{
        return _countDownAnim.value
    }
    val countDownAnim:LiveData<AnimationController> = _countDownAnim
    fun controlCountDownAnim(attributes: AnimationAttributes){
        val a = returnCountDownAnim()!!
        a.attributes = attributes
        setCountDownAnim(a)
    }

    fun getCardFromDB(cardId:Int) :LiveData<Card> = repository.getCardByCardId(cardId).asLiveData()
    val _parentCard = MutableLiveData<Card>()
    fun setParentCard(card: Card){
        _parentCard.value = card
    }
    fun returnParentCard():Card?{
        return _parentCard.value
    }
    val parentCard :LiveData<Card> = _parentCard

    val getAllCardsFromDB:LiveData<List<Card>> = repository.allCards.asLiveData()

    private val _parentPosition = MutableLiveData<Int>()
    fun setParentPosition(position: Int){
        if(position<0||returnFlipItems().size-1<position) return else
        _parentPosition.value = position
    }
    val parentPosition :LiveData<Int> = _parentPosition


    private val _typedAnswer = MutableLiveData<String>()
    fun setTypedAnswer(string: String){
            _typedAnswer.value = string
    }
    fun returnTypedAnswer():String{
        return _typedAnswer.value ?:""
    }
    val typedAnswer :LiveData<String> = _typedAnswer

    fun returnParentPosition():Int{
        return _parentPosition.value ?:0
    }
    private val _setting = MutableLiveData<AnkiSettingPopUpViewModel>()
    fun setSetting(viewModel:AnkiSettingPopUpViewModel){
        _setting.value = viewModel
    }
    fun checkFront():Boolean{
        return (returnFlipAction() == FlipAction.LookStringFront)||(returnFlipAction() == FlipAction.TypeAnswerString)
    }
    fun checkBack():Boolean{
        return (returnFlipAction() == FlipAction.LookStringBack)||(returnFlipAction() == FlipAction.CheckAnswerString)
    }
    fun checkChangeToNextCard(reverseMode: Boolean):Boolean{
        return  (reverseMode&&checkFront())||(reverseMode.not()&&checkBack())
    }
    fun checkChangeToPreviouscard(reverseMode: Boolean):Boolean{
        return (reverseMode&&checkBack())||(reverseMode.not()&&checkFront())
    }
    fun checkPositionIsOnStartEdge(reverseMode: Boolean):Boolean{
        return (returnParentPosition() == 0&&checkChangeToPreviouscard(reverseMode))
    }
    fun checkPositionIsOnEndEdge(reverseMode: Boolean):Boolean{
        return (returnParentPosition() == returnFlipItems().size -1&&checkChangeToNextCard(reverseMode))
    }


    fun flipNext(reverseMode:Boolean,typeAnswer:Boolean):NavDirections?{
        val changeCard = checkChangeToNextCard(reverseMode)
        val changeOnlySide = (reverseMode.not()&&checkFront())||(reverseMode&&checkBack())
        val flipToFront = (changeCard&&reverseMode.not())||(changeOnlySide&&reverseMode)
//        val flipToBack = (changeCard&&reverseMode)||(changeOnlySide&&reverseMode.not())
//        val isLastCard = (returnParentPosition()==returnFlipItems().size-1)
        val oldPosition = returnParentPosition()
        val newPosition = returnParentPosition() + 1
        return if(checkPositionIsOnEndEdge(reverseMode))
            null else {
                when(typeAnswer){
                    true -> when(changeCard){
                        true -> {
                            setParentPosition(newPosition)
                            FlipStringTypeAnswerFragmentDirections.toTypeAnswerString(
                                reverseMode.not(),
                                returnFlipItems()[newPosition].id)
                        }
                        false -> {
                            FlipStringCheckAnswerFragmentDirections.toCheckAnswerString(
                                reverseMode.not(),
                                returnTypedAnswer(),
                                returnFlipItems()[oldPosition].id,
                            )
                        }
                    }
                    false -> {
                        if(changeCard)setParentPosition(newPosition)
                        val cardId = if(changeCard) returnFlipItems()[newPosition].id else _parentCard.value!!.id
                        val action =
                        FlipStringFragmentDirections.toFlipString(
                        )
                        action.front = flipToFront
                        action.cardId = cardId
                        return action
                    }
                }

        }
    }
//    fun getFlipAction(fragmentNow:FlipAction):FlipAction{
//        return when(fragmentNow){
//            FlipAction.LookStringFront -> FlipAction.LookStringBack
//            FlipAction.LookStringBack -> FlipAction.LookStringFront
//            FlipAction.TypeAnswerString -> FlipAction.CheckAnswerString
//            FlipAction.CheckAnswerString -> FlipAction.TypeAnswerString }
//    }
//    fun translateFlipAction(flipAction: FlipAction):NavDirections{
//        return when(flipAction){
//            FlipAction.CheckAnswerString -> FlipStringCheckAnswerFragmentDirections.toCheckAnswerString("returnTypedAnswer()")
//            FlipAction.TypeAnswerString -> FlipStringTypeAnswerFragmentDirections.toTypeAnswerString()
//            FlipAction.LookStringBack,FlipAction.LookStringFront -> FlipStringFragmentDirections.toFlipString()
//        }
//    }
    fun flipPrevious(reverseMode:Boolean,typeAnswer: Boolean):NavDirections?{

        val changeCard = checkChangeToPreviouscard(reverseMode)
        val flipToFront = (changeCard&&reverseMode)||(!changeCard&&reverseMode.not())

//        if(!(isFirstCard&&changeCard))
//            setFront(returnFront().not())
//        return if(checkPositionIsOnStartEdge(reverseMode))
//            null else {
//                if(changeCard){
//                    setParentPosition(returnParentPosition()-1)
//                }
//            val action = getFlipAction(returnFlipAction())
//            setFlipAction(action)
//            translateFlipAction(action)
//        }
        val oldPosition = returnParentPosition()
        val newPosition = returnParentPosition() - 1
        return if(checkPositionIsOnStartEdge(reverseMode))
            null else {
            when(typeAnswer){
                true -> when(changeCard){
                    true -> {
                        setParentPosition(newPosition)
                        FlipStringCheckAnswerFragmentDirections.toCheckAnswerString(
                            reverseMode.not(),
                            returnTypedAnswer(),
                            returnFlipItems()[newPosition].id,
                        )
                    }
                    false -> {
                        FlipStringTypeAnswerFragmentDirections.toTypeAnswerString(
                            reverseMode.not(),
                            returnFlipItems()[oldPosition].id)
                    }
                }
                false -> {
                    if (changeCard) setParentPosition(newPosition)
                    val cardId = if(changeCard) returnFlipItems()[newPosition].id else _parentCard.value!!.id
                    val action =
                        FlipStringFragmentDirections.toFlipString(
                        )
                    action.front = flipToFront
                    action.cardId = cardId
                    return action
                }
            }

        }


    }
    private val _front = MutableLiveData<Boolean>()
    fun setFront(boolean: Boolean){
        _front.value = boolean
    }
    val front :LiveData<Boolean> = _front
    fun returnFront():Boolean{
        return _front.value ?:true
    }


    val _ankiFlipItems = MutableLiveData<MutableList<Card>>()
    fun setAnkiFlipItems(list: List<Card>){
        val a = mutableListOf<Card>()
        a.addAll(list)
        _ankiFlipItems.value = a
    }
    fun returnFlipItems():MutableList<Card>{
        return  _ankiFlipItems.value ?: mutableListOf()
    }
    val ankiFlipItems :LiveData<MutableList<Card>> = _ankiFlipItems

    fun changeRememberStatus(){
        val change = _parentCard.value ?:return
        change.remembered = change.remembered.not()
        viewModelScope.launch {
            repository.update(change)
        }
    }
    fun changeFlagStatus(){
        val change = _parentCard.value ?:return
        change.flag = change.remembered.not()
        viewModelScope.launch {
            repository.update(change)
        }
    }
    fun updateFlipped(card:Card){
        viewModelScope.launch {
            repository.updateCardFlippedTime(card)
        }
    }



}