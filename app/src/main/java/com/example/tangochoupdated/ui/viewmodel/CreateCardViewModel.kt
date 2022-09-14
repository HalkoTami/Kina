package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.dataclass.StringData
import com.example.tangochoupdated.db.enumclass.CardStatus
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.makeToast
import com.example.tangochoupdated.ui.fragment.base_frag_con.CreateCardFragmentBaseDirections
import com.example.tangochoupdated.ui.fragment.createCard_frag_com.CreateCardFragmentDirections
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiFragments
import com.example.tangochoupdated.ui.viewmodel.customClasses.MainFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class CreateCardViewModel(private val repository: MyRoomRepository) :ViewModel(){


    private val _mainActivityNavCon = MutableLiveData<NavController>()
    fun setMainActivityNavCon(navController: NavController){
        _mainActivityNavCon.value = navController
    }
    fun returnMainActivityNavCon(): NavController?{
        return _mainActivityNavCon.value
    }
    private val _openEditCard = MutableLiveData<Boolean>()
    fun setOpenEditCard (boolean: Boolean){
        _openEditCard.value = boolean
    }
    fun returnOpenEditCard ():Boolean{
        return _openEditCard.value ?:true
    }

    fun getParentFlashCardCover(fileId:Int?):LiveData<File?> = repository.getFileByFileId(fileId).asLiveData()
    private val _parentFlashCardCover = MutableLiveData<File?>()
    fun setParentFlashCardCover(file: File?){
        _parentFlashCardCover.value = file
    }
    fun onClickEditCardFromRV(card: Card,){
        setParentPosition(card.libOrder)
        returnMainActivityNavCon()?.navigate(CreateCardFragmentBaseDirections.openCreateCard())

    }
    val parentFlashCardCover:LiveData<File?> = _parentFlashCardCover
    fun returnParentFlashCardCover():File?{
        return _parentFlashCardCover.value
    }

//    parent card
    fun getParentCard(cardId: Int):LiveData<Card> = repository.getCardByCardId(cardId).asLiveData()
    private val _parentCard = MutableLiveData<Card?>()
    fun setParentCard(card: Card?){
        val before = _parentCard.value
        if(card==before )return else
        _parentCard.value = card
    }
    fun returnParentCard():Card?{
        return _parentCard.value
    }
    val parentCard :LiveData<Card?> = _parentCard

    fun getSisterCards(fileId: Int?):LiveData<List<Card>> = repository.getCardDataByFileId(fileId).asLiveData()
    private val _sisterCards = MutableLiveData<List<Card>>()
    fun setSisterCards(list:List<Card>){
        _sisterCards.value = list
    }
    private val _parentPosition = MutableLiveData<Int>()
    val parentPosition:LiveData<Int> = _parentPosition
    fun setParentPosition(int: Int){
        _parentPosition.value = int
    }
    fun returnParentPosition():Int{
        return _parentPosition.value ?:0
    }
    val sisterCards: LiveData<List<Card>> = _sisterCards
    fun returnSisterCards():List<Card>{
        return _sisterCards.value ?: mutableListOf()
    }
    fun lastInsertedCard(flashCardCoverId:Int?) :LiveData<Card> = repository.lastInsertedCard(flashCardCoverId).asLiveData()

    private val _cardStatus = MutableLiveData<CardStatus>()
    val cardStatus :LiveData<CardStatus> = _cardStatus
    fun setCardStatus(cardStatus: CardStatus){
        _cardStatus.value = cardStatus
    }
    private val _saveCard = MutableLiveData<Boolean>()
    val saveCard :LiveData<Boolean> = _saveCard
    fun setSaveCard(boolean: Boolean){
        _saveCard.value = boolean
    }
    private val _confirmFlipItemPopUpVisible = MutableLiveData<Boolean>()
    val confirmFlipItemPopUpVisible :LiveData<Boolean> = _confirmFlipItemPopUpVisible
    fun setConfirmFlipItemPopUpVisible(boolean: Boolean){
        _confirmFlipItemPopUpVisible.value = boolean
    }

//    card color attributes
    class ColorPalletStatus(
    var colNow:ColorStatus,
    var before:ColorStatus?
    )
    private val _cardColor = MutableLiveData<ColorPalletStatus>()
    val cardColor: LiveData<ColorPalletStatus> = _cardColor
    fun setCardColor(colorStatus: ColorStatus){
        val previous = _cardColor.value?.colNow
        if(previous == colorStatus){
            return
        } else{
            val newStatus = ColorPalletStatus(colorStatus,previous)
            _cardColor.value = newStatus
        }
    }

    private val _colPalletVisibility = MutableLiveData<Boolean>()
    val colPalletVisibility: LiveData<Boolean> = _colPalletVisibility
    fun setColPalletVisibility(boolean: Boolean){
        _colPalletVisibility.value = boolean
    }
    fun changeColPalletVisibility(){
        setColPalletVisibility( _colPalletVisibility.value?.not() ?:false)
    }
    private val _stringData = MutableLiveData<StringData?>()
    val stringData :LiveData<StringData?> =_stringData
    fun saveEmptyCard(position:Int,parentFlashCardCoverId:Int?){
        val newCard = Card(
            id = 0,
            libOrder =  position,
            deleted = false,
            colorStatus = ColorStatus.GRAY,
            cardStatus = CardStatus.STRING,
            quizData = null,
            markerData = null,
            stringData = null,
            belongingFlashCardCoverId = parentFlashCardCoverId,
            remembered = false
        )
        insert(newCard)
    }
    fun upDateCard(stringData: StringData,card: Card){
        val upDating = card
        upDating.stringData = stringData
        update(upDating)
    }
    private fun insert(any: Any){
        viewModelScope.launch {
            repository.insert(any)
        }

    }
    private fun update(any: Any){
        viewModelScope.launch {
            repository.update(any)
        }
    }
    fun updateCardPosition(card: Card,adapterPosition:Int){
        if(card.libOrder!=adapterPosition){
            card.libOrder = adapterPosition
            update(card)
        }
    }

//    navigation
    fun checkMakePopUpVisible(mainFragmentStatus: BaseViewModel.MainActivityChildFragmentStatus,ankiActiveFrag: AnkiFragments){
    if(mainFragmentStatus.before== MainFragment.Anki&&
        ankiActiveFrag == AnkiFragments.Flip){
        setConfirmFlipItemPopUpVisible(true)
    }
    }

    fun onClickBtnInsert(side:NeighbourCardSide,){
        setOpenEditCard(true)
        val orderNow= returnSisterCards().indexOf(returnParentCard())
        saveEmptyCard(
            when(side){
                NeighbourCardSide.NEXT -> orderNow + 1
                NeighbourCardSide.PREVIOUS -> orderNow 
                                },returnParentCard()?.belongingFlashCardCoverId)
    }




    enum class NeighbourCardSide{
        NEXT,PREVIOUS
    }


    fun getNeighbourCardId(side:NeighbourCardSide):Int?{
        val nowPosition = returnSisterCards().indexOf(returnParentCard())
        val newPosition = when(side){
            NeighbourCardSide.PREVIOUS -> nowPosition - 1
            NeighbourCardSide.NEXT -> nowPosition + 1}
        val neighbourCard =  if(newPosition > returnSisterCards().size-1||newPosition<0) null
        else returnSisterCards()[newPosition]
        return neighbourCard?.id

    }
    fun onClickBtnNavigate(navController: NavController,side: NeighbourCardSide){
        val flashCardId = returnParentCard()?.belongingFlashCardCoverId
        val a = if(flashCardId!=null) intArrayOf(flashCardId) else null
        val nextCardId = getNeighbourCardId(side)
        if(nextCardId!=null){
           val b = intArrayOf(nextCardId)
           val action = CreateCardFragmentDirections.flipCreateCard()
            action.parentFlashCardCoverId = a
            action.cardId = b
            navController.popBackStack()
           navController.navigate(action)
       }
    }

    fun onClickRVAddNewCard(item:Card,mainNavCon:NavController){
        setOpenEditCard(true)
        saveEmptyCard(item.libOrder + 1,item.belongingFlashCardCoverId,)
    }
    fun onClickAddNewCardBottomBar(){
        returnMainActivityNavCon()?.navigate(CreateCardFragmentBaseDirections.openCreateCard())
        setParentPosition(returnSisterCards().size)
        setOpenEditCard(true)
        saveEmptyCard(returnSisterCards().size ,returnParentFlashCardCover()?.fileId)

    }
    fun onClickAddNewCardRV(itemBefore:Card){
        returnMainActivityNavCon()?.navigate(CreateCardFragmentBaseDirections.openCreateCard())
        saveEmptyCard(itemBefore.libOrder +1,returnParentFlashCardCover()?.fileId)
        setOpenEditCard(true)

    }

    fun onClickEditCard(item: Card,navController: NavController){
        setSaveCard(true)
        setSaveCard(false)
        setOpenEditCard(false)
        val a =if(item.belongingFlashCardCoverId== null) null else intArrayOf(item.belongingFlashCardCoverId!!)
        val b = intArrayOf(item.id)
        val action = CreateCardFragmentDirections.flipCreateCard()
        action.cardId= b
        action.parentFlashCardCoverId = a
        navController.popBackStack()
        navController.navigate(action)

    }


}