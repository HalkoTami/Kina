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
import com.example.tangochoupdated.ui.viewmodel.customClasses.Mode
import com.example.tangochoupdated.ui.fragment.createCard_frag_com.CreateCardFragmentDirections
import kotlinx.coroutines.launch


class EditCardViewModel(private val repository: MyRoomRepository) :ViewModel(){

    fun onCreateViewModel(){
        setFromSameFrag(false)
        setOpenCreateCard(false)
    }
    fun onStartFrag(){
        setSavingCard(false)


        setOpenCreateCard(true)
        setColPalletVisibility(true)
        setCardStatus(CardStatus.STRING)
        setCardColor(ColorStatus.GRAY)
    }
    fun onEndFrag(){

    }



    private val _openEditCard = MutableLiveData<Boolean>()
    fun setOpenCreateCard (boolean: Boolean){
        _openEditCard.value = boolean
    }
    val openCreateCard :LiveData<Boolean> = _openEditCard
    fun returnOpenCreateCard ():Boolean{
        return _openEditCard.value ?:false
    }

    class CreateCardNav(
        val action: NavDirections,
        var fromSameFrag:Boolean,
    )

//    private val _action = MutableLiveData<CreateCardNav>()
//    private fun setAction (createCardNav: CreateCardNav){
//        if(_openCreateCard.value == true){
//            val before = _action.value
//            if(before == null){
//                createCardNav.fromSameFrag = false
//            }
//            _action.value = createCardNav
//        } else return
//
//    }
//    val action :LiveData<CreateCardNav> = _action

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
        setParentFlashCardCoverId(file?.fileId)
        if(file?.fileStatus == FileStatus.FLASHCARD_COVER){
            setHasParentFlashCarCover(true)
        } else setHasParentFlashCarCover(false)

    }
//    val parentFlashCardCover:LiveData<File?> =_parentFlashCardCover


//    private val _parentCardId = MutableLiveData<Int?>()
//    fun setParentCardId(int: Int?){
//        _parentCardId.value = int
//
//    }
//    val parentCardId:LiveData<Int?> =_parentCardId

    private val _parentFlashCardCoverId = MutableLiveData<Int?>()
    fun setParentFlashCardCoverId(int: Int?){
        _parentFlashCardCoverId.value = int
    }
//    val parentFlashCardCoverId:LiveData<Int?> =_parentFlashCardCoverId




//    parent card
    fun getParentCard(cardId: Int?):LiveData<Card?> = repository.getCardByCardId(cardId).asLiveData()
    private val _parentCard = MutableLiveData<Card>()
    fun setParentCard(card: Card){
        val before = _parentCard.value
        _parentCard.value = card
        if(before?.id != card.id ){
            val nowCard = _sisterCards.value!!.find { it.id == card.id }
            val nowPosition = _sisterCards.value!!.indexOf(nowCard)
            setPosition(nowPosition)
        }
        if(before?.id == card.id && before != card ){
            setUpdateCompleted(true)
        }

    //        card?.card?.apply {
//            if(markerData == null && quizData == null && stringData == null){
//                setMode(Mode.Create)
//            } else setMode(Mode.Edit)
//        }

    }
    val parentCard :LiveData<Card?> = _parentCard

//    parent sister cards
    fun getSisterCards(fileId: Int?):LiveData<List<Card>?> = repository.getCardDataByFileId(fileId).asLiveData()
    private val _sisterCards = MutableLiveData<List<Card>?>()
    fun setSisterCards(list:List<Card>?){
        val before = _sisterCards.value
        _sisterCards.value = list
        if(before!=null && list != null && before.size > list.size){
            setOpenCreateCard(false)
            var a = 0
            while(a < list.size){
                val item = list[a]
                item.libOrder = a
                update(item)
                a++
            }
        }
        val now = list!!.find { it.id == _parentCard.value?.id }
        val nowPosition = list.indexOf(now)
        setPosition(nowPosition)
    }
    val sisterCards:LiveData<List<Card>?> =_sisterCards
//    fun filterAndSetParentCard(cardId:Int?){
//        val a:CardAndTags? = _sisterCards.value?.find { it.card.id == cardId }
//        setParentCard(a)
//    }
    fun updateSistersPosition(position: Int){
        val updateSisters = mutableListOf<Card>()
        _sisterCards.value?.onEach {
            if(it.libOrder >= position){
                it.libOrder +=1
                updateSisters.add(it)
            }
        }
        upDateMultipleCards(updateSisters)
    }

//    val lastInsertedCard :LiveData<Card> = repository.lastInsertedCard.asLiveData()
//    private val _lastInsertedCard = MutableLiveData<Card>()
//    fun setLastInsertedCard(card: Card,navController: NavController){
//        val before = _lastInsertedCard.value
//        if((before?.id != card?.id) ) {
//            _lastInsertedCard.value = card
//
//            val a = if(_lastInsertedCard.value?.belongingFlashCardCoverId!=null)intArrayOf(_lastInsertedCard.value?.belongingFlashCardCoverId!!) else null
//            val b = intArrayOf(card!!.id)
//            val fromSameFrag = _fromSameFrag.value!!
//            val action = CreateCardFragmentBaseDirections.openCreateCard()
//            navController.navigate(action)
////            setMode(Mode.Edit)
//        }
//
//    }


    private val _hasParentFlashCardCover = MutableLiveData<Boolean>()
    private fun setHasParentFlashCarCover (boolean: Boolean){
        _hasParentFlashCardCover.value = boolean
    }

    private val _mode = MutableLiveData<Mode>()
    private fun setMode(mode: Mode){
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

//    position
    private val _position = MutableLiveData<Int>()
    private fun setPosition( int: Int){
        _position.value = int
        if(int < _sisterCards.value!!.size -1  ){
            setNextCardExists(true)
        } else setNextCardExists(false)
        if(int == 0 ){
            setPreviousCardExists(false)
        } else{
            setPreviousCardExists(true)
        }
        setTxvPositionText("${int + 1}/ ${_sisterCards.value?.size}")


    }
    val position :LiveData<Int> =_position

    private val _nextCardExists = MutableLiveData<Boolean>()
    val nextCardExists : LiveData<Boolean> = _nextCardExists
    fun setNextCardExists (boolean: Boolean){
        _nextCardExists.value = boolean
    }

    private val _previousCardExists = MutableLiveData<Boolean>()
    val previousCardExists : LiveData<Boolean> = _previousCardExists
    fun setPreviousCardExists (boolean: Boolean){
        _previousCardExists.value = boolean
    }


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
            colorStatus = _cardColor.value ?:ColorStatus.GRAY,
            cardStatus = _cardStatus.value!!,
            quizData = if(_cardStatus.value!=CardStatus.CHOICE) null else null,
            markerData = if(_cardStatus.value!=CardStatus.CHOICE) null else null,
            stringData = if(_cardStatus.value!=CardStatus.STRING) null else _stringData.value,
            belongingFlashCardCoverId = _parentFlashCardCover.value?.fileId,
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
            belongingFlashCardCoverId = parentFlashCardCoverId,
            remembered = false
        )
        insert(newCard)
    }
    fun upDateCard(){
        val upDating = _parentCard.value
        upDating?.stringData = _stringData.value
        if(upDating!=null){
            update(upDating)
        }
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

    fun onClickBtnInsertPrevious(){

        setOpenCreateCard(true)
        setFromSameFrag(true)
        setGetStringData(true)
        val a = intArrayOf(_parentCard.value!!.belongingFlashCardCoverId!!)
        val nowCard = _sisterCards.value!!.find { it.id == _parentCard.value!!.id }
        val nowPosition = _sisterCards.value!!.indexOf(nowCard)
        createNewCardNextToPosition(nowPosition,previous = true,a.single(),true)
    }

    fun onClickBtnInsertNext(){
        setOpenCreateCard(true)
        setFromSameFrag(true)
        setGetStringData(true)
        val a = if (_parentCard.value!!.belongingFlashCardCoverId!=null) intArrayOf(_parentCard.value!!.belongingFlashCardCoverId!!) else null
        val nowCard = _sisterCards.value!!.find { it.id == _parentCard.value!!.id }
        val nowPosition = _sisterCards.value!!.indexOf(nowCard)
        createNewCardNextToPosition(nowPosition + 1  ,false,a?.single(),true)


    }


    fun onClickBtnNext(navController: NavController){
        setGetStringData(true)
    setFromSameFrag(true)

//    val now = _sisterCards.value!!.find { it.card.id == _parentCard.value!!.card.id }
//    val nowPosition = _sisterCards.value!!.indexOf(now)

    if(_nextCardExists.value==true){
        val a = intArrayOf(_parentCard.value!!.belongingFlashCardCoverId!!)
        val nextCardId =_sisterCards.value?.get(_position.value!! +1)!!.id
        val b = intArrayOf(nextCardId)
        val action = CreateCardFragmentDirections.flipCreateCard()
        action.cardId = b
        action.parentFlashCardCoverId = a
        navController.navigate(action)
    } else return

    }
    fun onClickBtnPrevious(navController: NavController){
        setGetStringData(true)
        setFromSameFrag(true)
        val a = intArrayOf(_parentCard.value!!.belongingFlashCardCoverId!!)
        val nowCard = _sisterCards.value!!.find { it.id == _parentCard.value!!.id }
        val nowPosition = _sisterCards.value!!.indexOf(nowCard)
        if(nowPosition == 0 ){
            return


        } else{
            val nextCardId =_sisterCards.value?.get(nowPosition-1)!!.id
            val b = intArrayOf(nextCardId)
            val action = CreateCardFragmentDirections.flipCreateCard()
            action.cardId = b
            navController.navigate(action)
        }
    }


    private fun createNewCardNextToPosition(position: Int,previous:Boolean,parentFlashCardCoverId:Int?,fromSameFrag: Boolean){
        setFromSameFrag(fromSameFrag)
        setMode(Mode.New)
        val myPosition:Int = if(previous) position  else position
        setPosition(myPosition)
        setParentFlashCardCoverId(parentFlashCardCoverId)
        if (previous) updateSistersPosition(position) else updateSistersPosition(position)
        saveEmptyCard(myPosition,parentFlashCardCoverId)




    }


    fun onClickRVAddNewCard(item:LibraryRV){
//        val parentFileId = if(item.card?.belongingFileId==null){
//            null
//        } else intArrayOf(item.card.belongingFileId)
//
//        setAction(CreateCardFragmentDirections.toCreateCard(parentFileId,null))
        setOpenCreateCard(true)
        setFromSameFrag(false)
        createNewCardNextToPosition(item.card!!.libOrder +1,false,item.card.belongingFlashCardCoverId,false)
    }
    fun onClickAddNewCardBottomBar(){
        setOpenCreateCard(true)
        setFromSameFrag(false)
        createNewCardNextToPosition((_sisterCards.value?.size ?:0)    ,false,_parentFlashCardCoverId.value,false)



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
    fun onClickEditCard(item: Card,navController: NavController){
        setFromSameFrag(false)
        val a =if(item.belongingFlashCardCoverId== null) null else intArrayOf(item.belongingFlashCardCoverId!!)
        val b = intArrayOf(item.id)
        val action = CreateCardFragmentDirections.flipCreateCard()
        action.cardId= b
        action.parentFlashCardCoverId = a
        navController.navigate(action)
//        setAction(CreateCardNav( )
        setMode(Mode.Edit)



    }








}