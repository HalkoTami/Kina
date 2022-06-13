package com.example.tangochoupdated

import android.os.Bundle

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

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
class MainActivity : AppCompatActivity() {
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





        viewPager = binding.viewPager

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        val bnvBinding = ItemBottomNavigationBarBinding.inflate(layoutInflater)
        binding.frameBnv.addView(bnvBinding.root)


    }
    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    fun initLibRV(rv:RecyclerView,adapter: LibraryListAdapter){
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)


    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = NUM_PAGES


        override fun createFragment(position: Int): Fragment{
            val fragments = mutableListOf<Fragment>()
            fragments.add(0,HomeFragment())
            fragments.add(1,CreateFragment())
            fragments.add(2,AnkiFragment())
            return fragments[position]
        }

    }

}