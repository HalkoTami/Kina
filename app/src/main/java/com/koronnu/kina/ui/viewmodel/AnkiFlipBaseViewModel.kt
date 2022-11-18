package com.koronnu.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.koronnu.kina.customClasses.enumClasses.AnimationAttributes
import com.koronnu.kina.customClasses.enumClasses.FlipFragments
import com.koronnu.kina.customClasses.enumClasses.NeighbourCardSide
import com.koronnu.kina.customClasses.normalClasses.AnkiFilter
import com.koronnu.kina.customClasses.normalClasses.CountFlip
import com.koronnu.kina.customClasses.normalClasses.Progress
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.ActivityData
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.enumclass.ActivityStatus
import com.koronnu.kina.db.enumclass.DBTable
import com.koronnu.kina.ui.fragment.flipFragCon.FlipStringCheckAnswerFragDirections
import com.koronnu.kina.ui.fragment.flipFragCon.FlipStringFragDirections
import com.koronnu.kina.ui.fragment.flipFragCon.FlipStringTypeAnswerFragDirections
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnkiFlipBaseViewModel(val repository: MyRoomRepository) : ViewModel() {


    val allActivityData:LiveData<List<ActivityData>> = repository.allActivity.asLiveData()
    private val _flipLeavedTimeInSec = MutableLiveData<Int>()
    private fun setFlipLeavedTimeInSec(timeInSec:Int){
        _flipLeavedTimeInSec.value = timeInSec
    }
    fun addFlipLeavedTimeInSec(timeInSec: Int){
        setFlipLeavedTimeInSec(returnFlipLeavedTimeInSec()+timeInSec)
    }
    fun returnFlipLeavedTimeInSec():Int{
        return _flipLeavedTimeInSec.value ?:0
    }


    private val _rememberedCardsAmountOnStart = MutableLiveData<Int>()
    private fun setRememberedCardsAmountOnStart(startingList: List<Card>){
        _rememberedCardsAmountOnStart.value =  startingList.filter { it.remembered  }.size
    }
    private fun returnRememberedCardsAmountOnStart():Int?{
        return _rememberedCardsAmountOnStart.value
    }
    fun getNewlyRememberedCardAmount():Int{
        return returnFlipItems().filter { it.remembered }.size -(returnRememberedCardsAmountOnStart() ?:0)
    }


    fun onChildFragmentsStart(flipFragments: FlipFragments,
                              reverseMode: Boolean,
                              autoFlip: Boolean, ){
        setFlipFragment(flipFragments)
        if(autoFlip){
            if(returnAutoFlipPaused().not())
                setCountDownAnim(AnimationAttributes.StartAnim)
        }
    }
    private val _autoFlipPaused = MutableLiveData<Boolean>()
    val autoFlipPaused :LiveData<Boolean> = _autoFlipPaused
    fun setAutoFlipPaused(boolean: Boolean){
        _autoFlipPaused.value = boolean
    }
    fun returnAutoFlipPaused():Boolean{
        return _autoFlipPaused.value ?:false
    }


    private val _flipBaseNavCon = MutableLiveData<NavController>()
    fun setFlipBaseNavCon(navController: NavController){
        _flipBaseNavCon.value = navController
    }
    fun returnFlipBaseNavCon(): NavController?{
        return _flipBaseNavCon.value
    }
    private val _countFlip = MutableLiveData<CountFlip>()
    fun setCountFlip(countFlip: CountFlip){
        _countFlip.value = countFlip

    }
    fun returnCountFlip(): CountFlip?{
        return _countFlip.value
    }
    val countFlip:LiveData<CountFlip> = _countFlip

    private val _flipFragment = MutableLiveData<FlipFragments>()
    private fun setFlipFragment (flipAction: FlipFragments){
        _flipFragment.value = flipAction
    }
    fun returnFlipFragment(): FlipFragments {
        return _flipFragment.value ?: FlipFragments.LookStringFront
    }
    val flipFragment:LiveData<FlipFragments> = _flipFragment

    private val _countDownAnim = MutableLiveData<AnimationAttributes>()
    fun setCountDownAnim(animationAttributes: AnimationAttributes){
        _countDownAnim.value = animationAttributes
    }
    val countDownAnim:LiveData<AnimationAttributes> = _countDownAnim

    fun getCardFromDB(cardId:Int) :LiveData<Card> = repository.getCardByCardId(cardId).asLiveData()
    private val _parentCard = MutableLiveData<Card?>()
    fun setParentCard(card: Card?){
        _parentCard.value = card
    }
    fun returnParentCard():Card?{
        return _parentCard.value
    }
    val parentCard :LiveData<Card?> = _parentCard

    val getAllCardsFromDB:LiveData<List<Card>> = repository.allCards.asLiveData()

    private val _parentPosition = MutableLiveData<Int>()
    fun setParentPosition(position: Int){
        if(position<0||returnFlipItems().size-1<position) return else
            _parentPosition.value = position
    }
    val parentPosition:LiveData<Int> = _parentPosition

    fun returnParentPosition():Int{
        return _parentPosition.value ?:0
    }
    private fun checkFront():Boolean{
        return (returnFlipFragment() == FlipFragments.LookStringFront)
    }
    private fun checkBack():Boolean{
        return (returnFlipFragment() == FlipFragments.LookStringBack)
    }

    private fun checkChangeToNextCard(reverseMode: Boolean):Boolean{
        return  (reverseMode&&checkFront())||(reverseMode.not()&&checkBack())
    }
    private fun checkFirstSide(reverseMode: Boolean):Boolean{
        return (reverseMode&&checkBack())||(reverseMode.not()&&checkFront())
                ||returnFlipFragment() == FlipFragments.TypeAnswerString
    }
    private fun checkSecondSide(reverseMode: Boolean):Boolean{
        return (reverseMode&&checkFront())||(reverseMode.not()&&checkBack())
                ||returnFlipFragment() == FlipFragments.CheckAnswerString
    }
    private fun checkChangeToPreviouscard(reverseMode: Boolean):Boolean{
        return (reverseMode&&checkBack())||(reverseMode.not()&&checkFront())
    }
    private fun checkPositionIsOnStartEdge(reverseMode: Boolean):Boolean{
        return (returnParentPosition() == 0&&checkFirstSide(reverseMode))
    }
    private fun checkPositionIsOnEndEdge(reverseMode: Boolean):Boolean{
        return (returnParentPosition() == returnFlipItems().size -1&&checkSecondSide(reverseMode))
    }
    private val _flipProgress = MutableLiveData<Progress>()
    private fun setProgress(progress: Progress){
        _flipProgress.value = progress
    }
    fun changeProgress(reverseMode: Boolean){
        val position = returnParentPosition()
        val first = when(returnFlipFragment()){
            FlipFragments.LookStringBack     -> reverseMode
            FlipFragments.LookStringFront    -> reverseMode.not()
            FlipFragments.TypeAnswerString   -> true
            FlipFragments.CheckAnswerString  -> false
        }
        val now = if(first) (position +1 ) *2 -1 else (position +1)*2
        val all = returnFlipItems().size*2
        setProgress(Progress(now,all))

    }
    val flipProgress : LiveData<Progress> = _flipProgress




    fun startFlip(reverseMode: Boolean,typeAnswer: Boolean,list: List<Card>,startingPosition:Int,flipRoundResumed:Boolean){
        if(flipRoundResumed.not()) {
            setRememberedCardsAmountOnStart(list)
            saveFlipActionStatus(ActivityStatus.FLIP_ROUND_STARTED)
            setFlipLeavedTimeInSec(0)
        }
        fun getAction():NavDirections{
            return when (typeAnswer) {
                true -> {
                    FlipStringCheckAnswerFragDirections.toTypeAnswerString(
                        reverseMode.not(),
                        list[startingPosition].id
                    )
                }
                false -> {
                    val cardId =list[startingPosition].id
                    val action =
                        FlipStringFragDirections.toFlipString(
                        )
                    action.front = reverseMode.not()
                    action.cardId = intArrayOf(cardId)
                    return action
                }
            }
        }
        returnFlipBaseNavCon()?.navigate(getAction())
    }

    fun flip(side: NeighbourCardSide, reverseMode:Boolean, typeAnswer:Boolean):Boolean{

        fun getNextFrag():NavDirections?{
            val changeCard = checkChangeToNextCard(reverseMode)
            val changeOnlySide = (reverseMode.not()&&checkFront())||(reverseMode&&checkBack())
            val flipToFront = (changeCard&&reverseMode.not())||(changeOnlySide&&reverseMode)
            val oldPosition = returnParentPosition()
            val newPosition = returnParentPosition()  + 1
            return   if(checkPositionIsOnEndEdge(reverseMode))
                null else {
                when(typeAnswer){
                    true -> when(returnFlipFragment()){
                        FlipFragments.CheckAnswerString -> {
                            FlipStringTypeAnswerFragDirections.toTypeAnswerString(
                                reverseMode.not(),
                                returnFlipItems()[newPosition].id)
                        }
                        FlipFragments.TypeAnswerString -> {
                            FlipStringCheckAnswerFragDirections.toCheckAnswerString(
                                reverseMode.not(),
                                true,
                                returnFlipItems()[oldPosition].id,
                            )
                        }
                        else -> return null
                    }
                    false -> {
                        val cardId = if(changeCard) returnFlipItems()[newPosition].id else _parentCard.value!!.id
                        val action =
                            FlipStringFragDirections.toFlipString(
                            )
                        action.front = flipToFront
                        action.cardId = intArrayOf(cardId)
                        action
                    }
                }

            }

        }
        fun getPreviousFrag():NavDirections?{

            val changeCard = checkChangeToPreviouscard(reverseMode)
            val flipToFront = (changeCard&&reverseMode)||(!changeCard&&reverseMode.not())
            val oldPosition = returnParentPosition()
            val newPosition = returnParentPosition() - 1
            return if(checkPositionIsOnStartEdge(reverseMode))
                null else {
                when(typeAnswer){
                    true -> when(changeCard){
                        true -> {
                            FlipStringCheckAnswerFragDirections.toCheckAnswerString(
                                reverseMode.not(),
                                false,
                                returnFlipItems()[newPosition].id,
                            )
                        }
                        false -> {
                            FlipStringTypeAnswerFragDirections.toTypeAnswerString(
                                reverseMode.not(),
                                returnFlipItems()[oldPosition].id)
                        }
                    }
                    false -> {
                        val cardId = if(changeCard) returnFlipItems()[newPosition].id else _parentCard.value!!.id
                        val action =
                            FlipStringFragDirections.toFlipString(
                            )
                        action.front = flipToFront
                        action.cardId = intArrayOf(cardId)
                        return action
                    }
                }

            }


        }
        val action = when(side){
            NeighbourCardSide.PREVIOUS-> getPreviousFrag()
            NeighbourCardSide.NEXT -> getNextFrag()
        }
        returnFlipBaseNavCon()?.navigate(action ?:return false)
        return true
    }



    private val _front = MutableLiveData<Boolean>()
    fun setFront(boolean: Boolean){
        _front.value = boolean
    }


    private val _ankiFlipItems = MutableLiveData<MutableList<Card>>()
    fun setAnkiFlipItems(list: List<Card>,ankiFilter: AnkiFilter){
        val a = mutableListOf<Card>()
        a.addAll(list)
        a.sortBy { it.cardBefore }
        val flag = if(ankiFilter.flagFilterActive) a.filter { it.flag == ankiFilter.flag }else a
        val remembered = if(ankiFilter.rememberedFilterActive) flag.filter { it.remembered == ankiFilter.remembered } else flag
        val answerTyped =  if(ankiFilter.answerTypedFilterActive) remembered.filter { it.lastTypedAnswerCorrect == ankiFilter.correctAnswerTyped } else remembered
        _ankiFlipItems.value = answerTyped.toMutableList()
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
    fun saveFlipActionStatus(activityStatus: ActivityStatus){
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN)
        val a = ActivityData(id = 0,
            activityStatus =
            activityStatus,
            dateTime =  formatter.format(Date()).toString())
        viewModelScope.launch {
            repository.insert(a )
        }
    }
    fun updateFlipped(card:Card){
        viewModelScope.launch {
            repository.updateCardFlippedTime(card)
        }
    }
    fun updateLookedTime(card:Card,opened:Boolean){
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN)
        val a = ActivityData(0,card.id,DBTable.TABLE_CARD,
            if(opened) ActivityStatus.CARD_OPENED else ActivityStatus.CARD_CLOSED,formatter.format(Date()).toString())
        viewModelScope.launch {
            repository.insert(a)
        }
    }



}