package com.koronnu.kina.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.ActivityMainBinding
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.ui.tabLibrary.LibraryBaseViewModel
import com.koronnu.kina.ui.tabLibrary.chooseFileMoveTo.ChooseFileMoveToViewModel
import com.koronnu.kina.ui.viewmodel.SearchViewModel


class MainActivity : AppCompatActivity(){

    private lateinit var mainNavCon              : NavController

    val createFileViewModel get() = mainActivityViewModel.editFileViewModel
    val createCardViewModel get() = mainActivityViewModel.createCardViewModel

    val mainActivityViewModel : MainViewModel by viewModels { MainViewModel.getViewModel(this) }
    val deletePopUpViewModel get() = mainActivityViewModel.deletePopUpViewModel

    lateinit var libraryViewModel : LibraryBaseViewModel
    private lateinit var chooseFileMoveToViewModel : ChooseFileMoveToViewModel
    private lateinit var searchViewModel         : SearchViewModel

    private lateinit var binding                  : ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fun setMainActivityLateInitVars(){

            binding = ActivityMainBinding.inflate(layoutInflater)
            binding.mainViewModel = mainActivityViewModel
            binding.lifecycleOwner = this
            val navHostFragment = supportFragmentManager.findFragmentById(binding.fcvActivityMain.id) as NavHostFragment
            libraryViewModel        =ViewModelProvider(this,
                LibraryBaseViewModel.getFactory(mainActivityViewModel,this,this.baseContext))[LibraryBaseViewModel::class.java]
            chooseFileMoveToViewModel      = ViewModelProvider(this,
                ChooseFileMoveToViewModel.getFactory(libraryViewModel))[ChooseFileMoveToViewModel::class.java]
            searchViewModel       = ViewModelProvider(this,SearchViewModel.Factory)[SearchViewModel::class.java]
            mainNavCon            =   navHostFragment.navController


        }


        setMainActivityLateInitVars()
        setContentView(binding.root)
//       TODO checkIfInstall()


        val bnvVisibilityObserver            = Observer<Boolean>{
            changeViewVisibility(binding.flTwgActivityMain,it)
        }





        val libraryParentFileObserver           = Observer<File?>{
            createCardViewModel.setParentFlashCardCover(it)
        }

//        ー－－－mainActivityのviewModel 読み取りー－－－

        binding.bindingPwEditFile.lifecycleOwner = this
        binding.bindingPwEditFile.viewModel = mainActivityViewModel.editFileViewModel

        mainActivityViewModel.setMainActivityNavCon(mainNavCon)
        mainActivityViewModel.setMainActivityBinding(binding)
        mainActivityViewModel.observeLiveDataFromMainActivity(this,this)
        mainActivityViewModel.bnvVisibility         .observe(this@MainActivity,bnvVisibilityObserver)
//        ー－－－CreateFileViewModelの読み取りー－－－
//        ー－－－LibraryViewModelの読み取りー－－－
        libraryViewModel. onCreate()
        libraryViewModel.parentFile.observe(this@MainActivity,libraryParentFileObserver)
        mainActivityViewModel.createCardViewModel.setMainActivityNavCon(mainNavCon)
        mainActivityViewModel.createCardViewModel.lastInsertedCardFromDB.observe(this) {
            createCardViewModel.setLastInsertedCard(it)
        }

    }



    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivityViewModel.doOnBackPress()
            }
        })
    }
}