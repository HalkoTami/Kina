package com.example.tangochoupdated

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels

import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tangochoupdated.databinding.MyActivityMainBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.library.LibraryViewModelFactory

class MainActivity : AppCompatActivity() {


    private lateinit var binding: MyActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MyActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.myBnv
        navView.itemIconTintList = null


        val navController =supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController!!.findNavController())



    }
}