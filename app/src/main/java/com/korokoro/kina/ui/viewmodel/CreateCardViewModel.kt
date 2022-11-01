package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.korokoro.kina.actions.SortActions
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.dataclass.StringData
import com.korokoro.kina.db.enumclass.CardStatus
import com.korokoro.kina.db.enumclass.ColorStatus
import com.korokoro.kina.ui.fragment.base_frag_con.EditCardBaseFragDirections
import com.korokoro.kina.ui.fragment.createCard_frag_com.EditCardFragDirections
import com.korokoro.kina.customClasses.AnkiFragments
import com.korokoro.kina.customClasses.ColorPalletStatus
import com.korokoro.kina.customClasses.MainFragment
import com.korokoro.kina.customClasses.NeighbourCardSide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class CreateCardViewModel(private val repository: MyRoomRepository) :ViewModel(){




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

    fun getParentFlashCardCover(fileId:Int?):LiveData<File?> = repository.getFileByFileId(fileId).asLiveData()
    private val _parentFlashCardCover = MutableLiveData<File?>()
    fun setParentFlashCardCover(file: File?){
        _parentFlashCardCover.value = file
    }
    fun onClickEditCardFromRV(card: Card,){
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
    fun sortCards(list: List<Card>):List<Card>{
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
    private val _confirmFlipItemPopUpVisible = MutableLiveData<Boolean>()
    val confirmFlipItemPopUpVisible :LiveData<Boolean> = _confirmFlipItemPopUpVisible
    fun setConfirmFlipItemPopUpVisible(boolean: Boolean){
        _confirmFlipItemPopUpVisible.value = boolean
    }


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
    private fun setColPalletVisibility(boolean: Boolean){
        _colPalletVisibility.value = boolean
    }
    fun changeColPalletVisibility(){
        setColPalletVisibility( _colPalletVisibility.value?.not() ?:false)
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
    fun upDateCard(stringData: StringData,card: Card){
        val upDating = card
        upDating.stringData = stringData
        update(upDating)
    }
    fun upDateCardBefore(card:Card,newCardBefore:Int){
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
    fun checkMakePopUpVisible(mainFragmentStatus: MainViewModel.MainActivityChildFragmentStatus, ankiActiveFrag: AnkiFragments){
    if(mainFragmentStatus.before== MainFragment.Anki&&
        ankiActiveFrag == AnkiFragments.Flip){
        setConfirmFlipItemPopUpVisible(true)
    }
    }

    fun onClickBtnInsert(side: NeighbourCardSide,){
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
    fun onClickBtnNavigate(navController: NavController,side: NeighbourCardSide){
        val flashCardId = returnParentCard()?.belongingFlashCardCoverId
        val a = if(flashCardId!=null) intArrayOf(flashCardId) else null
        val nextCardId = getNeighbourCardId(side)
        if(nextCardId!=null){
           val b = intArrayOf(nextCardId)
           val action = EditCardFragDirections.flipCreateCard()
            action.parentFlashCardCoverId = a
            action.cardId = b
            navController.popBackStack()
           navController.navigate(action)
       }
    }

    fun onClickRVAddNewCard(item:Card,mainNavCon:NavController){
        setOpenEditCard(true)
        saveEmptyCard(item.cardBefore ?:0+ 1,item.belongingFlashCardCoverId,)
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


}