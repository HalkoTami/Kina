package com.example.tangochoupdated

import androidx.lifecycle.*
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
    }
    //    parent FlashCardCover
    val parentFlashCardCoverId = MutableLiveData<Int>()
    fun setParentFlashCardCover(int:Int){
        parentFlashCardCoverId.value = int
    }







//    bottom menu Visibility


//    popUpFile data

// txv left top parent file title














//    onclick add






//    bnv each View Change
    inner class BnvLayout(
        val drawableId:Int,
        val padding:Int,
        val tabNameVisibility:Boolean,
        val tabName:String
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
    private val _action = MutableLiveData<Any>()
    val action: LiveData<Any> = _action
    fun setAction(any: Any){
        _action.value = any
    }

//    which tab is active

    private val _activeTab = MutableLiveData<Tab>()
    val activeTab:LiveData<Tab> = _activeTab
    private fun setActiveTab (tab:Tab){
        val previousTab = _activeTab.value
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
            }
            Tab.TabAnki -> {
                activateBnvLayout3View()
                setAction(AnkiFragmentDirections.toAnki())
            }
            Tab.CreateCard -> {
                val a = intArrayOf(parentFlashCardCoverId.value!!)
                setAction(CreateCardFragmentDirections.toCreateCard(a))
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





    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }





}

