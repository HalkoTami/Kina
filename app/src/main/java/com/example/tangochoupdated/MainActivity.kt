package com.example.tangochoupdated

import android.os.Bundle
import androidx.activity.viewModels

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.databinding.MyActivityMainBinding
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.ui.library.LibraryRVViewModel
import com.example.tangochoupdated.ui.library.ViewModelFactory

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
    fun initLibRV(rv:RecyclerView,adapter: LibraryListAdapter){
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)


    }


}