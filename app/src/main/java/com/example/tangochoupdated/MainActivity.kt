package com.example.tangochoupdated

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import androidx.fragment.app.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

import com.example.tangochoupdated.databinding.ItemBottomNavigationBarBinding
import com.example.tangochoupdated.databinding.MyActivityMainBinding
import com.example.tangochoupdated.ui.anki.AnkiFragment
import com.example.tangochoupdated.ui.library.HomeFragment
import com.example.tangochoupdated.ui.planner.CreateFragment

class MainActivity : AppCompatActivity() {
    enum class Tab{
        TabLibrary,TabAnki,TabCreate
    }
    enum class ViewStatus{
        Active, InActive
    }
    var activeTab = Tab.TabLibrary
    var popUpVisible = false


    private lateinit var binding: MyActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = MyActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bnvBinding = ItemBottomNavigationBarBinding.inflate(layoutInflater)
        bnvBinding.bnvImv1.setImageDrawable(getDrawable(R.drawable.icon_library_active))
        bnvBinding.bnvTxv1.visibility = GONE
        bnvBinding.imv2.setImageDrawable(getDrawable(R.drawable.iconadd))
        bnvBinding.imv3.setImageDrawable(getDrawable(R.drawable.iconanki))
        bnvBinding.txv3.text = getString(R.string.title_tab_anki)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.viewPager.id) as NavHostFragment
        val myNav = navHostFragment.navController





        bnvBinding.root.children.iterator().forEachRemaining {
            it.setOnClickListener { view->
                onClickBnv(bnvBinding,view,myNav)
            }
        }


        binding.frameBnv.addView(bnvBinding.root)





    }

    private fun onClickBnv(bnvBinding: ItemBottomNavigationBarBinding, v: View,navController: NavController){
        val makeActive = when(v.id){
            bnvBinding.layout1.id -> Tab.TabLibrary
            bnvBinding.layout3.id -> Tab.TabAnki
            else -> null

        }


        fun setBnvLayout( tab:Tab?,makeStatus: ViewStatus) {
            val imv = when(tab){
                Tab.TabLibrary -> bnvBinding.bnvImv1
                Tab.TabAnki -> bnvBinding.imv3
                else -> return
            }
            val txv= when(tab){
                Tab.TabLibrary -> bnvBinding.bnvTxv1
                Tab.TabAnki -> bnvBinding.txv3
                Tab.TabCreate -> return
            }
            val txvText = when(tab){
                Tab.TabLibrary -> getString(R.string.title_tab_library)
                Tab.TabAnki -> getString(R.string.title_tab_anki)
                Tab.TabCreate -> return
            }
            val activeDrawable:Drawable = when(tab){
                Tab.TabLibrary -> getDrawable(R.drawable.icon_library_active)!!
                Tab.TabAnki -> getDrawable(R.drawable.icon_anki_active)!!
                Tab.TabCreate -> return
            }
            val inActiveDrawable:Drawable= when(tab){
                Tab.TabLibrary -> getDrawable(R.drawable.icon_library_plane)!!
                Tab.TabAnki -> getDrawable(R.drawable.iconanki)!!
                Tab.TabCreate -> return
            }

            when (makeStatus) {
                ViewStatus.Active -> {
                    txv.visibility = GONE
                    imv.setImageDrawable(activeDrawable)
                }
                ViewStatus.InActive ->  {
                    txv.visibility = VISIBLE
                    txv.text = txvText
                    imv.setImageDrawable(inActiveDrawable)
                }
            }



        }
        fun fragmentAction( makeActive:Tab ){

            val actionId = when(v.id){
                bnvBinding.layout1.id -> R.id.to_lib
                bnvBinding.layout3.id -> R.id.to_anki
                else -> return
            }
            navController.navigate(actionId!!)
            setBnvLayout(activeTab,ViewStatus.InActive)
            setBnvLayout(makeActive,ViewStatus.Active)
            activeTab = makeActive


        }
        if(popUpVisible){
            supportFragmentManager.commit {
                remove( supportFragmentManager.findFragmentByTag("AddFragment")!!)
                popUpVisible = false
            }
        } else{
            if(makeActive!=null){
                if(makeActive!=activeTab){
                    fragmentAction(makeActive)
                } else return
            } else{
                supportFragmentManager.commit {
                    add(binding.viewPager.id,CreateFragment(),"AddFragment")
                    popUpVisible =true

                }
            }

        }





    }



}