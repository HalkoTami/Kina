package com.example.tangochoupdated

import android.os.Bundle
import android.view.View

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit

import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.example.tangochoupdated.databinding.ItemBottomNavigationBarBinding
import com.example.tangochoupdated.databinding.MyActivityMainBinding
import com.example.tangochoupdated.ui.anki.AnkiFragment
import com.example.tangochoupdated.ui.library.HomeFragment
import com.example.tangochoupdated.ui.planner.CreateFragment
import com.example.tangochoupdated.ui.planner.PlannerFragment

private const val NUM_PAGES = 5
class MainActivity : AppCompatActivity(),View.OnClickListener {
    val fragments = mutableListOf(HomeFragment(),PlannerFragment(),CreateFragment())
    val fragmentManager = MyFragmentManager()

    class MyFragmentManager:FragmentManager(){

    }

    private lateinit var binding: MyActivityMainBinding
    private lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = MyActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fm = supportFragmentManager
        val fta = fm.beginTransaction()
        fta.replace(binding.viewPager.id,HomeFragment()).commit()


        val bnvBinding = ItemBottomNavigationBarBinding.inflate(layoutInflater)

        bnvBinding.root.children.iterator().forEachRemaining {
            it.setOnClickListener { when (it?.id) {
                bnvBinding.layout3.id -> fm.commit {
                    replace(binding.viewPager.id,AnkiFragment())
                }
                bnvBinding.layout2.id -> fm.commit {
                    replace(binding.viewPager.id,CreateFragment())
                }
                bnvBinding.layout1.id -> fm.commit {
                    replace(binding.viewPager.id,HomeFragment())
                }
            }
            }
        }


        binding.frameBnv.addView(bnvBinding.root)





    }

    override fun onClick(v: View?) {
        val bnvBinding = ItemBottomNavigationBarBinding.inflate(layoutInflater)
        val fm = supportFragmentManager
        val fta = fm.beginTransaction()
        when (v?.id) {
            bnvBinding.imvAnki.id -> fta.replace(binding.frameBnv.id, AnkiFragment()).commit()
            bnvBinding.imvAdd.id -> fta.replace(binding.frameBnv.id, CreateFragment()).commit()
        }




    }

}