package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.example.tangochoupdated.R
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.enumclass.AnkiFragments
import com.example.tangochoupdated.db.enumclass.FragmentTree
import com.example.tangochoupdated.db.enumclass.StartFragment
import com.example.tangochoupdated.db.enumclass.Tab
import com.example.tangochoupdated.ui.fragment.base_frag_con.AnkiFragmentBaseDirections
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBaseDirections
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



    private val _activeFragment = MutableLiveData<FragmentTree>()
    val activeFragment:LiveData<FragmentTree> = _activeFragment
    fun setActiveFragment(fragmentTree: FragmentTree){
        val previous = _activeFragment.value
        if (fragmentTree == previous) return else {
            _activeFragment.value = fragmentTree
        }
        if(fragmentTree.startFragment== StartFragment.EditCard||
                fragmentTree.ankiFragment == AnkiFragments.Flip)
            setBnvVisibility(false) else setBnvVisibility(true)

    }
    fun returnActiveFragment():FragmentTree?{
        return _activeFragment.value
    }

    private val _bnvVisibility = MutableLiveData<Boolean>()
    val bnvVisibility:LiveData<Boolean> = _bnvVisibility
    fun setBnvVisibility(boolean: Boolean){
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


//   nav action

    private val _action = MutableLiveData<NavDirections>()
    val action: LiveData<NavDirections> = _action
    fun setAction(navDirections: NavDirections){
        _action.value = navDirections
    }

//    which tab is active

    private val _activeTab = MutableLiveData<Tab>()
    val activeTab:LiveData<Tab> = _activeTab
    fun setActiveTab (tab:Tab){
        _activeTab.value = tab




    }
    fun onClickTab1(){
        when(_activeTab.value){
            Tab.TabLibrary -> return
            else -> {
                setActiveTab(Tab.TabLibrary)
                val a = LibraryFragmentBaseDirections.toLibHome()
                a.parentItemId = null
                setAction(a)
            }
        }
    }


    fun onClickTab3(){
        when(_activeTab.value){
            Tab.TabAnki -> return
            else -> {
                setActiveTab(Tab.TabAnki)
                setAction(AnkiFragmentBaseDirections.toAnki())
            }
        }
    }

//    fun onClickAddNewCard(){
//        setActiveTab(Tab.CreateCard)
//        val parentFileId:IntArray?
//        val parentCard:IntArray?
//        if(parentFile.value== null || parentFile.value?.fileStatus!=FileStatus.TANGO_CHO_COVER){
//            parentFileId = null
//        } else parentFileId = intArrayOf(parentFile.value!!.fileId)
//        parentCard = null
//
//        val a = CreateCardFragmentDirections.toCreateCard(parentFileId,parentCard)
//        setAction(a)
//    }







    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }





}

