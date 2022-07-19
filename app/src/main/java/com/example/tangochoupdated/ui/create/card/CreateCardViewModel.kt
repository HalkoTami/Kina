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
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.Mode
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.CookieHandler
import java.text.FieldPosition


class CreateCardViewModel(private val repository: MyRoomRepository) :ViewModel(){

    fun onStart(){
        setSavingCard(false)
        setColPalletVisibility(false)
        setCardStatus(CardStatus.STRING)
        setCardColor(ColorStatus.GRAY)
//        setGetStringData(false)
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

        if(file?.fileStatus == FileStatus.TANGO_CHO_COVER){
            setHasParentFlashCarCover(true)
        } else setHasParentFlashCarCover(false)

    }
    val parentFlashCardCover:LiveData<File?> =_parentFlashCardCover

    private val _parentCardId = MutableLiveData<Int?>()
    fun setParentCardId(int: Int?){
        _parentCardId.value = int

    }
    val parentCardId:LiveData<Int?> =_parentCardId



//    parent card
    fun getParentCard(cardId: Int?):LiveData<CardAndTags?> = repository.getCardByCardId(cardId).asLiveData()
    private val _parentCard = MutableLiveData<Card>()
    fun setParentCard(card: Card?){
        _parentCard.value = card
        setTxvPositionText("${card?.libOrder}/ ${_sisterCards.value?.size}")
        if(card == null) {
            setMode(Mode.Create)
        } else {
            setMode(Mode.Edit)
        }

    }
    val parentCard :LiveData<Card?> = _parentCard

//    parent sister cards
    fun getSisterCards(fileId: Int?):LiveData<List<CardAndTags>?> = repository.getCardDataByFileId(fileId).asLiveData()
    private val _sisterCards = MutableLiveData<List<CardAndTags>?>()
    fun setSisterCards(list:List<CardAndTags>?){
        _sisterCards.value = list
//        setTxvPositionText("${_position.value}/ ${list?.size}")
    }
    val sisterCards:LiveData<List<CardAndTags>?> =_sisterCards
    fun filterAndSetParentCard(cardId:Int?){
        val a:CardAndTags? = _sisterCards.value?.find { it.card.id == cardId }
        setParentCard(a?.card)
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
        val before = _position.value
        if(before != int){
            _position.value = int
//            setTxvPositionText("${int}/ ${_sisterCards.value?.size ?: 1}")
        }

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
//
    private val _getStringData = MutableLiveData<Boolean>()
    val getStringData: LiveData<Boolean> = _getStringData
    private fun setGetStringData (boolean: Boolean){
        val before =_getStringData.value
        if(before != boolean){
            _getStringData.value = boolean
        } else return

    }


    private val _createCardFragActive = MutableLiveData<Boolean>()
    val createCardFragActive :LiveData<Boolean> = _createCardFragActive
    private fun setCreateFragActive(boolean: Boolean){
        _createCardFragActive.value = boolean
    }


    private val _stringData = MutableLiveData<StringData?>()

    fun setStringData(stringData: StringData?){
        _stringData.value = stringData
        when(_mode.value){
            Mode.Create -> saveCard()
            Mode.Edit -> upDateCard()
            else -> throw  IllegalArgumentException()
        }
//        setGetStringData(false)


    }
    val stringData :LiveData<StringData?> =_stringData
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
        insert(newCard)
    }
    fun upDateCard(){
        val upDating = _parentCard.value!!
        upDating.stringData = _stringData.value
        update(upDating)
    }

    private fun insert(any: Any){
        viewModelScope.launch {
            repository.insert(any)
        }
        setSavingCard(false)

    }
    private fun update(any: Any){
        viewModelScope.launch {
            repository.update(any)
        }

    }
    private fun upDateMultipleCards(list: List<Card>){
        viewModelScope.launch {
            repository.updateMultiple(list)
        }
    }
    private fun activateCreateCard(){
        val parentFileId = try {
            intArrayOf(_parentFlashCardCover.value!!.fileId)
        } catch (e:Exception) { null }
        val cardId = try {
            intArrayOf(_parentCardId.value!!)
        } catch (e:Exception){ null }
        val action = CreateCardFragmentDirections.toCreateCard(parentFileId,cardId)
        setAction(action)
    }

// onClickEvents

//   fragment create card

//   color palet
    fun onClickColPaletIcon(){
        setColPalletVisibility(_colPalletVisibility.value!!.not())
    }
    fun onClickEachColor(colorStatus: ColorStatus){
        setCardColor(colorStatus)

    }

//    navigation
    fun onClickBtnNext(){
       val nextCard = _sisterCards.value?.find { it.card.libOrder == _parentCard.value!!.libOrder + 1}
        setParentCard(nextCard?.card)


    }


    fun onClickAddNewCardByPosition(item:LibraryRV){
        setMode(Mode.Create)
        setParentCardId(null)
        setParentCard(null)
        setPosition(item.position +2)
        updateSistersPosition(item.position +2)
        activateCreateCard()

    }
    fun onClickAddNewCardBottomBar(){
        setMode(Mode.Create)
        setParentCardId(null)
        setParentCard(null)
        setPosition((_sisterCards.value!!.size) +1)
        activateCreateCard()

    }

    fun onClickSaveAndBack(){
        setGetStringData(true)
//        setCreateFragActive(false)
    }
    enum class CursorPosition{
        Tag, FrontTitle, FrontContent, BackTitle, BackText
    }
    fun onClickEditCard(item: LibraryRV){
        setMode(Mode.Edit)
        setParentCardId(item.card!!.id)
        setParentCard(item.card)
        setTxvPositionText("${item.card.libOrder}/ ${_sisterCards.value?.size ?: 1}")
        activateCreateCard()

    }








}