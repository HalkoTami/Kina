package com.example.tangochoupdated

import androidx.lifecycle.*
import androidx.navigation.NavAction
import androidx.navigation.NavDirections
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.ui.anki.AnkiFragmentDirections
import com.example.tangochoupdated.ui.create.card.CreateCardFragmentDirections
import com.example.tangochoupdated.ui.library.HomeFragmentDirections
import kotlinx.coroutines.*



class BaseViewModel(private val repository: MyRoomRepository):ViewModel(){


    val createFragmentActive = MutableLiveData<Boolean>()



//on create
    fun setOnCreate(){
    activateBnvLayout1View()
    deactivateBnvLayout3View()
    setActiveTab(Tab.TabLibrary)
    setBnvVisibility(true)
    }
    //    parent FlashCardCover
    val parentFlashCardCoverId = MutableLiveData<Int>()
    fun setParentFlashCardCover(int:Int){
        parentFlashCardCoverId.value = int
    }


    private val _bnvVisibility = MutableLiveData<Boolean>()
    val bnvVisibility:LiveData<Boolean> = _bnvVisibility
    private fun setBnvVisibility(boolean: Boolean){
        val previous = _bnvVisibility.value
        if (boolean == previous) return else {
            _bnvVisibility.value = boolean
        }

    }

//    bnv each View Change
    inner class BnvLayout(
        val drawableId:Int,
        val padding:Int,
        val tabNameVisibility:Boolean,
        val tabName:String,
        )
    private val _bnvLayout1View = MutableLiveData<BnvLayout>()
    val bnvLayout1View:LiveData<BnvLayout> = _bnvLayout1View
    private fun setBnvLayout1View(bnvLayout: BnvLayout){
        _bnvLayout1View.value = bnvLayout
    }

    fun activateBnvLayout1View(){
        val a =BnvLayout(
            drawableId = R.drawable.icon_library_active,
            padding = 0,
            tabNameVisibility = true,
            tabName = "ライブラリ")
        setBnvLayout1View(a)
    }
    fun deactivateBnvLayout1View(){
        val a =  BnvLayout(
            drawableId = R.drawable.icon_library_plane,
            padding = 10,
            tabNameVisibility = false,
            tabName = "")
        setBnvLayout1View(a)
    }

    private val _bnvLayout3View = MutableLiveData<BnvLayout>()
    val bnvLayout3View:LiveData<BnvLayout> = _bnvLayout3View
    private fun setBnvLayout3View(bnvLayout: BnvLayout){
        _bnvLayout3View.value = bnvLayout
    }
    fun activateBnvLayout3View(){
        val a = BnvLayout(
            drawableId = R.drawable.icon_anki_active,
            padding = 0,
            tabNameVisibility = true,
            tabName = "暗記する")
        setBnvLayout3View(a)
    }
    fun deactivateBnvLayout3View(){
        val a = BnvLayout(
            drawableId = R.drawable.iconanki,
            padding = 10,
            tabNameVisibility = false,
            tabName = "")
        setBnvLayout3View(a)
    }
    private val _action = MutableLiveData<NavDirections>()
    val action: LiveData<NavDirections> = _action
    fun setAction(navDirections: NavDirections){
        _action.value = navDirections
    }

//    which tab is active

    private val _activeTab = MutableLiveData<Tab>()
    val activeTab:LiveData<Tab> = _activeTab
    fun setActiveTab (tab:Tab){

        val previousTab = _activeTab.value
        if(tab == previousTab) return else{
            _activeTab.apply {
                value = tab
            }
            when(previousTab){
                Tab.TabLibrary -> deactivateBnvLayout1View()
                Tab.TabAnki -> deactivateBnvLayout3View()
            }

            when(tab){
                Tab.TabLibrary ->{
                    activateBnvLayout1View()
                    setAction(HomeFragmentDirections.toLib())
                    setBnvVisibility(true)
                }
                Tab.TabAnki -> {
                    activateBnvLayout3View()
                    setAction(AnkiFragmentDirections.toAnki())
                    setBnvVisibility(true)
                }
                Tab.CreateCard -> {
                    val a = intArrayOf(parentFlashCardCoverId.value!!)
                    val b = CreateCardFragmentDirections.toCreateCard(a)
                    setAction(b)
                    setBnvVisibility(false)
                }
            }
        }




    }
    fun onClickTab1(){
        when(_activeTab.value){
            Tab.TabLibrary -> return
            else -> {
                setActiveTab(Tab.TabLibrary)
            }
        }
    }


    fun onClickTab3(){
        when(_activeTab.value){
            Tab.TabAnki -> return
            else -> {
                setActiveTab(Tab.TabAnki)
            }
        }
    }

    fun onClickAddNewCard(){
        setActiveTab(Tab.CreateCard)
    }





    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }





}

