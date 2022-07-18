package com.example.tangochoupdated.ui.create.card

import androidx.lifecycle.*
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.StringData
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.ui.create.Mode
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import kotlinx.coroutines.launch
import java.net.CookieHandler


class CreateCardViewModel(private val repository: MyRoomRepository) :ViewModel(){

    fun onStart(){
        setSavingCard(false)
        setColPalletVisibility(false)
        setCardStatus(CardStatus.STRING)
        val newCard = Card(
            id = 0,
            libOrder = 0,
            deleted = false,
            colorStatus = ColorStatus.YELLOW,
            cardStatus = CardStatus.STRING!!,
            quizData = null,
            markerData =  null,
            stringData = StringData("d", "a","b","c"),
            belongingFileId = 2,
            remembered = false
        )
        insertCard(newCard)
    }





//    DBからとってくる
    fun getParentFlashCardCover(fileId:Int?):LiveData<File?> = repository.getFileByFileId(fileId).asLiveData()
    private val _parentFlashCardCover = MutableLiveData<File?>()
    fun setParentFlashCardCover(file: File?){
        _parentFlashCardCover.value = file
        when(file){
            null -> setHasParentFlashCarCover(false)
            else -> setHasParentFlashCarCover(true)
        }
    }

    fun  getParentCard(cardId:Int?): LiveData<Card?> = repository.getCardByCardId(cardId).asLiveData()
    private val _parentCard = MutableLiveData<Card?>()
    fun setParentCard(card: Card?){
        _parentCard.value = card
        when(card){
            null -> setMode(Mode.Create)
            else -> {
                setMode(Mode.Edit)
                setCardColor(card.colorStatus!!)
            }
        }

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
            libOrder = 0,
            deleted = false,
            colorStatus = _cardColor.value,
            cardStatus = _cardStatus.value!!,
            quizData = if(_cardStatus.value!=CardStatus.CHOICE) null else null,
            markerData = if(_cardStatus.value!=CardStatus.CHOICE) null else null,
            stringData = if(_cardStatus.value!=CardStatus.STRING) null else _stringData.value,
            belongingFileId = _parentFlashCardCover.value!!.fileId,
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

// onClickEvents
    fun onClickColPaletIcon(){
        setColPalletVisibility(_colPalletVisibility.value!!.not())
    }
    fun onClickEachColor(colorStatus: ColorStatus){
        setCardColor(colorStatus)

    }

    fun onClickSaveAndBack(){
        setSavingCard(true)
        setCreateFragActive(false)
    }








}