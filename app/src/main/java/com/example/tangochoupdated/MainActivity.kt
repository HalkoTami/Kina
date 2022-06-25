package com.example.tangochoupdated

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.setPadding
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment

import com.example.tangochoupdated.databinding.ItemBottomNavigationBarBinding
import com.example.tangochoupdated.databinding.MyActivityMainBinding
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.room.enumclass.TabStatus

import com.example.tangochoupdated.ui.planner.CreateFragment

class MainActivity : AppCompatActivity() {


    var previousTabHolder:Tab = Tab.TabLibrary
    var popUpVisible = false

    lateinit var baseviewModel: BaseViewModel

    private val allTabs= listOf(Tab.TabLibrary,Tab.TabAnki,Tab.TabCreate)


    private lateinit var binding: MyActivityMainBinding
    private lateinit var bnvBinding: ItemBottomNavigationBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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
            supportFragmentManager.findFragmentById(binding.viewPager.id) as NavHostFragment
        val navCon = navHostFragment.navController









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
                bnvBinding.layout2.setOnClickListener {
                    supportFragmentManager.commit {
                        if(supportFragmentManager.findFragmentByTag("createFragment")==null){
                            add(binding.viewPager.id, CreateFragment(),"createFragment")
                            baseviewModel.changeCreateFragmentStatus(true)
                        } else return@setOnClickListener

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

//    private fun onClickBnv(bnvBinding: ItemBottomNavigationBarBinding, v: View,navController: NavController){
//        val makeActive = when(v.id){
//            bnvBinding.layout1.id -> Tab.TabLibrary
//            bnvBinding.layout3.id -> Tab.TabAnki
//            else -> null
//
//        }
//
//
//        fun setBnvLayout( tab:Tab?,makeStatus: ViewStatus) {
//            val imv = when(tab){
//                Tab.TabLibrary -> bnvBinding.bnvImv1
//                Tab.TabAnki -> bnvBinding.imv3
//                else -> return
//            }
//            val txv= when(tab){
//                Tab.TabLibrary -> bnvBinding.bnvTxv1
//                Tab.TabAnki -> bnvBinding.txv3
//                Tab.TabCreate -> return
//            }
//            val txvText = when(tab){
//                Tab.TabLibrary -> getString(R.string.title_tab_library)
//                Tab.TabAnki -> getString(R.string.title_tab_anki)
//                Tab.TabCreate -> return
//            }
//            val activeDrawable:Drawable = when(tab){
//                Tab.TabLibrary -> getDrawable(R.drawable.icon_library_active)!!
//                Tab.TabAnki -> getDrawable(R.drawable.icon_anki_active)!!
//                Tab.TabCreate -> return
//            }
//            val inActiveDrawable:Drawable= when(tab){
//                Tab.TabLibrary -> getDrawable(R.drawable.icon_library_plane)!!
//                Tab.TabAnki -> getDrawable(R.drawable.iconanki)!!
//                Tab.TabCreate -> return
//            }
//
//            when (makeStatus) {
//                ViewStatus.Active -> {
//                    txv.visibility = GONE
//                    imv.setImageDrawable(activeDrawable)
//                    imv.setPadding(10)
//                }
//                ViewStatus.InActive ->  {
//                    txv.visibility = VISIBLE
//                    txv.text = txvText
//                    imv.setImageDrawable(inActiveDrawable)
//                    imv.setPadding(0)
//                }
//            }
//
//
//
//        }
//        fun fragmentAction( makeActive:Tab ){
//
//            val actionId = when(v.id){
//                bnvBinding.layout1.id -> R.id.to_lib
//                bnvBinding.layout3.id -> R.id.to_anki
//                else -> return
//            }
//            navController.navigate(actionId!!)
//            setBnvLayout(activeTab,ViewStatus.InActive)
//            setBnvLayout(makeActive,ViewStatus.Active)
//            activeTab = makeActive
//
//
//        }
//        if(baseviewModel.popUpVisible){
//            supportFragmentManager.commit {
//                remove( supportFragmentManager.findFragmentByTag("AddFragment")!!)
//                popUpVisible = false
//            }
//        } else{
//            if(makeActive!=null){
//                if(makeActive!=activeTab){
//                    fragmentAction(makeActive)
//                } else return
//            } else{
//                supportFragmentManager.commit {
//                    add(binding.viewPager.id,CreateFragment(),"AddFragment")
//                    popUpVisible =true
//
//                }
//            }
//
//        }
//
//
//
//
//
//    }
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