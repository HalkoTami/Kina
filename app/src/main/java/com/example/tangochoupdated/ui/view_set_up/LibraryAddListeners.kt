package com.example.tangochoupdated.ui.view_set_up

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.listener.*
import com.example.tangochoupdated.ui.listener.popUp.LibFragPopUpConfirmDeleteCL
import com.example.tangochoupdated.ui.listener.recyclerview.*
import com.example.tangochoupdated.ui.listener.topbar.*
import com.example.tangochoupdated.ui.viewmodel.*

class LibraryAddListeners(val libVM: LibraryViewModel,val deletePopUpViewModel: DeletePopUpViewModel,val libNavCon: NavController){
//    top Bars
    fun confirmDeletePopUpAddCL(onlyP: LibraryFragPopupConfirmDeleteBinding,deleteAllC:LibraryFragPopupConfirmDeleteAllChildrenBinding){
        arrayOf(
            onlyP.btnCloseConfirmDeleteOnlyParentPopup,
            onlyP.btnCommitDeleteOnlyParent,
            onlyP.btnDenyDeleteOnlyParent,
            deleteAllC.btnCloseConfirmDeleteOnlyParentPopup,
            deleteAllC.btnDeleteAllChildren,
            deleteAllC.deleteOnlyFile,
            deleteAllC.btnCancel
        )   .onEach {
            it.setOnClickListener(LibFragPopUpConfirmDeleteCL(onlyP,deleteAllC,libVM, deletePopUpViewModel ))
        }
    }
    fun multiTopBarAddCL(binding: LibraryFragTopBarMultiselectModeBinding,menuBarBinding: LibItemTopBarMenuBinding,menuFrame:FrameLayout, context: Context, navCon: NavController){
        arrayOf(
            binding.imvCloseMultiMode,
            binding.imvSelectAll,
            binding.imvChangeMenuVisibility,
            menuBarBinding.linLayMoveSelectedItems,
            menuBarBinding.linLayDeleteSelectedItems,
            menuBarBinding.linLaySetFlagToSelectedItems,
        ).onEach { it.setOnClickListener( LibFragTopBarMultiModeCL(context, binding,menuBarBinding ,menuFrame,libVM, navCon,deletePopUpViewModel)) }
    }
    fun fragChildMultiBaseAddCL(binding: LibraryChildFragWithMulModeBaseBinding,context: Context,navCon: NavController){
        multiTopBarAddCL(binding.topBarMultiselectBinding,binding.multiSelectMenuBinding,binding.frameLayMultiModeMenu,context,navCon)
        searchAddCL(binding.imvSearchLoupe,binding.laySearchView,binding.bindingSearch,context)

    }
    fun homeTopBarAddCL(binding: LibraryFragTopBarHomeBinding, context: Context, navCon: NavController){
        arrayOf(
            binding.frameLayInBox,
            binding.imvBookMark,
        ).onEach { it.setOnClickListener( LibFragTopBarHomeCL(context, binding, libVM, navCon)) }
    }


    fun inBoxTopBarAddCL(binding: LibraryFragTopBarInboxBinding, context: Context, navCon: NavController){
        arrayOf(
            binding.imvMoveToFlashCard,
            binding.imvCloseInbox,
        ).onEach { it.setOnClickListener( LibFragTopBarInBoxCL(context, binding,libVM,navCon)) }
    }
    fun fileTopBarAddCL(binding: LibraryFragTopBarFileBinding,ancestors:LibraryFragTopBarAncestorsBinding, context: Context, navCon: NavController){
        arrayOf(
            binding.imvGoBack,
            ancestors.lineLayGGFile,
            ancestors.lineLayGPFile,

            ).onEach { it.setOnClickListener( LibFragTopBarFileCL(context, binding,ancestors,libVM, navCon)) }
    }
    fun fragChooseFileMoveToTopBarAddCL(binding: LibraryFragTopBarChooseFileMoveToBinding, context: Context, navCon: NavController){
        arrayOf(
            binding.imvCloseChooseFileMoveTo,
        ).onEach { it.setOnClickListener( LibFragTopBarChooseFileMoveToCL(context, binding,libVM, navCon)) }
    }
    fun searchAddCL(imv:ImageView,
                    frameLay:FrameLayout,
                    searchBarBinding: ItemSearchBarBinding,
                    context: Context){
        arrayOf(imv).onEach {
            it.setOnClickListener(LibFragSearchBarCL(
                context,imv,frameLay, searchBarBinding,libVM
            ))
        }
    }

//    recycler Views

    fun searchRVAddCL(binding: LibraryFragRvItemBaseBinding,
                      item: Any,
                      createCardViewModel: CreateCardViewModel,
                      libVM: LibraryViewModel,
                      mainNavController: NavController
    ){ binding.apply {
        arrayOf(
            baseContainer,

        ).onEach { it.setOnClickListener(
            LibraryRVSearchCL(item =  item,
                createCardViewModel = createCardViewModel,
                lVM = libVM,
                rvBinding = binding,
            navController = libNavCon, mainNavCon = mainNavController))
        }
    }
    }
    fun fileRVAddCL(binding: LibraryFragRvItemBaseBinding,
                    context: Context,
                    item: File,
                    createFileVM: CreateFileViewModel,
                    chooseFile:Boolean
    ){ binding.apply {
            arrayOf(
                baseContainer,
                btnDelete,
                btnSelect,
                btnAddNewCard,
                btnEditWhole
            ).onEach { if(chooseFile)it.setOnTouchListener(
                LibraryRVChooseFileMoveToCL(
                    it,
                    context,
                    item,
                    libNavCon,
                    libVM,
                    binding,
                )
            ) else it.setOnTouchListener(
                LibraryRVFileCL(it,context,item,libNavCon,
                    createFileVM,
                    libVM,binding,deletePopUpViewModel)
            )
            }
        }
    }

    fun cardRVAddCL(binding: LibraryFragRvItemBaseBinding,
                    context: Context,
                    item: Card,
                    createCardViewModel: CreateCardViewModel
    ){
        binding.apply {
            arrayOf(
                baseContainer,
                btnDelete,
                btnSelect,
                btnAddNewCard,
                btnEditWhole,

            ).onEach { it.setOnTouchListener(
                LibraryRVCardCL(it,context,item,libNavCon, createCardViewModel,libVM,binding,deletePopUpViewModel)
            )
            }
        }
    }

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