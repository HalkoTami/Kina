package com.example.tangochoupdated

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.animation.doOnEnd
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavAction
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs

import com.example.tangochoupdated.databinding.ItemBottomNavigationBarBinding
import com.example.tangochoupdated.databinding.MyActivityMainBinding
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.ui.anki.AnkiFragmentDirections
import com.example.tangochoupdated.ui.create.card.CreateCardFragmentDirections
import com.example.tangochoupdated.ui.library.HomeFragmentArgs
import com.example.tangochoupdated.ui.library.HomeFragmentDirections
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel

class MainActivity : AppCompatActivity(),View.OnClickListener {


    lateinit var factory:ViewModelFactory

    lateinit var baseviewModel: BaseViewModel
    lateinit var createFileviewModel: CreateFileViewModel

    var filePopUpVisible = false



    private lateinit var binding: MyActivityMainBinding
    private lateinit var bnvBinding: ItemBottomNavigationBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        factory = ViewModelFactory((application as RoomApplication).repository)

        baseviewModel = ViewModelProvider(this,
        ViewModelFactory((application as RoomApplication).repository)
        )[BaseViewModel::class.java]

        createFileviewModel = ViewModelProvider(this,
            ViewModelFactory((application as RoomApplication).repository)
        )[CreateFileViewModel::class.java]

        binding = MyActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        bnvBinding = binding.bnvBinding
        bnvBinding.imvEditFile.setImageDrawable(getDrawable(R.drawable.icon_add_middlesize))





        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragContainerView.id) as NavHostFragment
        val navCon = navHostFragment.navController
        navArgs<HomeFragmentArgs>()



        binding.createFileBinding.apply {
            popupAddFile.visibility = GONE
            frameBottomMenu.visibility = GONE
        }





        baseviewModel.setOnCreate()
        createFileviewModel.onCreate()




        baseviewModel.apply {
            action.observe(this@MainActivity){
                navCon.navigate(it)

            }

        }




//    add new file pop up -- bottom menu
        createFileviewModel.bottomMenuClickable.observe(this){
            binding.createFileBinding.bindingAddMenu.apply {
                this.imvnewfolder.visibility = if(!it.createFile)  GONE else VISIBLE
                this.imvnewTangocho.visibility =if (!it.createFlashCardCover)  GONE else VISIBLE
                this.imvnewCard.visibility= if(!it.createCard) GONE else VISIBLE

            }
        }


        createFileviewModel.bottomMenuVisible.observe(this){
            val btmMenuAnimator = AnimatorSet().apply{
                val a = ObjectAnimator.ofFloat(binding.createFileBinding.frameBottomMenu, View.TRANSLATION_Y, 300f,0f)
                val b = ObjectAnimator.ofFloat(binding.createFileBinding.frameBottomMenu, View.ALPHA,0f,1f)
                playTogether(a,b)
                duration = 500
            }

            when (it){
                true -> {
                    btmMenuAnimator.start()
                    binding.createFileBinding.frameBottomMenu.visibility = VISIBLE



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
                createFileviewModel.onCLickCreateFlashCardCover()
            }
            imvnewfolder.setOnClickListener {
            createFileviewModel.onClickCreateFolder()
            }
            imvnewCard.setOnClickListener {
                createFileviewModel.setAddFileActive(false)
                baseviewModel.onClickAddNewCard()

            }
        }

        //    add new file pop up -- edit file
        createFileviewModel.editFilePopUpVisible.observe(this){
            val appearAnimator = AnimatorSet().apply {
                duration= 500
                play(ObjectAnimator.ofFloat(binding.createFileBinding.popupAddFile, View.ALPHA, 0f,1f ))
            }
            when (it){
                true -> {

                    binding.createFileBinding.popupAddFile.visibility = VISIBLE
                    appearAnimator.start()
                    binding.createFileBinding.popupAddFile.setOnClickListener(null)

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
            createFileviewModel.txvLeftTop.observe(this@MainActivity){
                txvFileTitle.text = it
            }
            createFileviewModel.txvHint.observe(this@MainActivity){
                txvHint.text = it
            }

            var previousColor:ColorStatus?
            previousColor = null


            createFileviewModel.fileColor.observe(this@MainActivity){



                if(previousColor == null){
                    changeSelected(ColorStatus.RED,false)
                    changeSelected(ColorStatus.YELLOW,false)
                    changeSelected(ColorStatus.BLUE,false)
                    changeSelected(ColorStatus.GRAY,false)

                } else{
                    changeSelected(previousColor!!,false)
                }
                changeSelected(it,true)
                previousColor = it



            }


            binding.createFileBinding.bindingCreateFile.colPaletBinding.apply {
                imvColBlue.setOnClickListener{createFileviewModel.setFileColor(ColorStatus.BLUE)}
                imvColGray.setOnClickListener{ createFileviewModel.setFileColor(ColorStatus.GRAY)}
                imvColRed.setOnClickListener{createFileviewModel.setFileColor(ColorStatus.RED)}
                imvColYellow.setOnClickListener { createFileviewModel.setFileColor(ColorStatus.YELLOW) }
            }


            var start = true
            createFileviewModel.lastInsetedFileId.observe(this@MainActivity){
                if(start){
                    start = false
                    return@observe
                } else{
                    createFileviewModel.setLastInsertedFileId(it)
                }


            }
            createFileviewModel.parentFileStock.observe(this@MainActivity){
                Toast.makeText(this@MainActivity,"stock size ${it.size}",Toast.LENGTH_SHORT).show()
            }
            btnCreateFile.setOnClickListener {
                if(edtCreatefile.text.isEmpty()){
                    edtCreatefile.hint = "タイトルが必要です"
                } else{
                    createFileviewModel.onClickFinish(edtCreatefile.text!!.toString())
                }

            }
            createFileviewModel.txvFileTitleText.observe(this@MainActivity){
                edtCreatefile.text = it
            }

//

        }


//         background

        createFileviewModel.addFileActive.observe(this@MainActivity){ boolean ->
            binding.createFileBinding.apply {
                when(boolean){
                    true ->{
                        root.visibility = VISIBLE
                        bindingCreateFile.btnClose.setOnClickListener {
                            createFileviewModel.setAddFileActive(false)
                        }
                        background.setOnClickListener{
                            createFileviewModel.setAddFileActive(false)
                        }
                    }
                    false -> {
                        root.visibility = GONE
                        this@MainActivity.filePopUpVisible = false
                    }
                }

            }



        }


//        bottom navigation bar
//
        baseviewModel.bnvVisibility.observe(this@MainActivity){
                binding.frameBnv.visibility = if(it == true) VISIBLE else GONE
            }
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
                createFileviewModel.onClickImvAddBnv()

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

    override fun onClick(v: View?) {


    }



    fun changeSelected(colorStatus: ColorStatus,selected:Boolean){
        val circle = getDrawable(R.drawable.circle)!!
        val wrappedCircle = DrawableCompat.wrap(circle)
        val stroke = getDrawable(R.drawable.circle_stroke)
        val imageView:ImageView
        val col:Int
        binding.createFileBinding.bindingCreateFile.colPaletBinding.apply {
            when(colorStatus) {
                ColorStatus.GRAY -> {
                    col = getColor(R.color.gray)
                    imageView = this.imvColGray
                }
                ColorStatus.BLUE -> {
                    col = getColor(R.color.blue)
                    imageView = this.imvColBlue
                }
                ColorStatus.YELLOW -> {
                    col = getColor(R.color.yellow)
                    imageView = this.imvColYellow
                }
                ColorStatus.RED -> {
                    col = getColor(R.color.red)
                    imageView = this.imvColRed
                }
            }
        }


        when(selected){
            true -> {
                DrawableCompat.setTint(wrappedCircle, col)
                val lay = LayerDrawable(arrayOf(wrappedCircle,stroke))
                imageView.setImageDrawable(lay)
            }
            false -> {
                DrawableCompat.setTint(wrappedCircle, col)
                imageView.setImageDrawable(wrappedCircle)
            }
        }


    }















}