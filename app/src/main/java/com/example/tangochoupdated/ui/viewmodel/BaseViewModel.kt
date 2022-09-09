package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.enumclass.MainFragment
import com.example.tangochoupdated.ui.fragment.base_frag_con.AnkiFragmentBaseDirections
import com.example.tangochoupdated.ui.fragment.base_frag_con.CreateCardFragmentBaseDirections
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBaseDirections
import kotlinx.coroutines.*



class BaseViewModel():ViewModel(){

    class MainActivityChildFragmentStatus(
        var now:MainFragment,
        var before:MainFragment?
    )
    private val _mainActivityNavCon = MutableLiveData<NavController>()
    fun setMainActivityNavCon(navController: NavController){
        _mainActivityNavCon.value = navController
    }
    fun returnMainActivityNavCon(): NavController?{
        return _mainActivityNavCon.value
    }

    private val _childFragmentsStatus = MutableLiveData<MainActivityChildFragmentStatus>()
    val childFragmentStatus:LiveData<MainActivityChildFragmentStatus> = _childFragmentsStatus
    fun setChildFragmentStatus(fragment: MainFragment){
        val previous = _childFragmentsStatus.value?.now
        if (fragment == previous) return else {
            val newStatus = MainActivityChildFragmentStatus(fragment,previous)
            _childFragmentsStatus.value = newStatus
        }
    }
    fun returnActiveFragment():MainFragment?{
        return _childFragmentsStatus.value?.now
    }
    fun returnFragmentStatus():MainActivityChildFragmentStatus?{
        return _childFragmentsStatus.value
    }
    fun changeFragment(to:MainFragment){
        val navCOn = returnMainActivityNavCon()
        if(returnActiveFragment() == to||
                navCOn==null) return
        else{
            val action =
            when(to){
                MainFragment.Library -> LibraryFragmentBaseDirections.toLibrary()
                MainFragment.EditCard -> CreateCardFragmentBaseDirections.openCreateCard()
                MainFragment.Anki -> AnkiFragmentBaseDirections.toAnki()
            }
            navCOn.navigate(action)
        }
    }
    private val _bnvVisibility = MutableLiveData<Boolean>()
    val bnvVisibility:LiveData<Boolean> = _bnvVisibility
    fun setBnvVisibility(boolean: Boolean){
        val previous = _bnvVisibility.value
        if (boolean == previous) return else {
            _bnvVisibility.value = boolean
        }

    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }





}

