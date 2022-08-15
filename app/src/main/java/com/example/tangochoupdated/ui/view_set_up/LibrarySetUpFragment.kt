package com.example.tangochoupdated.ui.view_set_up

import android.content.Context
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibrarySetUpFragment(val libVM: LibraryViewModel){
    private val addL = LibraryAddListeners(libVM)


    fun setUpFragLibBase(binding: LibraryFragBinding){
        addL.confirmDeletePopUpAddCL(binding)
    }
    fun setUpFragLibHome(binding: LibraryFragHomeBaseBinding,
                         navCon:NavController,
                         context: Context){
        addL.homeTopBarAddCL(binding.topBarHomeBinding,context,navCon)
        addL.multiTopBarAddCL(binding.topBarMultiselectBinding,context,navCon)
        addL.searchAddCL(binding.imvSearchLoupe,binding.laySearchView, binding.bindingSearch,context)
    }
    fun setUpFragLibFolder(binding: LibraryFragOpenFolderBaseBinding,
                         navCon:NavController,
                         context: Context){
        addL.fileTopBarAddCL(binding.topBarFileBinding,context,navCon)
        addL.multiTopBarAddCL(binding.topBarMultiselectBinding,context,navCon)
        addL.searchAddCL(binding.imvSearchLoupe,binding.laySearchView, binding.bindingSearch,context)
    }
    fun setUpFragLibFlashCardCover(binding: LibraryFragOpenFlashCardCoverBaseBinding,
                         navCon:NavController,
                         context: Context){
        addL.fileTopBarAddCL(binding.topBarFileBinding,context,navCon)
        addL.multiTopBarAddCL(binding.topBarMultiselectBinding,context,navCon)
        addL.searchAddCL(binding.imvSearchLoupe,binding.laySearchView, binding.bindingSearch,context)
    }
    fun setUpFragLibInBox(binding: LibraryFragInboxBaseBinding,
                         navCon:NavController,
                         context: Context){
        addL.inBoxTopBarAddCL(binding.topBarInboxBinding,context,navCon)
        addL.multiTopBarAddCL(binding.topBarMultiselectBinding,context,navCon)
    }
    fun setUpFragLibChooseFileMoveTo(binding: LibraryFragSelectFileMoveToBaseBinding,
                          navCon:NavController,
                          context: Context){
        addL.fragChooseFileMoveToTopBarAddCL(binding.topBarChooseFileMoveToBinding,context,navCon)
        addL.searchAddCL(binding.imvSearchLoupe,binding.laySearchView, binding.bindingSearch,context)
    }






}