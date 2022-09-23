package com.korokoro.kina.ui.view_set_up

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import com.korokoro.kina.databinding.*
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.ui.listener.*
import com.korokoro.kina.ui.listener.recyclerview.*
import com.korokoro.kina.ui.listener.topbar.*
import com.korokoro.kina.ui.viewmodel.*

class LibraryAddListeners(){
//    top Bars


    fun fragChildMultiBaseAddCL(binding: LibraryChildFragWithMulModeBaseBinding,
                                context: Context,
                                libraryViewModel: LibraryBaseViewModel,
                                createCardViewModel: CreateCardViewModel,
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
            menuBarBinding.linLaySetFlagToSelectedItems,
        ).onEach { it.setOnClickListener( LibFragTopBarMultiModeCL( binding,menuBarBinding ,menuFrame,libVM,deletePopUpViewModel)) }
        }
        fun searchAddCL(){
            arrayOf(binding.imvSearchLoupe,binding.bindingSearch.txvCancel).onEach {
                it.setOnClickListener(LibFragSearchBarCL(
                    context,binding.imvSearchLoupe,binding.frameLaySearchBar,binding.bindingSearch,searchViewModel,inputMethodManager
                ))
            }
        }

        multiTopBarAddCL(binding.topBarMultiselectBinding,binding.multiSelectMenuBinding,binding.frameLayMultiModeMenu,libraryViewModel,deletePopUpViewModel)
        searchAddCL()


        binding.rvCover.
        setOnTouchListener(LibraryRVTouchListener(context,binding.vocabCardRV,libraryViewModel,binding.frameLayTest,createCardViewModel))

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
                        mainNavCon = mainNavController
                    )
                )
            }
        }
    }




}