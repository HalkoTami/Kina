package com.example.tangochoupdated.ui.create.card

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.StringData
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.Mode
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import kotlinx.coroutines.launch
import java.net.CookieHandler
import java.text.FieldPosition


class CreateCardViewModel(private val repository: MyRoomRepository) :ViewModel(){

    fun onStart(){
        setSavingCard(false)
        setColPalletVisibility(false)
        setCardStatus(CardStatus.STRING)
//        val newCard = Card(
//            id = 0,
//            libOrder = 0,
//            deleted = false,
//            colorStatus = ColorStatus.YELLOW,
//            cardStatus = CardStatus.STRING!!,
//            quizData = null,
//            markerData =  null,
//            stringData = StringData("d", "a","b","c"),
//            belongingFileId = 2,
//            remembered = false
//        )
//        insertCard(newCard)
        _mode
    }



    private val _action = MutableLiveData<NavDirections>()
    private fun setAction (navDirections: NavDirections){
        _action.value = navDirections
    }
    val action :LiveData<NavDirections> = _action


//    DBからとってくる
//    parent tangocho
    fun getParentFlashCardCover(fileId:Int?):LiveData<File?> = repository.getFileByFileId(fileId).asLiveData()
    private val _parentFlashCardCover = MutableLiveData<File?>()
    fun setParentFlashCardCover(file: File?){
        _parentFlashCardCover.value = file
        when(file){
            null -> setHasParentFlashCarCover(false)
            else -> setHasParentFlashCarCover(true)
        }
    }
    val parentFlashCardCover:LiveData<File?> =_parentFlashCardCover

    private val _parentCardId = MutableLiveData<Int?>()
    fun setParentCardId(int: Int?){
        _parentCardId.value = int

    }
    val parentCardId:LiveData<Int?> =_parentCardId

//    parent card
    private val _parentCard = MutableLiveData<CardAndTags?>()
    fun setParentCard(card: CardAndTags?){
        _parentCard.value = card
        if(card == null) {
            setMode(Mode.Create)
        } else {
            setMode(Mode.Edit)
        }
    }
    val parentCard :LiveData<CardAndTags?> = _parentCard

//    parent sister cards
    fun getSisterCards(fileId: Int?):LiveData<List<CardAndTags>?> = repository.getCardDataByFileId(fileId).asLiveData()
    private val _sisterCards = MutableLiveData<List<CardAndTags>?>()
    fun setSisterCards(list:List<CardAndTags>?){
        _sisterCards.value = list
    }
    val sisterCards:LiveData<List<CardAndTags>?> =_sisterCards
    fun filterAndSetParentCard(cardId:Int?){
        val a:CardAndTags? = _sisterCards.value?.find { it.card.id == cardId }
        setParentCard(a)
    }
    fun updateSistersPosition(position: Int){
        val updateSisters = mutableListOf<Card>()
        _sisterCards.value?.onEach {
            if(it.card.libOrder >= position){
                it.card.libOrder +=1
                updateSisters.add(it.card)
            }
        }
        upDateMultipleCards(updateSisters)
    }

    private val _hasParentFlashCardCover = MutableLiveData<Boolean>()
    private fun setHasParentFlashCarCover (boolean: Boolean){
        _hasParentFlashCardCover.value = boolean
    }

    private val _mode = MutableLiveData<Mode>()
    private fun setMode(mode:Mode){
        val before = _mode.value
        if(before == mode) return else{
            _mode.value = mode
            when (mode){
                Mode.Create -> setCardColor(ColorStatus.GRAY)

                else -> return
            }
        }

    }
    val mode:LiveData<Mode> = _mode
    private val _position = MutableLiveData<Int>()
    private fun setPosition( int: Int){
        _position.value = int
    }
    val position :LiveData<Int> =_position

    private val _txvPositionText = MutableLiveData<String>()
    private fun setTxvPositionText(string: String){
        _txvPositionText.value = string
    }
    val txvPositionText :LiveData<String> =_txvPositionText

//    card status
    private val _cardStatus = MutableLiveData<CardStatus>()
    val cardStatus :LiveData<CardStatus> = _cardStatus

    fun setCardStatus(cardStatus: CardStatus){
        _cardStatus.value = cardStatus
    }

//    card color attributes

    private val _cardColor = MutableLiveData<ColorStatus>()
    val cardColor: LiveData<ColorStatus> = _cardColor
    private fun setCardColor(colorStatus: ColorStatus){
        val previous = _cardColor.value
        if(previous == colorStatus){
            return
        } else{
            _cardColor.value = colorStatus
        }


    }

    private val _colPalletVisibility = MutableLiveData<Boolean>()
    val colPalletVisibility: LiveData<Boolean> = _colPalletVisibility
    private fun setColPalletVisibility(boolean: Boolean){
        _colPalletVisibility.value = boolean
    }

    private val _savingCard = MutableLiveData<Boolean>()
    val savingCard : LiveData<Boolean> = _savingCard
    fun setSavingCard (boolean: Boolean){
        _savingCard.value = boolean
    }

    private val _getStringData = MutableLiveData<Boolean>()
    val getStringData: LiveData<Boolean> = _getStringData
    private fun setGetStringData (boolean: Boolean){
        _getStringData.value = boolean
        if(_savingCard.value == true){
            saveCard()
        }
    }


    private val _createCardFragActive = MutableLiveData<Boolean>()
    val createCardFragActive :LiveData<Boolean> = _createCardFragActive
    private fun setCreateFragActive(boolean: Boolean){
        _createCardFragActive.value = boolean
    }


    private val _stringData = MutableLiveData<StringData>()

    fun setStringData(stringData: StringData){
        _stringData.value = stringData
        when(_savingCard.value){
            true -> saveCard()
            else -> return
        }


    }
    fun saveCard(){
        val newCard = Card(
            id = 0,
            libOrder =  _position.value!!,
            deleted = false,
            colorStatus = _cardColor.value,
            cardStatus = _cardStatus.value!!,
            quizData = if(_cardStatus.value!=CardStatus.CHOICE) null else null,
            markerData = if(_cardStatus.value!=CardStatus.CHOICE) null else null,
            stringData = if(_cardStatus.value!=CardStatus.STRING) null else _stringData.value,
            belongingFileId = _parentFlashCardCover.value?.fileId,
            remembered = false
        )
        insertCard(newCard)
    }

    private fun insertCard(card: Card){
        viewModelScope.launch {
            repository.insert(card)
        }
        setSavingCard(false)

    }
    private fun upDateMultipleCards(list: List<Card>){
        viewModelScope.launch {
            repository.updateMultiple(list)
        }
    }

// onClickEvents
    fun onClickColPaletIcon(){
        setColPalletVisibility(_colPalletVisibility.value!!.not())
    }
    fun onClickEachColor(colorStatus: ColorStatus){
        setCardColor(colorStatus)

    }
    fun onClickAddNewCardByPosition(item:LibraryRV){
        setMode(Mode.Create)
        setParentCard(null)
        setPosition(item.position+1)
        updateSistersPosition(item.position+1 )

    }
    fun onClickAddNewCardBottomBar(){
        setMode(Mode.Create)

    }

    fun onClickSaveAndBack(){
        setSavingCard(true)
        setGetStringData(true)
//        setCreateFragActive(false)
    }








}