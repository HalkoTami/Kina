package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.korokoro.kina.ui.fragment.base_frag_con.AnkiBaseFragDirections
import com.korokoro.kina.ui.fragment.base_frag_con.EditCardBaseFragDirections
import com.korokoro.kina.ui.fragment.base_frag_con.LibraryBaseFragDirections
import com.korokoro.kina.customClasses.MainFragment
import kotlinx.coroutines.*



class MainViewModel:ViewModel(){

    class MainActivityChildFragmentStatus(
        var now: MainFragment,
        var before: MainFragment?
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
    private fun returnActiveFragment(): MainFragment?{
        return _childFragmentsStatus.value?.now
    }
    fun returnFragmentStatus():MainActivityChildFragmentStatus?{
        return _childFragmentsStatus.value
    }
    fun changeFragment(to: MainFragment){
        val navCOn = returnMainActivityNavCon()
        if(returnActiveFragment() == to||
                navCOn==null) return
        else{
            val action =
            when(to){
                MainFragment.Library -> LibraryBaseFragDirections.toLibrary()
                MainFragment.EditCard ->return
                MainFragment.Anki -> AnkiBaseFragDirections.toAnki()
            }
            navCOn.navigate(action)
        }
    }
    private val _bnvVisibility = MutableLiveData<Boolean>()
    val bnvVisibility:LiveData<Boolean> = _bnvVisibility
    fun setBnvVisibility(boolean: Boolean){
        _bnvVisibility.value = boolean
    }
    fun returnBnvVisibility():Boolean?{
        return _bnvVisibility.value
    }
    private val _helpOptionVisibility = MutableLiveData<Boolean>()
    val helpOptionVisibility:LiveData<Boolean> = _helpOptionVisibility
    fun setHelpOptionVisibility(boolean: Boolean){
        _helpOptionVisibility.value = boolean
    }
    fun returnHelpOptionVisibility():Boolean{
        return _helpOptionVisibility.value ?:false
    }
    private val _guideVisibility = MutableLiveData<Boolean>()
    val guideVisibility:LiveData<Boolean> = _guideVisibility
    fun setGuideVisibility(boolean: Boolean){
        _guideVisibility.value = boolean
    }
    fun returnGuideVisibility():Boolean{
        return _guideVisibility.value ?:false
    }
    fun checkIfFrameLayHelpIsVisible():Boolean{
        return returnGuideVisibility()||returnHelpOptionVisibility()
    }
    private val _bnvCoverVisible = MutableLiveData<Boolean>()
    val bnvCoverVisible:LiveData<Boolean> = _bnvCoverVisible

    fun setBnvCoverVisible (boolean: Boolean){
        _bnvCoverVisible.value = boolean
    }
    private val _confirmEndGuidePopUpVisible = MutableLiveData<Boolean>()
    val confirmEndGuidePopUpVisible:LiveData<Boolean> = _confirmEndGuidePopUpVisible

    fun setConfirmEndGuidePopUpVisible (boolean: Boolean){
        _confirmEndGuidePopUpVisible.value = boolean
    }
    fun returnConfirmEndGuidePopUpVisible ():Boolean{
        return _confirmEndGuidePopUpVisible.value ?:false
    }
    fun onClickEndGuide(){
        setConfirmEndGuidePopUpVisible(false)
        setGuideVisibility(false)
        setHelpOptionVisibility(false)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }





}

