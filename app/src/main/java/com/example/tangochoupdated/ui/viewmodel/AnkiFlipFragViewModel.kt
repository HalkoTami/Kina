package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.*
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringCheckAnswerFragmentDirections
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringFragmentDirections
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringTypeAnswerFragmentDirections
import kotlinx.coroutines.launch

class AnkiFlipFragViewModel(val repository: MyRoomRepository) : ViewModel() {


    fun onChildFragmentsStart(flipFragments: FlipFragments,
                              reverseMode: Boolean,
                              autoFlip: Boolean){
        setFlipFragment(flipFragments)
        changeProgress(reverseMode)
        if(autoFlip){
            setCountDownAnim(AnimationAttributes.StartAnim)
        }
    }
    private val _countFlip = MutableLiveData<CountFlip>()
    fun setCountFlip(countFlip: CountFlip){
        _countFlip.value = countFlip

    }
    fun returnCountFlip():CountFlip?{
        return _countFlip.value
    }
    val countFlip:LiveData<CountFlip> = _countFlip

    private val _flipFragment = MutableLiveData<FlipFragments>()
    fun setFlipFragment (flipAction: FlipFragments){
        _flipFragment.value = flipAction
    }
    fun returnFlipFragment():FlipFragments{
        return _flipFragment.value ?:FlipFragments.LookStringFront
    }
    val flipFragment :LiveData<FlipFragments> = _flipFragment


    private val _countDownAnim = MutableLiveData<AnimationAttributes>()
    fun setCountDownAnim(animationAttributes: AnimationAttributes){
        _countDownAnim.value = animationAttributes
    }
    val countDownAnim:LiveData<AnimationAttributes> = _countDownAnim

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
        return (returnFlipFragment() == FlipFragments.LookStringFront)
    }
    fun checkBack():Boolean{
        return (returnFlipFragment() == FlipFragments.LookStringBack)
    }

    fun checkChangeToNextCard(reverseMode: Boolean):Boolean{
        return  (reverseMode&&checkFront())||(reverseMode.not()&&checkBack())
    }
    fun checkFirstSide(reverseMode: Boolean):Boolean{
        return (reverseMode&&checkBack())||(reverseMode.not()&&checkFront())
                ||returnFlipFragment() == FlipFragments.TypeAnswerString
    }
    fun checkSecondSide(reverseMode: Boolean):Boolean{
        return (reverseMode&&checkFront())||(reverseMode.not()&&checkBack())
                ||returnFlipFragment() == FlipFragments.CheckAnswerString
    }
    fun checkChangeToPreviouscard(reverseMode: Boolean):Boolean{
        return (reverseMode&&checkBack())||(reverseMode.not()&&checkFront())
    }
    fun checkPositionIsOnStartEdge(reverseMode: Boolean):Boolean{
        return (returnParentPosition() == 0&&checkFirstSide(reverseMode))
    }
    fun checkPositionIsOnEndEdge(reverseMode: Boolean):Boolean{
        return (returnParentPosition() == returnFlipItems().size -1&&checkSecondSide(reverseMode))
    }
    private val _flipProgress = MutableLiveData<Progress>()
    fun setProgress(progress: Progress){
        _flipProgress.value = progress
    }
    fun changeProgress(reverseMode: Boolean){
        val first = when(returnFlipFragment()){
            FlipFragments.LookStringBack     -> reverseMode
            FlipFragments.LookStringFront    -> reverseMode.not()
            FlipFragments.TypeAnswerString   -> true
            FlipFragments.CheckAnswerString  -> false
        }
        val now = if(first) (returnParentPosition() +1) *2 -1 else (returnParentPosition()+1)*2
        val all = returnFlipItems().size*2
        setProgress(Progress(now,all))

    }
    val flipProgress : LiveData<Progress> = _flipProgress


    fun getStart(reverseMode: Boolean,typeAnswer: Boolean):NavDirections{
         return when (typeAnswer) {
            true -> {
                FlipStringTypeAnswerFragmentDirections.toTypeAnswerString(
                    reverseMode.not(),
                    returnFlipItems()[0].id
                )
            }
            false -> {
                val cardId =returnFlipItems()[0].id
                val action =
                    FlipStringFragmentDirections.toFlipString(
                    )
                action.front = reverseMode.not()
                action.cardId = intArrayOf(cardId)
                return action
            }
        }
    }
    fun flipNext(reverseMode:Boolean,typeAnswer:Boolean):NavDirections?{
        val changeCard = checkChangeToNextCard(reverseMode)
        val changeOnlySide = (reverseMode.not()&&checkFront())||(reverseMode&&checkBack())
        val flipToFront = (changeCard&&reverseMode.not())||(changeOnlySide&&reverseMode)
//        val flipToBack = (changeCard&&reverseMode)||(changeOnlySide&&reverseMode.not())
//        val isLastCard = (returnParentPosition()==returnFlipItems().size-1)
        val oldPosition = returnParentPosition()
        val newPosition = returnParentPosition()  + 1
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
                                true,
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
                        action.cardId = intArrayOf(cardId)
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
                            false,
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
                    action.cardId = intArrayOf(cardId)
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
        change.flag = change.flag.not()
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