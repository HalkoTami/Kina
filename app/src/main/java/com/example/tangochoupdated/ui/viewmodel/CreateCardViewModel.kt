package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.dataclass.StringData
import com.example.tangochoupdated.db.enumclass.CardStatus
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.rvclasses.LibraryRV
import com.example.tangochoupdated.db.enumclass.Mode
import com.example.tangochoupdated.ui.fragment.base_frag_con.CreateCardFragmentBaseDirections
import com.example.tangochoupdated.ui.fragment.createCard_frag_com.CreateCardFragmentDirections
import kotlinx.coroutines.launch


class CreateCardViewModel(private val repository: MyRoomRepository) :ViewModel(){


    private val _openEditCard = MutableLiveData<Boolean>()
    fun setOpenEditCard (boolean: Boolean){
        _openEditCard.value = boolean
    }
    fun returnOpenEditCard ():Boolean{
        return _openEditCard.value ?:false
    }

    fun getParentFlashCardCover(fileId:Int?):LiveData<File?> = repository.getFileByFileId(fileId).asLiveData()
    private val _parentFlashCardCover = MutableLiveData<File?>()
    fun setParentFlashCardCover(file: File?){
        _parentFlashCardCover.value = file
    }
    private fun returnParentFlashCardCover():File?{
        return _parentFlashCardCover.value
    }

//    parent card
    fun getParentCard(cardId: Int?):LiveData<Card> = repository.getCardByCardId(cardId).asLiveData()
    private val _parentCard = MutableLiveData<Card>()
    fun setParentCard(card: Card){
        _parentCard.value = card
    }
    fun returnParentCard():Card?{
        return _parentCard.value
    }
    val parentCard :LiveData<Card> = _parentCard

    fun getSisterCards(fileId: Int?):LiveData<List<Card>?> = repository.getCardDataByFileId(fileId).asLiveData()
    private val _sisterCards = MutableLiveData<List<Card>?>()
    fun setSisterCards(list:List<Card>?){
        _sisterCards.value = list
    }
    fun returnSisterCards():List<Card>{
        return _sisterCards.value ?: mutableListOf()
    }
    val lastInsertedCard :LiveData<Card> = repository.lastInsertedCard.asLiveData()

    private val _cardStatus = MutableLiveData<CardStatus>()
    val cardStatus :LiveData<CardStatus> = _cardStatus
    fun setCardStatus(cardStatus: CardStatus){
        _cardStatus.value = cardStatus
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
//    navigation

    fun onClickBtnInsert(side:NeighbourCardSide){
        setOpenEditCard(true)
        val orderNow= returnParentCard()?.libOrder ?:0
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
        val neighbourCard = returnSisterCards().find { it.libOrder == when(side){
            NeighbourCardSide.PREVIOUS -> (returnParentCard()?.libOrder ?: -2) - 1
            NeighbourCardSide.NEXT -> (returnParentCard()?.libOrder ?: -2) + 1
        } }
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
    fun onClickAddNewCardBottomBar(mainNavCon:NavController){
        setOpenEditCard(true)
        saveEmptyCard(returnSisterCards().size+1,returnParentFlashCardCover()?.fileId)
        mainNavCon.navigate(CreateCardFragmentBaseDirections.openCreateCard())

    }

    fun onClickEditCard(item: Card,navController: NavController){
        setOpenEditCard(false)
        val a =if(item.belongingFlashCardCoverId== null) null else intArrayOf(item.belongingFlashCardCoverId!!)
        val b = intArrayOf(item.id)
        val action = CreateCardFragmentDirections.flipCreateCard()
        action.cardId= b
        action.parentFlashCardCoverId = a
        navController.navigate(action)

    }


}