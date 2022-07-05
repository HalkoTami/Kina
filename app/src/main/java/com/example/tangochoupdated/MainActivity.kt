package com.example.tangochoupdated

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.vector.Group
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
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
import com.example.tangochoupdated.ui.anki.AnkiFragmentDirections
import com.example.tangochoupdated.ui.library.HomeFragmentArgs
import com.example.tangochoupdated.ui.library.HomeFragmentDirections

import com.example.tangochoupdated.ui.planner.CreateFragment
import com.example.tangochoupdated.ui.planner.CreateFragmentDirections
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {


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

        binding.frameBnv.addView(bnvBinding.root)




        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragContainerView.id) as NavHostFragment
        val navCon = navHostFragment.navController
        navArgs<HomeFragmentArgs>()
        val toLibrary = HomeFragmentDirections.toLib()
        val toAnki = AnkiFragmentDirections.toAnki()

        binding.createFileBinding.apply {
            popupAddFile.visibility = GONE
            frameBottomMenu.visibility = GONE
        }




        baseviewModel.setOnCreate()




        baseviewModel.activeTab.observe(this){
            when(it){
                Tab.TabLibrary -> {
                    navCon.navigate(toLibrary)
                }
                Tab.TabAnki -> navCon.navigate(toAnki)
            }
        }




//    add new file pop up -- bottom menu


        baseviewModel.bottomMenuVisible.observe(this){
            val btmMenuAnimator = AnimatorSet().apply{
                val a = ObjectAnimator.ofFloat(binding.createFileBinding.frameBottomMenu, View.TRANSLATION_Y, 300f,0f)
                val b = ObjectAnimator.ofFloat(binding.createFileBinding.frameBottomMenu, View.ALPHA,0f,1f)
                playTogether(a,b)
                duration = 500
            }

            when (it){
                true -> {
                    binding.createFileBinding.root.visibility = VISIBLE
                    btmMenuAnimator.start()
                    binding.createFileBinding.frameBottomMenu.visibility = VISIBLE
                    binding.createFileBinding.root.setOnClickListener {
                        baseviewModel.reset()

                    }


                }
                false ->{
                    btmMenuAnimator.doOnEnd {
                        binding.createFileBinding.frameBottomMenu.visibility = GONE }
                    btmMenuAnimator.reverse()

                }
            }

        }

        binding.createFileBinding.bindingAddMenu.apply {
            imvnewTangocho.setOnClickListener {
                baseviewModel.onClickFolderOrFlashCard()
            }
            imvnewfolder.setOnClickListener {
            baseviewModel.onClickFolderOrFlashCard()
        }
        }

        //    add new file pop up -- edit file
        baseviewModel.editFilePopUpVisible.observe(this){
            val appearAnimator = AnimatorSet().apply {
                duration= 500
                play(ObjectAnimator.ofFloat(binding.createFileBinding.popupAddFile, View.ALPHA, 0f,1f ))
            }
            when (it){
                true -> {

                    binding.createFileBinding.popupAddFile.visibility = VISIBLE
                    appearAnimator.start()
                    binding.createFileBinding.root.setOnClickListener {
                        baseviewModel.reset()
                    }
                }
                false ->{
                    appearAnimator.doOnEnd {
                        binding.createFileBinding.popupAddFile.visibility = GONE
                    }
                    appearAnimator.reverse()

                }
            }

        }
//        add new File pop up Data
        binding.createFileBinding.bindingCreateFile.apply {


            val a = DrawableCompat.wrap(getDrawable(R.drawable.color_palet_circle)!!)
            a.setTint(R.color.red)
            val b = LayerDrawable(arrayOf(a))
        }


//         background

        baseviewModel.addFileActive.observe(this){
            binding.createFileBinding.root.visibility = when(it){
                true -> VISIBLE
                false -> GONE
            }

        }

//        bottom navigation bar
        bnvBinding.apply {
            layout1.apply {
                baseviewModel.bnvLayout1View.observe(this@MainActivity){
                    bnvImv1.setImageDrawable(getDrawable(it.drawableId))
                    bnvImv1.setPadding(it.padding)
                    bnvTxv1.text = it.tabName
                    bnvTxv1.visibility = when(it.tabNameVisibility){
                        true -> View.VISIBLE
                        false -> GONE
                    }
                }
                setOnClickListener {
                    baseviewModel.onClickTab1()
                }
            }
            layout2.setOnClickListener {
                baseviewModel.onClickAddTab()
            }
            layout3.apply {
                baseviewModel.bnvLayout3View.observe(this@MainActivity){
                    imv3.setImageDrawable(getDrawable(it.drawableId))
                    imv3.setPadding(it.padding)
                    txv3.text = it.tabName
                    txv3.visibility = when(it.tabNameVisibility){
                        true -> View.VISIBLE
                        false -> GONE
                    }
                }
                setOnClickListener {
                    baseviewModel.onClickTab3()
                }
            }
        }



//   reset


    }










}