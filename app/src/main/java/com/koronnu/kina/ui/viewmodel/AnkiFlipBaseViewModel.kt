package com.koronnu.kina.ui.viewmodel

import android.animation.ValueAnimator
import android.content.res.Resources
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.koronnu.kina.R
import com.koronnu.kina.actions.DateTimeActions
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
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
import com.koronnu.kina.ui.tabAnki.flipCompleted.AnkiFlipCompleteFragDirections
import com.koronnu.kina.ui.fragment.base_frag_con.EditCardBaseFragDirections
import com.koronnu.kina.ui.tabAnki.flip.checkTypedAnswer.FlipStringCheckAnswerFragDirections
import com.koronnu.kina.ui.tabAnki.flip.lookString.FlipStringFragDirections
import com.koronnu.kina.ui.tabAnki.flip.typeAnswer.FlipStringTypeAnswerFragDirections
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnkiFlipBaseViewModel(val repository: MyRoomRepository,
                            val resources: Resources) : ViewModel() {
    private lateinit var mainViewModel: MainViewModel
    private val createCardViewModel get() = mainViewModel.createCardViewModel
    private val ankiBaseViewModel get() = mainViewModel.ankiBaseViewModel
    private val ankiSettingPopUpViewModel get() = ankiBaseViewModel.ankiSettingPopUpViewModel
    companion object{
        fun getFactory(mainViewModel: MainViewModel): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val resources = application.resources
                val baseViewModel = AnkiFlipBaseViewModel(repository,resources)
                baseViewModel.mainViewModel = mainViewModel
                return baseViewModel as T
            }
        }
    }
    val lastFlipDurationInMin = repository.lastFlipRoundDuration.asLiveData()
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

    fun onChildFragmentsStart(
        flipFragments: FlipFragments,
        autoFlip: Boolean, ){
        setFlipFragment(flipFragments)
        setLoadingCountDownAnim(true)
    }
    val _autoFlipRunning = MutableLiveData<Boolean>()
    fun setAutoFlipRunning(boolean: Boolean){
        _autoFlipRunning.value = boolean
        if(boolean) getCountDownAnim?.resume()
        else getCountDownAnim?.pause()
    }
    val getAutoFlipRunning get() = _autoFlipRunning.value ?:false



    private val _flipBaseNavCon = MutableLiveData<NavController>()
    fun setFlipBaseNavCon(navController: NavController){
        _flipBaseNavCon.value = navController
    }
    private fun returnFlipBaseNavCon(): NavController?{
        return _flipBaseNavCon.value
    }
    private val _countFlip = MutableLiveData<CountFlip>()
    fun setCountFlip(countFlip: CountFlip){
        _countFlip.value = countFlip

    }
    fun returnCountFlip(): CountFlip?{
        return _countFlip.value
    }

    private val _flipFragment = MutableLiveData<FlipFragments>()
    private fun setFlipFragment (flipAction: FlipFragments){
        _flipFragment.value = flipAction
    }
    fun returnFlipFragment(): FlipFragments {
        return _flipFragment.value ?: FlipFragments.LookStringFront
    }

    private val _countDownAnim = MutableLiveData<ValueAnimator>()
    fun setCountDownAnim(valueAnimator: ValueAnimator){
        getCountDownAnim?.cancel()
        _countDownAnim.value = valueAnimator
        doAfterCountDownAnimSet()
    }
    fun doAfterCountDownAnimSet(){
        if(ankiSettingPopUpViewModel.getAutoFlip.active){
            getCountDownAnim?.start()
            if(!getAutoFlipRunning) getCountDownAnim?.pause()
        }
    }
    val getCountDownAnim get() = _countDownAnim.value

    val _loadingNewCountDownAnim = MutableLiveData<Boolean>()
    val loadingNewCountDownAnim :LiveData<Boolean> = _loadingNewCountDownAnim
    fun setLoadingCountDownAnim(boolean: Boolean){
        _loadingNewCountDownAnim.value = boolean
    }

    fun getCardFromDB(cardId:Int) :LiveData<Card> = repository.getCardByCardId(cardId).asLiveData()
    val _parentCard = MutableLiveData<Card>()
    fun setParentCard(card: Card){
        if(_parentCard.value===card) return
        _parentCard.value = card
        doAfterParentCardChanged()
    }
    val getParentCard get() = _parentCard.value!!
    val topBarFlipProgressText = MutableLiveData<String>()
    fun setTopBarFlipProgressText(){
        val flipItems = returnFlipItems()
        val updatedString = resources.getString(R.string.flipProgress,flipItems.indexOf(getParentCard)+1,flipItems.size)
        topBarFlipProgressText.value = updatedString
    }
    fun doAfterParentCardChanged(){
        val flipItems = returnFlipItems()
        updateLookedTime(getParentCard)
        setTopBarFlipProgressText()
        createCardViewModel.setStartingCardId(getParentCard.id)
        setParentPosition(flipItems.indexOf(getParentCard))
        changeProgress()
    }
    val getAllCardsFromDB:LiveData<List<Card>> = repository.allCards.asLiveData()

    private val _parentPosition = MutableLiveData<Int>()
    fun setParentPosition(position: Int){
        if(position<0||returnFlipItems().size-1<position) return else
            _parentPosition.value = position
    }

    fun returnParentPosition():Int{
        return _parentPosition.value ?:0
    }
    private fun checkFront():Boolean{
        return (returnFlipFragment() == FlipFragments.LookStringFront)
    }

    val reverseMode get() = ankiSettingPopUpViewModel.getReverseCardSideActive
    private val changeCardIfFlipNext get()= parentFlipSide==FlipSide.Back
    private val changeCardIfFlipPrevious get() = parentFlipSide == FlipSide.Front
    private val _flipProgress = MutableLiveData<Progress>()
    private fun setProgress(progress: Progress){
        _flipProgress.value = progress
    }
    fun changeProgress(){
        val reverseMode = ankiSettingPopUpViewModel.getReverseCardSideActive
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
                        FlipStringFragDirections.toFlipString()
                    action.front = reverseMode.not()
                    action.cardId = intArrayOf(cardId)
                    return action
                }
            }
        }
        setParentFlipSide(FlipSide.Front)
        setAutoFlipRunning(ankiSettingPopUpViewModel.getAutoFlip.active)
        returnFlipBaseNavCon()?.navigate(getAction())

    }
    val typeAnswer get() = ankiSettingPopUpViewModel.getTypeAnswer
    val nextCard get() =  returnFlipItems()[returnParentPosition()+1]
    val previousCard get() =  returnFlipItems()[returnParentPosition()-1]
    fun getTypeAndCheckNavDirection(side:NeighbourCardSide):NavDirections{
        val changeCard = if(side==NeighbourCardSide.NEXT) nextCard else previousCard
        return when(returnFlipFragment()){
            FlipFragments.CheckAnswerString ->
                FlipStringTypeAnswerFragDirections.toTypeAnswerString(
                reverseMode.not(),
                changeCard.id)
            FlipFragments.TypeAnswerString ->
                FlipStringCheckAnswerFragDirections.toCheckAnswerString(
                    reverseMode.not(),
                    true,
                    getParentCard.id,
                )
            else -> throw IllegalArgumentException()
    }}
    val parentSideIsFront:Boolean get() = checkFront()
    fun getFlipStringNavDirection(side: NeighbourCardSide):NavDirections{
        val card = if(side == NeighbourCardSide.NEXT&&changeCardIfFlipNext) nextCard
        else if(side== NeighbourCardSide.PREVIOUS&&changeCardIfFlipPrevious) previousCard
        else getParentCard
        val cardId = card.id
        val action = FlipStringFragDirections.toFlipString()
        action.front = parentSideIsFront.not()
        action.cardId = intArrayOf(cardId)
        return action
    }
    enum class FlipSide{
        Front, Back
    }
    val _parentFlipSide = MutableLiveData<FlipSide>()
    val parentFlipSide get() =_parentFlipSide.value ?:FlipSide.Front
    fun setParentFlipSide(flipSide: FlipSide){
        _parentFlipSide.value = flipSide
    }

    val isLastCard get() = (returnFlipItems().last() == getParentCard)
    val isLastCardLastSide get() = isLastCard&&parentFlipSide==FlipSide.Back
    val isFirstCard get() = (returnFlipItems().first() == getParentCard)

    val isFirstCardFirstSide get() = isFirstCard&&parentFlipSide==FlipSide.Front
    fun flip(side: NeighbourCardSide):Boolean{
        if(side == NeighbourCardSide.PREVIOUS&&isFirstCardFirstSide) return false
        if(side==NeighbourCardSide.NEXT&&isLastCardLastSide) {
            ankiBaseViewModel.navigateInAnkiFragments(AnkiFragments.FlipCompleted)
            return true
        }
        val action = if(typeAnswer) getTypeAndCheckNavDirection(side)
        else getFlipStringNavDirection(side)
        returnFlipBaseNavCon()?.navigate(action )
        reverseFlipSide()
        return true
    }
    fun reverseFlipSide(){
        val reverseSide = when(parentFlipSide){
            FlipSide.Front->FlipSide.Back
            FlipSide.Back -> FlipSide.Front
        }
        setParentFlipSide(reverseSide)
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
        val formatter =  SimpleDateFormat(resources.getString(R.string.activityData_dateFormat), Locale.JAPAN)
        val a = ActivityData(id = 0,
            activityStatus =
            activityStatus,
            dateTime =  DateTimeActions().parentTimeToString())
        viewModelScope.launch {
            repository.insert(a )
        }
    }

    fun updateLookedTime(card:Card){
        val a = ActivityData(0,card.id,DBTable.TABLE_CARD,
            ActivityStatus.CARD_OPENED,DateTimeActions().parentTimeToString())
        viewModelScope.launch {
            repository.insert(a)
        }
    }
    val _frConfirmEndVisible = MutableLiveData<Boolean>()
    fun setFrConfirmEndVisible(visible: Boolean){
        _frConfirmEndVisible.value = visible
    }
    val frConfirmEndVisible get() = _frConfirmEndVisible.value ?:false


    fun cancelEnd(){
        setFrConfirmEndVisible(false)
    }
    fun end(){
        ankiBaseViewModel.getAnkiBaseNavCon.popBackStack()
        saveFlipActionStatus(ActivityStatus.FLIP_ROUND_ENDED)
        setFrConfirmEndVisible(false)
    }

    fun openParentFlipItems(){
        ankiBaseViewModel.navigateInAnkiFragments(AnkiFragments.FlipItems)
    }
    fun openCreateCardByCardId(editingId:Int){
        createCardViewModel.setStartingCardId(editingId)
        mainViewModel.getMainActivityNavCon
            .navigate(EditCardBaseFragDirections.openCreateCard())
    }
    fun editParentFlipCard(){
        try {
            openCreateCardByCardId(getParentCard.id)
        } catch (error:java.lang.NullPointerException){
            println(error)
            println("flip called before parentCard set")
            return
        }
    }

    fun controlCountDown(){
        setAutoFlipRunning(getAutoFlipRunning.not())
    }

    fun onBackPressed():Boolean{
        setFrConfirmEndVisible(frConfirmEndVisible.not())
        return true
    }

}