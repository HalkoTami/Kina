//package com.example.tangochoupdated.ui.library
//
//import android.content.Context
//import android.view.View
//import android.widget.FrameLayout
//import android.widget.ImageView
//import androidx.navigation.NavController
//import com.example.tangochoupdated.databinding.*
//import com.example.tangochoupdated.db.dataclass.Card
//import com.example.tangochoupdated.db.dataclass.File
//import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
//import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
//import com.example.tangochoupdated.ui.library.clicklistener.*
//
//class LibraryAddClickListeners{
//    private fun confirmDeletePopUpAddCL(binding: LibraryFragBinding,libVM:LibraryViewModel){
//        val onlyP = binding.confirmDeletePopUpBinding
//        val deleteAllC = binding.confirmDeleteChildrenPopUpBinding
//        arrayOf(
//            onlyP.btnCloseConfirmDeleteOnlyParentPopup,
//            onlyP.btnCommitDeleteOnlyParent,
//            onlyP.btnDenyDeleteOnlyParent,
//            deleteAllC.btnCloseConfirmDeleteOnlyParentPopup,
//            deleteAllC.btnCommitDeleteAllChildren,
//            deleteAllC.btnDenyDeleteAllChildren
//        )   .onEach {
//            it.setOnClickListener(LibFragPopUpConfirmDeleteCL(binding,libVM))
//        }
//    }
//    private fun fragLibHomeTopBarAddCL(binding: LibraryFragTopBarHomeBinding,context: Context,libVM: LibraryViewModel,navCon: NavController){
//        arrayOf(
//            binding.frameLayInBox,
//            binding.imvBookMark,
//        ).onEach { it.setOnClickListener( LibFragTopBarHomeCL(context, binding, libVM, navCon)) }
//    }
//    private fun fragLibMultiTopBarAddCL(binding: LibraryFragTopBarMultiselectModeBinding,libVM: LibraryViewModel,context: Context,navCon: NavController){
//        arrayOf(
//            binding.imvCloseMultiMode,
//            binding.imvSelectAll,
//            binding.imvChangeMenuVisibility,
//            binding.multiSelectMenuBinding.linLayMoveSelectedItems,
//            binding.multiSelectMenuBinding.linLayDeleteSelectedItems,
//            binding.multiSelectMenuBinding.linLaySetFlagToSelectedItems,
//        ).onEach { it.setOnClickListener( LibFragTopBarMultiModeCL(context, binding,libVM, navCon)) }
//    }
//
//    private fun fragLibInBoxTopBarAddCL(binding: LibraryFragTopBarInboxBinding,libVM: LibraryViewModel,context: Context,navCon: NavController){
//        arrayOf(
//            binding.imvMoveToFlashCard,
//            binding.imvCloseInbox,
//        ).onEach { it.setOnClickListener( LibFragTopBarInBoxCL(context, binding,libVM,navCon)) }
//    }
//    private fun fragLibFileTopBarAddCL(binding: LibraryFragTopBarFileBinding,libVM: LibraryViewModel,context: Context,navCon: NavController){
//        arrayOf(
//            binding.imvGoBack,
//            binding.lineLayGGFile,
//            binding.lineLayGPFile,
//
//            ).onEach { it.setOnClickListener( LibFragTopBarFileCL(context, binding,libVM, navCon)) }
//    }
//    private fun fragLibChooseFileMoveToTopBarAddCL(binding: LibraryFragTopBarChooseFileMoveToBinding,libVM: LibraryViewModel,context: Context,navCon: NavController){
//        arrayOf(
//            binding.imvCloseChooseFileMoveTo,
//        ).onEach { it.setOnClickListener( LibFragTopBarChooseFileMoveToCL(context, binding,libVM, navCon)) }
//    }
//    private fun fragLibSearchAddCL(imv:ImageView,
//                                   frameLay:FrameLayout,
//                                   searchBarBinding: ItemSearchBarBinding,
//                                   libVM: LibraryViewModel,
//                                   context: Context){
//        arrayOf(imv).onEach {
//            it.setOnClickListener(LibFragSearchBarCL(
//                context,imv,frameLay, searchBarBinding,libVM
//            ))
//        }
//    }
//
//    fun fragLibBaseAddCL(binding: LibraryFragBinding,libVM: LibraryViewModel){
//        confirmDeletePopUpAddCL(binding,libVM)
//    }
//    fun fragLibHomeAddCL(binding: LibraryFragHomeBaseBinding,
//                         libVM: LibraryViewModel,
//                         navCon:NavController,
//                         context: Context){
//        fragLibHomeTopBarAddCL(binding.topBarHomeBinding,context,libVM,navCon)
//        fragLibMultiTopBarAddCL(binding.topBarMultiselectBinding,libVM,context,navCon)
//        fragLibSearchAddCL(binding.imvSearchLoupe,binding.laySearchView, binding.bindingSearch,libVM,context)
//    }
//    fun fragLibFolderAddCL(binding: LibraryFragOpenFolderBaseBinding,
//                         libVM: LibraryViewModel,
//                         navCon:NavController,
//                         context: Context){
//        fragLibFileTopBarAddCL(binding.topBarFileBinding,libVM,context,navCon)
//        fragLibMultiTopBarAddCL(binding.topBarMultiselectBinding,libVM,context,navCon)
//        fragLibSearchAddCL(binding.imvSearchLoupe,binding.laySearchView, binding.bindingSearch,libVM,context)
//    }
//    fun fragLibFlashCardCoverAddCL(binding: LibraryFragOpenFlashCardCoverBaseBinding,
//                         libVM: LibraryViewModel,
//                         navCon:NavController,
//                         context: Context){
//        fragLibFileTopBarAddCL(binding.topBarFileBinding,libVM,context,navCon)
//        fragLibMultiTopBarAddCL(binding.topBarMultiselectBinding,libVM,context,navCon)
//        fragLibSearchAddCL(binding.imvSearchLoupe,binding.laySearchView, binding.bindingSearch,libVM,context)
//    }
//    fun fragLibInBoxAddCL(binding: LibraryFragInboxBaseBinding,
//                         libVM: LibraryViewModel,
//                         navCon:NavController,
//                         context: Context){
//        fragLibInBoxTopBarAddCL(binding.topBarInboxBinding,libVM,context,navCon)
//        fragLibMultiTopBarAddCL(binding.topBarMultiselectBinding,libVM,context,navCon)
//    }
//    fun fragLibChooseFileMoveToAddCL(binding: LibraryFragSelectFileMoveToBaseBinding,
//                          libVM: LibraryViewModel,
//                          navCon:NavController,
//                          context: Context){
//        fragLibChooseFileMoveToTopBarAddCL(binding.topBarChooseFileMoveToBinding,libVM,context,navCon)
//        fragLibSearchAddCL(binding.imvSearchLoupe,binding.laySearchView, binding.bindingSearch,libVM,context)
//    }
//
//
//    fun fragLibFileRVAddCL(binding: LibraryFragRvItemBaseBinding,
//                           fileBinding:LibraryFragRvItemFileBinding,
//                           context: Context,
//                           item: File,
//                           libVM: LibraryViewModel,
//                           createFileVM: CreateFileViewModel,
//                           chooseFileMoveTo:Boolean){
//        binding.apply {
//            arrayOf(
//                baseContainer,
//                btnDelete,
//                btnSelect,
//                btnAddNewCard,
//                btnEditWhole
//            ).onEach { it.setOnTouchListener(
//                if(chooseFileMoveTo)
//                    LibraryRVChooseFileMoveToCL(
//                        it,context,item,
//                        createFileVM,
//                        libVM,binding,fileBinding
//                    )
//                    else
//                LibraryRVFileCL(it,context,item,
//                    createFileVM,
//                    libVM,binding)
//            )
//            }
//        }
//    }
//    fun fragLibCardRVAddCL(binding: LibraryFragRvItemBaseBinding,
//                           context: Context,
//                           item: Card,
//                           libVM: LibraryViewModel,
//                           createCardViewModel: CreateCardViewModel){
//        binding.apply {
//            arrayOf(
//                baseContainer,
//                btnDelete,
//                btnSelect,
//                btnAddNewCard,
//                btnEditWhole
//            ).onEach { it.setOnTouchListener(
//                LibraryRVCardCL(it,context,item,createCardViewModel,libVM,binding)
//            )
//            }
//        }
//    }
//
//
//
//}