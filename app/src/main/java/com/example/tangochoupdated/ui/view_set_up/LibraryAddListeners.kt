package com.example.tangochoupdated.ui.view_set_up

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.ui.unit.Constraints
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.makeToast
import com.example.tangochoupdated.ui.listener.*
import com.example.tangochoupdated.ui.listener.popUp.LibFragPopUpConfirmDeleteCL
import com.example.tangochoupdated.ui.listener.popUp.LibFragPopUpConfirmMoveToFileCL
import com.example.tangochoupdated.ui.listener.recyclerview.*
import com.example.tangochoupdated.ui.listener.topbar.*
import com.example.tangochoupdated.ui.viewmodel.*

class LibraryAddListeners(){
//    top Bars


    fun fragChildMultiBaseAddCL(binding: LibraryChildFragWithMulModeBaseBinding,
                                context: Context,
                                libraryViewModel: LibraryViewModel,
                                createCardViewModel: CreateCardViewModel,
                                deletePopUpViewModel: DeletePopUpViewModel,
                                searchViewModel:SearchViewModel, ){
        fun multiTopBarAddCL(binding: LibraryFragTopBarMultiselectModeBinding,
                             menuBarBinding: LibItemTopBarMenuBinding,
                             menuFrame:FrameLayout,
                             libVM: LibraryViewModel,
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
                    context,binding.imvSearchLoupe,binding.frameLaySearchBar,binding.bindingSearch,searchViewModel
                ))
            }
        }

        multiTopBarAddCL(binding.topBarMultiselectBinding,binding.multiSelectMenuBinding,binding.frameLayMultiModeMenu,libraryViewModel,deletePopUpViewModel)
        searchAddCL()
        binding.rvCover.
        setOnTouchListener(LibraryRVTouchListener(context,binding.vocabCardRV,libraryViewModel,binding.frameLayTest,createCardViewModel))
        binding.mainFrameLayout.viewTreeObserver.addOnGlobalLayoutListener (
            object: ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    binding.rvCover.layoutParams.height = binding.mainFrameLayout.height+10
                    binding.mainFrameLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )
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


    fun fileTopBarAddCL(binding: LibraryFragTopBarFileBinding,ancestors:LibraryFragTopBarAncestorsBinding,libVM: LibraryViewModel){
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
                          stringCardViewModel: StringCardViewModel,
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