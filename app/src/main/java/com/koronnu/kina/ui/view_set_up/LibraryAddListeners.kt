package com.koronnu.kina.ui.view_set_up

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import com.koronnu.kina.databinding.*
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.ui.customViews.ImvChangeAlphaOnDown
import com.koronnu.kina.ui.listener.*
import com.koronnu.kina.ui.listener.recyclerview.*
import com.koronnu.kina.ui.listener.topbar.*
import com.koronnu.kina.ui.viewmodel.*

class LibraryAddListeners(){
//    top Bars


    fun fragChildMultiBaseAddCL(binding: LibraryChildFragWithMulModeBaseBinding,
                                context: Context,
                                libraryViewModel: LibraryBaseViewModel,
                                imvSearchLoup:ImvChangeAlphaOnDown?,
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
        ).onEach { it.setOnClickListener( LibFragTopBarMultiModeCL( binding,menuBarBinding ,menuFrame,libVM,deletePopUpViewModel)) }
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
            if(it.toString()!=""){
                binding.mainFrameLayout.visibility = View.GONE
                binding.frameLaySearchRv.visibility = View.VISIBLE
                searchViewModel.setSearchText(it.toString())
            } else {
                searchViewModel.setSearchText("")
                binding.mainFrameLayout.visibility= View.VISIBLE
                binding.frameLaySearchRv.visibility = View.GONE
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




    fun cardRVStringAddCL(binding: LibraryFragRvItemCardStringBinding,
                          item: Card,
                          createCardViewModel: CreateCardViewModel,
                          stringCardViewModel: CardTypeStringViewModel,
                          mainNavController: NavController
    ) {
        binding.apply {
            arrayOf(
                btnEdtBack,
                btnEdtFront,
            ).onEach {
                it.setOnClickListener(
                    LibraryRVStringCardCL(
                        item = item,
                        createCardViewModel = createCardViewModel,
                        stringCardViewModel = stringCardViewModel,
                        binding = binding,
                    )
                )
            }
        }
    }




}