package com.koronnu.kina.util.view_set_up

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.koronnu.kina.databinding.*
import com.koronnu.kina.ui.tabLibrary.LibraryBaseViewModel
import com.koronnu.kina.customViews.ImvChangeAlphaOnDown
import com.koronnu.kina.ui.listener.topbar.*
import com.koronnu.kina.ui.tabLibrary.DeletePopUpViewModel
import com.koronnu.kina.ui.tabLibrary.LibFragSearchBarCL
import com.koronnu.kina.ui.viewmodel.*

class LibraryAddListeners(){
//    top Bars


    fun fragChildMultiBaseAddCL(binding: LibraryChildFragWithMulModeBaseBinding,
                                context: Context,
                                libraryViewModel: LibraryBaseViewModel,
                                imvSearchLoup: ImvChangeAlphaOnDown?,
                                deletePopUpViewModel: DeletePopUpViewModel,
                                searchViewModel:SearchViewModel,
                                inputMethodManager: InputMethodManager){

        fun multiTopBarAddCL(binding: LibraryFragTopBarMultiselectModeBinding,
                             menuBarBinding: LibItemTopBarMenuBinding,
                             menuFrame:FrameLayout,
                             libVM: LibraryBaseViewModel,
                             deletePopUpViewModel: DeletePopUpViewModel, ){
        arrayOf(
            binding.imvCloseMultiMode,
            binding.imvSelectAll,
            binding.imvChangeMenuVisibility,
            menuBarBinding.linLayMoveSelectedItems,
            menuBarBinding.linLayDeleteSelectedItems,
        ).onEach { it.setOnClickListener( LibFragTopBarMultiModeCL( libVM)) }
        }

        fun searchAddCL(){
            if(imvSearchLoup!=null)
                arrayOf(imvSearchLoup,binding.bindingSearch.txvCancel).onEach {
                it.setOnClickListener(LibFragSearchBarCL(
                    context,imvSearchLoup,binding.frameLaySearchBar,binding.bindingSearch,searchViewModel,inputMethodManager
                ))
            }
        }

        multiTopBarAddCL(binding.topBarMultiselectBinding,binding.multiSelectMenuBinding,binding.frameLayMultiModeMenu,libraryViewModel,deletePopUpViewModel)
        searchAddCL()

        binding.libChildFragBackground.setOnClickListener {
            libraryViewModel.setMultiMenuVisibility(false)
        }


        binding.bindingSearch.edtLibrarySearch.addTextChangedListener {
            if(it.toString().isBlank()){
                searchViewModel.setSearchText(String())
                binding.mainFrameLayout.visibility= View.VISIBLE
                binding.frameLaySearchRv.visibility = View.GONE

            } else {
                binding.mainFrameLayout.visibility = View.GONE
                binding.frameLaySearchRv.visibility = View.VISIBLE
                searchViewModel.setSearchText(it.toString())
            }
        }

    }


    fun fileTopBarAddCL(binding: LibraryFragTopBarFileBinding,ancestors:LibraryFragTopBarAncestorsBinding,libVM: LibraryBaseViewModel){
        arrayOf(
            binding.imvGoBack,
            ancestors.lineLayGGFile,
            ancestors.lineLayGPFile,

            ).onEach { it.setOnClickListener( LibFragTopBarFileCL(binding,ancestors,libVM, )) }
    }



//    recycler Views







}