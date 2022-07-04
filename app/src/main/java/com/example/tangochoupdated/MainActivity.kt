package com.example.tangochoupdated

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.vector.Group
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs

import com.example.tangochoupdated.databinding.ItemBottomNavigationBarBinding
import com.example.tangochoupdated.databinding.MyActivityMainBinding
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.room.enumclass.TabStatus
import com.example.tangochoupdated.ui.library.HomeFragmentArgs
import com.example.tangochoupdated.ui.library.HomeFragmentDirections

import com.example.tangochoupdated.ui.planner.CreateFragment
import com.example.tangochoupdated.ui.planner.CreateFragmentDirections

class MainActivity : AppCompatActivity() {


    var previousTabHolder:Tab = Tab.TabLibrary
    var popUpVisible = false
    lateinit var factory:ViewModelFactory

    lateinit var baseviewModel: BaseViewModel

    private val allTabs= listOf(Tab.TabLibrary,Tab.TabAnki,Tab.TabCreate)


    private lateinit var binding: MyActivityMainBinding
    private lateinit var bnvBinding: ItemBottomNavigationBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        factory = ViewModelFactory((application as RoomApplication).repository)

        baseviewModel = ViewModelProvider(this,
        ViewModelFactory((application as RoomApplication).repository)
        )[BaseViewModel::class.java]



        binding = MyActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        bnvBinding = ItemBottomNavigationBarBinding.inflate(layoutInflater)
        bnvBinding.imv2.setImageDrawable(getDrawable(R.drawable.icon_add_middlesize))
        allTabs.onEach {
            setEachOne(it,bnvBinding,TabStatus.UnFocused)
        }
        baseviewModel.changeActiveTab(Tab.TabLibrary)
        baseviewModel.changeCreateFragmentStatus(false)
        binding.frameBnv.addView(bnvBinding.root)




        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragContainerView.id) as NavHostFragment
        val navCon = navHostFragment.navController
        navArgs<HomeFragmentArgs>()

        baseviewModel.setOnCreate()








        baseviewModel.bottomMenuVisible.observe(this){

            val btmMenuAnimator = AnimatorSet().apply{
                val a = ObjectAnimator.ofFloat(binding.frameBottomMenu, View.TRANSLATION_Y, 300f,0f)
                val b = ObjectAnimator.ofFloat(binding.frameBottomMenu, View.ALPHA,0f,1f)
                playTogether(a,b)
                duration = 500

            }
            when (it){
                true -> {
                    btmMenuAnimator.start()
                    binding.frameBottomMenu.visibility = VISIBLE

                    binding.wholeView.setOnClickListener {
                        baseviewModel.reset()
                    }
                }
                false ->{
                    btmMenuAnimator.reverse()
                    binding.frameBottomMenu.visibility = GONE
                }
            }

        }
        baseviewModel.editFilePopUpVisible.observe(this){
            val appearAnimator = AnimatorSet().apply {
                duration= 500
                play(ObjectAnimator.ofFloat(binding.popupAddFile, View.ALPHA, 0f,1f ))
            }
            when (it){
                true -> {
                    appearAnimator.start()
                    binding.popupAddFile.visibility = VISIBLE
                }
                false ->{
                    appearAnimator.reverse()
                    binding.popupAddFile.visibility = GONE
                }
            }
        }
        bnvBinding.layout2.setOnClickListener {
            baseviewModel.onClickAddTab()
        }

        baseviewModel.activeTab.observe(this){
            setEachOne(previousTabHolder,bnvBinding,TabStatus.UnFocused)
            setEachOne(it, bnvBinding,TabStatus.Focused)
            previousTabHolder = it
        }
        bnvBinding.root.setOnClickListener {
            if(baseviewModel.createFragmentActive.value==true){

                val a = supportFragmentManager.findFragmentByTag("createFragment")!!
                if (a is CreateFragment){
                    a.finishWithAnimation()
                }

            } else if (baseviewModel.createFragmentActive.value==false){
                bnvBinding.layout1.setOnClickListener {
                    if(baseviewModel.activeTab.value!=Tab.TabLibrary){
                        baseviewModel.changeActiveTab(Tab.TabLibrary)
                        navCon.navigate(R.id.to_lib)
                    }

                }
                bnvBinding.layout3.setOnClickListener {
                    if(baseviewModel.activeTab.value!=Tab.TabAnki){
                        baseviewModel.changeActiveTab(Tab.TabAnki)
                        navCon.navigate(R.id.to_anki)
                    }
                }
            }
        }



    }



    fun setEachOne(changeTab: Tab?, bnvBinding: ItemBottomNavigationBarBinding, viewStatus: TabStatus){
        val imv = when(changeTab){
            Tab.TabLibrary -> bnvBinding.bnvImv1
            Tab.TabAnki -> bnvBinding.imv3
            else -> return
        }
        val txv= when(changeTab){
            Tab.TabLibrary -> bnvBinding.bnvTxv1
            Tab.TabAnki -> bnvBinding.txv3
            Tab.TabCreate -> return
        }
        val txvText = when(changeTab){
            Tab.TabLibrary -> getString(R.string.title_tab_library)
            Tab.TabAnki -> getString(R.string.title_tab_anki)
            Tab.TabCreate -> return
        }
        val activeDrawable:Drawable = when(changeTab){
            Tab.TabLibrary -> getDrawable(R.drawable.icon_library_active)!!
            Tab.TabAnki -> getDrawable(R.drawable.icon_anki_active)!!
            Tab.TabCreate -> return
        }
        val inActiveDrawable:Drawable= when(changeTab){
            Tab.TabLibrary -> getDrawable(R.drawable.icon_library_plane)!!
            Tab.TabAnki -> getDrawable(R.drawable.iconanki)!!
            Tab.TabCreate -> return
        }

        when (viewStatus) {
            TabStatus.Focused -> {
                txv.visibility = GONE
                imv.setImageDrawable(activeDrawable)
                imv.setPadding(10)
            }
            TabStatus.UnFocused ->  {
                txv.visibility = VISIBLE
                txv.text = txvText
                imv.setImageDrawable(inActiveDrawable)
                imv.setPadding(0)
            }
        }
    }





}