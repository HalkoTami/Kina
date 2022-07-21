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
import com.example.tangochoupdated.ui.library.HomeFragmentDirections
import kotlinx.coroutines.launch


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



    class CreateCardNav(
        val action: NavDirections,
        var fromSameFrag:Boolean,
//        var updateDone :Boolean
    )

    private val _action = MutableLiveData<CreateCardNav>()
    private fun setAction (createCardNav: CreateCardNav){
        val before = _action.value
        if(before == null){
            createCardNav.fromSameFrag = false
        }
        _action.value = createCardNav
    }
    val action :LiveData<CreateCardNav> = _action

    private val _fromSameFrag = MutableLiveData<Boolean>()
    private fun setFromSameFrag (boolean: Boolean){
        _fromSameFrag.value = boolean
    }
    val fromSameFrag :LiveData<Boolean> = _fromSameFrag


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

    private val _parentFlashCardCoverId = MutableLiveData<Int?>()
    fun setParentFlashCardCoverId(int: Int?){
        _parentFlashCardCoverId.value = int

    }
    val parentFlashCardCoverId:LiveData<Int?> =_parentFlashCardCoverId




//    parent card
    fun getParentCard(cardId: Int?):LiveData<CardAndTags?> = repository.getCardByCardId(cardId).asLiveData()
    private val _parentCard = MutableLiveData<CardAndTags>()
    fun setParentCard(card: CardAndTags){
        val before = _parentCard.value

        _parentCard.value = card
//        setPosition(card.card.libOrder)
        val nowCard = _sisterCards.value!!.find { it.card.id == _parentCard.value!!.card.id }
        val nowPosition = _sisterCards.value!!.indexOf(nowCard)
        setPosition(nowPosition)
        setTxvPositionText("${card.card.libOrder + 1}/ ${_sisterCards.value?.size}")
        if(before?.card?.id == card.card.id && before.card != card.card ){
            setUpdateCompleted(true)
        }

    //        card?.card?.apply {
//            if(markerData == null && quizData == null && stringData == null){
//                setMode(Mode.Create)
//            } else setMode(Mode.Edit)
//        }

    }
    val parentCard :LiveData<CardAndTags?> = _parentCard

//    parent sister cards
    fun getSisterCards(fileId: Int?):LiveData<List<CardAndTags>?> = repository.getCardDataByFileId(fileId).asLiveData()
    private val _sisterCards = MutableLiveData<List<CardAndTags>?>()
    fun setSisterCards(list:List<CardAndTags>?){
        _sisterCards.value = list

        setTxvPositionText("${(_parentCard.value?.card?.libOrder ?:0) + 1}/ ${list?.size}")
    }
    val sisterCards:LiveData<List<CardAndTags>?> =_sisterCards
//    fun filterAndSetParentCard(cardId:Int?){
//        val a:CardAndTags? = _sisterCards.value?.find { it.card.id == cardId }
//        setParentCard(a)
//    }
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

    val lastInsertedCardAndTags :LiveData<Card?> = repository.lastInsertedCard.asLiveData()
    private val _lastInsertedCardAndTags = MutableLiveData<Card>()
    fun setLastInsertedCardAndTags(card: Card?){
        val before = _lastInsertedCardAndTags.value?.id
        if(before != null &&card != null && before != card.id ) {
            _lastInsertedCardAndTags.value = card
            val a = intArrayOf(card.belongingFileId!!)
            val b = intArrayOf(card.id)
            val fromSameFrag = _fromSameFrag.value!!
            setAction(CreateCardNav(HomeFragmentDirections.toCreateCard(a,b),fromSameFrag))
//            setMode(Mode.Edit)
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
                Mode.New -> setCardColor(ColorStatus.GRAY)

                else -> return
            }
        }

    }
    val mode:LiveData<Mode> = _mode
    private val _position = MutableLiveData<Int>()
    private fun setPosition( int: Int){
        _position.value = int
        setTxvPositionText("${int + 1}/ ${_sisterCards.value?.size}")


    }
    val position :LiveData<Int> =_position

    private val _txvPositionText = MutableLiveData<String>()
    fun setTxvPositionText(string: String){
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
        _getStringData.value = boolean

    }


    private val _createCardFragActive = MutableLiveData<Boolean>()
    val createCardFragActive :LiveData<Boolean> = _createCardFragActive
    private fun setCreateFragActive(boolean: Boolean){
        _createCardFragActive.value = boolean
    }


    private val _stringData = MutableLiveData<StringData?>()

    fun setStringData(stringData: StringData?){
        _stringData.value = stringData
//        val update =_parentCard.value!!
//        update.card.stringData = stringData
//        update(update)
        upDateCard()
//        upDateCard()
//        when(_mode.value){
//            Mode.Create -> saveCard()
//            Mode.Edit -> upDateCard()
//            else -> throw  IllegalArgumentException()
//        }
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
            belongingFileId = parentFlashCardCoverId,
            remembered = false
        )
        insert(newCard)
    }
    fun upDateCard(){
        val upDating = _parentCard.value!!.card
        upDating.stringData = _stringData.value
        update(upDating)
        setGetStringData(false)
    }
// db insert & update

    private val _updateCompleted = MutableLiveData<Boolean>()
    val upDateCompleted :LiveData<Boolean> = _updateCompleted
    private fun setUpdateCompleted (boolean: Boolean){
        _updateCompleted.value = boolean
    }

    private fun insert(any: Any){
        viewModelScope.launch {
            repository.insert(any)
        }

    }
//    fun confirmUpdate(){
//        val update = _action.value!!
//        update.updateDone = true
//        setAction(update)
//    }
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
//    private fun activateCreateCard(){
//        val parentFileId = try {
//            intArrayOf(_parentFlashCardCover.value!!.fileId)
//        } catch (e:Exception) { null }
//        val cardId = try {
//            intArrayOf(_parentCardId.value!!)
//        } catch (e:Exception){ null }
//        val action = CreateCardFragmentDirections.toCreateCard(parentFileId,cardId)
//        setAction(action)
//    }

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
    setFromSameFrag(true)
    val a = intArrayOf(_parentCard.value!!.card.belongingFileId!!)
    val now = _sisterCards.value!!.find { it.card.id == _parentCard.value!!.card.id }
    val nowPosition = _sisterCards.value!!.indexOf(now)

    if(nowPosition < _sisterCards.value!!.size -1  ){

        val nextCardId =_sisterCards.value?.get(nowPosition+1)!!.card.id
        val b = intArrayOf(nextCardId)
        setAction(CreateCardNav(CreateCardFragmentDirections.toCreateCard(a,b),true,
//            updateDone = false
        ) )
    } else createNewCardNextToPosition(nowPosition  ,false,a.single())
    setGetStringData(true)

    }
    fun onClickBtnPrevious(){
        setGetStringData(true)
        setFromSameFrag(true)
        val a = intArrayOf(_parentCard.value!!.card.belongingFileId!!)
        val nowCard = _sisterCards.value!!.find { it.card.id == _parentCard.value!!.card.id }
        val nowPosition = _sisterCards.value!!.indexOf(nowCard)
        if(nowPosition == 0 ){
            createNewCardNextToPosition(nowPosition,previous = true,a.single())


        } else{
            val nextCardId =_sisterCards.value?.get(nowPosition-1)!!.card.id
            val b = intArrayOf(nextCardId)
            setAction(CreateCardNav(CreateCardFragmentDirections.toCreateCard(a,b),true) )
        }
    }


    private fun createNewCardNextToPosition(position: Int,previous:Boolean,parentFlashCardCoverId:Int?){
        setMode(Mode.New)
        val myPosition:Int = if(previous) position  else position +1
        setPosition(myPosition)
        setParentFlashCardCoverId(parentFlashCardCoverId)
        if (previous) updateSistersPosition(myPosition) else updateSistersPosition(position+2)
        saveEmptyCard(myPosition,parentFlashCardCoverId)




    }


    fun onClickRVAddNewCard(item:LibraryRV){
//        val parentFileId = if(item.card?.belongingFileId==null){
//            null
//        } else intArrayOf(item.card.belongingFileId)
//
//        setAction(CreateCardFragmentDirections.toCreateCard(parentFileId,null))
        setFromSameFrag(false)
        createNewCardNextToPosition(item.card!!.libOrder,false,item.card.belongingFileId)
    }
    fun onClickAddNewCardBottomBar(){
        val parentFile = intArrayOf(_parentFlashCardCoverId.value!!)
//        setAction(CreateCardFragmentDirections.toCreateCard(parentFile,null))
        setFromSameFrag(false)
        createNewCardNextToPosition((_sisterCards.value?.size ?:0)  ,false,_parentFlashCardCoverId.value!!)


    }

    fun onClickSaveAndBack(){
        setGetStringData(true)
//        setCreateFragActive(false)
    }
    fun onClickBack(){
        setGetStringData(true)
    }

    enum class CursorPosition{
        Tag, FrontTitle, FrontContent, BackTitle, BackText
    }
    fun onClickEditCard(item: LibraryRV){
        val a = intArrayOf(item.card!!.belongingFileId!!)
        val b = intArrayOf(item.card.id)
        setFromSameFrag(false)
        setAction(CreateCardNav( HomeFragmentDirections.toCreateCard(a,b),false))
        setMode(Mode.Edit)



    }








}