package com.koronnu.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.dataclass.StringData
import com.koronnu.kina.db.enumclass.CardStatus
import com.koronnu.kina.db.enumclass.ColorStatus
import com.koronnu.kina.ui.fragment.base_frag_con.EditCardBaseFragDirections
import com.koronnu.kina.ui.fragment.createCard_frag_com.EditCardFragDirections
import com.koronnu.kina.customClasses.enumClasses.NeighbourCardSide
import com.koronnu.kina.databinding.CreateCardFragMainBinding
import com.koronnu.kina.databinding.CreateCardFragStringFragBinding
import kotlinx.coroutines.launch


class CreateCardViewModel(private val repository: MyRoomRepository) :ViewModel(){

    lateinit var mainViewModel: MainViewModel


    companion object{
        fun getFactory(mainViewModel: MainViewModel) : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val createCardViewModel = CreateCardViewModel(repository)
                createCardViewModel.mainViewModel = mainViewModel
                return createCardViewModel as T
            }
        }
    }

    private val _editCardBaseFragNavDirection = MutableLiveData<NavDirections?>()
    fun setEditCardBaseFragNavDirection(direction: NavDirections?){
        _editCardBaseFragNavDirection.value = direction
    }
    val editCardBaseFragNavDirection :LiveData<NavDirections?> = _editCardBaseFragNavDirection

    private var _createCardStringBinding :CreateCardFragStringFragBinding? = null
    fun setCreateCardStringBinding(createCardFragStringFragBinding: CreateCardFragStringFragBinding){
        _createCardStringBinding = createCardFragStringFragBinding
    }


    private val _mainActivityNavCon = MutableLiveData<NavController>()
    fun setMainActivityNavCon(navController: NavController){
        _mainActivityNavCon.value = navController
    }
    private fun returnMainActivityNavCon(): NavController?{
        return _mainActivityNavCon.value
    }
    private val _openEditCard = MutableLiveData<Boolean>()
    private fun setOpenEditCard (boolean: Boolean){
        _openEditCard.value = boolean
    }

    fun getFileAndChildrenCards(fileId:Int?): LiveData<Map<File, List<Card>>> = repository.getFileAndChildrenCards(fileId).asLiveData()
    val cardsWithoutCardsFlashCard:LiveData<List<Card>?> =  this.repository.getCardDataByFileIdSorted(null).asLiveData()

    private val _parentFlashCardCover = MutableLiveData<File?>()
    fun setParentFlashCardCover(file: File?){
        _parentFlashCardCover.value = file
    }
    fun onClickEditCardFromRV(card: Card){
        setStartingCardId(card.id)
        returnMainActivityNavCon()?.navigate(EditCardBaseFragDirections.openCreateCard())

    }
    val parentFlashCardCover:LiveData<File?> = _parentFlashCardCover
    private fun returnParentFlashCardCover():File?{
        return _parentFlashCardCover.value
    }

//    parent card
    fun getParentCard(cardId: Int):LiveData<Card> = repository.getCardByCardId(cardId).asLiveData()
    private val _parentCard = MutableLiveData<Card?>()
    fun setParentCard(card: Card?){
        val before = _parentCard.value
        if(card==before ) return else
            _parentCard.value = card
    }
    fun returnParentCard():Card?{
        return _parentCard.value
    }
    val parentCard :LiveData<Card?> = _parentCard


    fun getSisterCards(fileId: Int?):LiveData<List<Card>> = repository.getCardDataByFileId(fileId).asLiveData()
    private val _sisterCards = MutableLiveData<List<Card>>()
    private fun sortCards(list: List<Card>):List<Card>{
        val sorted = mutableListOf<Card>()
        fun getNextCard(cardBefore: Card?){
            val nextList = list.filter { it.cardBefore == cardBefore?.id }
            if(nextList.size==1){
                sorted.addAll(nextList)
                getNextCard(nextList.single())
            } else if(nextList.size>1){
                val cardBeforeDoubledSorted = nextList.sortedBy { it.id }.reversed()
                cardBeforeDoubledSorted.onEach {
                    val nowPos = cardBeforeDoubledSorted.indexOf(it)
                    if(nowPos>0)
                        upDateCardBefore(it,cardBeforeDoubledSorted[nowPos-1].id)
                }
                getNextCard(cardBeforeDoubledSorted.last())
            }
        }
        getNextCard(null)
        return  sorted


    }
    fun setSisterCards(list:List<Card>){
        val sorted = sortCards(list)
        _sisterCards.value = sorted
    }
    private val _startingCardId = MutableLiveData<Int>()
    fun setStartingCardId(int: Int){
        _startingCardId.value = int
    }
    fun returnStartingCardId():Int{
        return _startingCardId.value ?:0
    }
    val sisterCards: LiveData<List<Card>> = _sisterCards
    fun returnSisterCards():List<Card>{
        return _sisterCards.value ?: mutableListOf()
    }
    val lastInsertedCardFromDB:LiveData<Card> = repository.lastInsertedCard.asLiveData()
    private val _lastInsertedCard =  MutableLiveData<Card?>()
    val lastInsertedCard: LiveData<Card?> = _lastInsertedCard
    private fun returnLastInsertedCard():Card?{
        return _lastInsertedCard.value
    }

    fun setLastInsertedCard(card: Card?){
        if(card==returnLastInsertedCard()) return else
            _lastInsertedCard.value = card
    }
    private val _cardStatus = MutableLiveData<CardStatus>()
    fun setCardStatus(cardStatus: CardStatus){
        _cardStatus.value = cardStatus
    }
    private val _saveCard = MutableLiveData<Boolean>()
    private fun setSaveCard(boolean: Boolean){
        _saveCard.value = boolean
    }





    private val _stringData = MutableLiveData<StringData?>()
    val stringData :LiveData<StringData?> =_stringData
    private fun saveEmptyCard(position:Int?,parentFlashCardCoverId:Int?){
        val newCard = Card(
            id = 0,
            cardBefore =  position ,
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
    fun upDateCard(stringData: StringData,card: Card) {
        card.stringData = stringData
        update(card)
    }
    private fun upDateCardBefore(card:Card, newCardBefore:Int){
        card.cardBefore = newCardBefore
        update(card)
    }
    private fun insert(any: Any){
        viewModelScope.launch {
            repository.insert(any)
        }
    }

    fun update(any: Any){
        viewModelScope.launch {
            repository.update(any)
        }
    }

//    navigation


    fun onClickBtnInsert(side: NeighbourCardSide){
        setOpenEditCard(true)
        fun insertToNext(){
            val nextCard = returnSisterCards().find { it.cardBefore == returnParentCard()?.id }
            if(nextCard!=null){
                upDateCardBefore(nextCard,(returnLastInsertedCard()?.id ?:0)+1)
            }
            saveEmptyCard(returnParentCard()?.id,returnParentFlashCardCover()?.fileId)
        }
        fun insertToBefore(){
            val insertingCardsBefore = returnParentCard()?.cardBefore
            saveEmptyCard(insertingCardsBefore,returnParentFlashCardCover()?.fileId)
            upDateCardBefore(returnParentCard()!!,(returnLastInsertedCard()?.id ?:0)+1)
        }
        when(side){
            NeighbourCardSide.NEXT -> insertToNext()
            NeighbourCardSide.PREVIOUS -> insertToBefore()
        }
    }







    fun getNeighbourCardId(side: NeighbourCardSide):Int?{
        val nowPosition = returnSisterCards().indexOf(returnParentCard())
        val newPosition = when(side){
            NeighbourCardSide.PREVIOUS -> nowPosition - 1
            NeighbourCardSide.NEXT -> nowPosition + 1}
        val neighbourCard =  if(newPosition > returnSisterCards().size-1||newPosition<0) null
        else returnSisterCards()[newPosition]
        return neighbourCard?.id

    }
    fun onClickBtnNavigate(side: NeighbourCardSide){
        val flashCardId = returnParentCard()?.belongingFlashCardCoverId
        val a = if(flashCardId!=null) intArrayOf(flashCardId) else null
        val nextCardId = getNeighbourCardId(side)
        if(nextCardId!=null){
           val b = intArrayOf(nextCardId)
           val action = EditCardFragDirections.flipCreateCard()
            action.parentFlashCardCoverId = a
            action.cardId = b
            setEditCardBaseFragNavDirection(action)
       }
    }


    fun onClickAddNewCardBottomBar(){
        returnMainActivityNavCon()?.navigate(
            EditCardBaseFragDirections.openCreateCard())
        setStartingCardId((returnLastInsertedCard()?.id ?:0) + 1)
        setOpenEditCard(true)
        val last = if(returnSisterCards().isEmpty()) null else returnSisterCards().last().id
        saveEmptyCard(last ,returnParentFlashCardCover()?.fileId)

    }
    fun onClickAddNewCardRV(itemBefore:Card){
        returnMainActivityNavCon()?.navigate(EditCardBaseFragDirections.openCreateCard())
        setStartingCardId((returnLastInsertedCard()?.id ?:0)+1)
        val nextCard = returnSisterCards().find { it.cardBefore == itemBefore.id }
        if(nextCard!=null){
            upDateCardBefore(nextCard,(returnLastInsertedCard()?.id ?:0)+1)
        }
        saveEmptyCard(itemBefore.id,returnParentFlashCardCover()?.fileId)


        setOpenEditCard(true)

    }

    fun onClickEditCard(item: Card,navController: NavController){
        setSaveCard(true)
        setSaveCard(false)
        setOpenEditCard(false)
        val a =if(item.belongingFlashCardCoverId== null) null else intArrayOf(item.belongingFlashCardCoverId!!)
        val b = intArrayOf(item.id)
        val action = EditCardFragDirections.flipCreateCard()
        action.cardId= b
        action.parentFlashCardCoverId = a
        navController.popBackStack()
        navController.navigate(action)

    }
    fun doOnBackPress():Boolean{
        return false
    }


}