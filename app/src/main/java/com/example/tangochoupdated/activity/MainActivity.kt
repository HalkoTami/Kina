package com.example.tangochoupdated.activity

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
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.ui.mainactivity.BaseViewModel
import com.example.tangochoupdated.R
import com.example.tangochoupdated.RoomApplication
import com.example.tangochoupdated.ui.ViewModelFactory
import com.example.tangochoupdated.databinding.ColorPaletBinding

import com.example.tangochoupdated.databinding.ItemBottomNavigationBarBinding
import com.example.tangochoupdated.databinding.MyActivityMainBinding
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.library.LibraryViewModel

class MainActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var factory: ViewModelFactory

    lateinit var mainActivityViewModel: BaseViewModel
    lateinit var createFileViewModel: CreateFileViewModel
    lateinit var createCardViewModel: CreateCardViewModel
    lateinit var stringCardViewModel: StringCardViewModel
    lateinit var libraryViewModel:LibraryViewModel

    private lateinit var binding: MyActivityMainBinding
    private lateinit var bnvBinding: ItemBottomNavigationBarBinding

    private val  mainActivityClickableItem = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

//      全部のデータを消したいとき
//        applicationContext.deleteDatabase("my_database")


//        ー－－－mainActivityのviewー－－－

        binding = MyActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        navController の宣言
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragContainerView.id) as NavHostFragment
        val navCon = navHostFragment.navController
        binding.apply {
//        Focus request
            mainTopConstrainLayout.requestFocus()
//            初期view設定
            bnvBinding.imvEditFile.setImageDrawable(getDrawable(R.drawable.icon_add_middlesize))
            binding.createFileBinding.apply {
                popupAddFile.visibility = GONE
                frameBottomMenu.visibility = GONE
            }
//       　　 viewをclickListenerに追加
            mainActivityClickableItem.apply {
                bnvBinding.apply {
                    addAll(arrayOf(
                        layout1,layout2,layout3
                    ))
                }
                createFileBinding.apply {
                    bindingAddMenu.apply {
                        addAll(arrayOf(imvnewCard,imvnewTangocho,imvnewfolder))
                    }
                    bindingCreateFile.apply {
                        addAll(arrayOf(btnCreateFile,btnClose))
                        colPaletBinding.apply {
                            addAll(arrayOf(
                                imvColBlue,imvColGray,imvColRed,imvColYellow,imvIconPalet
                            ))
                        }
                    }
                }
                onEach { it.setOnClickListener(this@MainActivity) }
            }

        }



//        ー－－－－－－－

//        ー－－－すべてのViewModelの作成ー－－－
//         ViewModel Factory
        factory = ViewModelFactory((application as RoomApplication).repository)
//        viewModelの定義
        mainActivityViewModel = ViewModelProvider(this,
            ViewModelFactory((application as RoomApplication).repository)
        )[BaseViewModel::class.java]
        createFileViewModel = ViewModelProvider(this,
            ViewModelFactory((application as RoomApplication).repository)
        )[CreateFileViewModel::class.java]
        createCardViewModel = ViewModelProvider(this,
            ViewModelFactory((application as RoomApplication).repository)
        )[CreateCardViewModel::class.java]
        libraryViewModel = ViewModelProvider(this, factory
            )[LibraryViewModel::class.java]
        stringCardViewModel = ViewModelProvider(this)[StringCardViewModel::class.java]
//      　ー－－－－－－－－－

//        ー－－－mainActivityのviewModel 読み取りー－－－

        mainActivityViewModel.apply{
//        初期設定
            setOnCreate()
//           タブの画面遷移
            action.observe(this@MainActivity){
                navCon.navigate(it)
            }
//            ナビゲーションバーのUi変化
            bnvVisibility.observe(this@MainActivity){
                binding.frameBnv.visibility = if(it == true) VISIBLE else GONE
            }
            binding.bnvBinding.apply {
                bnvLayout1View.observe(this@MainActivity){
                    bnvImv1.setImageDrawable(getDrawable(it.drawableId))
                    bnvImv1.setPadding(it.padding)
                    bnvTxv1.text = it.tabName
                    bnvTxv1.visibility = when(it.tabNameVisibility){
                        true -> View.VISIBLE
                        false -> GONE
                    }
                }
                bnvLayout3View.observe(this@MainActivity){
                    imv3.setImageDrawable(getDrawable(it.drawableId))
                    imv3.setPadding(it.padding)
                    txv3.text = it.tabName
                    txv3.visibility = when(it.tabNameVisibility){
                        true -> View.VISIBLE
                        false -> GONE
                    }
                }
            }
        }

//        ー－－－ー－－－

//        ー－－－CreateFileViewModelの読み取りー－－－

        createFileViewModel.apply{
//            初期設定
            onCreate()
            binding.createFileBinding.apply {
//            すべてのvisibility
                addFileActive.observe(this@MainActivity){ boolean ->
                    when(boolean){
                        true ->{
                            root.visibility = VISIBLE
                        }
                        false -> {
                            root.visibility = GONE
                        }
                    }
                }
//            ー－－－追加メニューUIー－－－

                bottomMenuClickable.observe(this@MainActivity){
                    bindingAddMenu.apply {
                        this.imvnewfolder.visibility = if(!it.createFile)  GONE else VISIBLE
                        this.imvnewTangocho.visibility =if (!it.createFlashCardCover)  GONE else VISIBLE
                        this.imvnewCard.visibility= if(!it.createCard) GONE else VISIBLE
                    }
                }
                frameBottomMenu.apply {
                    bottomMenuVisible.observe(this@MainActivity){
                        val btmMenuAnimator = AnimatorSet().apply{
                            val a = ObjectAnimator.ofFloat(frameBottomMenu, View.TRANSLATION_Y, 300f,0f)
                            val b = ObjectAnimator.ofFloat(frameBottomMenu, View.ALPHA,0f,1f)
                            playTogether(a,b)
                            duration = 500
                        }
                        when (it){
                            true -> {
                                btmMenuAnimator.start()
                                visibility = VISIBLE
                            }
                            false ->{
                                btmMenuAnimator.doOnEnd {
                                    visibility = GONE }
                                btmMenuAnimator.reverse()
                            }
                        }
                    }
                }


//            ー－－－追加メニューUIー－－－

//            ーーーーファイル作成・編集UIー－－－

//                visibility
                popupAddFile.apply {

                    editFilePopUpVisible.observe(this@MainActivity){
                        val appearAnimator = AnimatorSet().apply {
                            duration= 500
                            play(ObjectAnimator.ofFloat(popupAddFile, View.ALPHA, 0f,1f ))
                        }
                        when (it){
                            true -> {
                                visibility = VISIBLE
                                appearAnimator.start()
                            }
                            false ->{
                                appearAnimator.doOnEnd {
                                    visibility = GONE
                                }
                                appearAnimator.reverse()
                            }
                        }
                    }
                }
//                UIへデータ反映
                bindingCreateFile.apply {
                    txvLeftTop.observe(this@MainActivity){
                        txvFileTitle.text = it
                    }
                    txvHintText.observe(this@MainActivity){
                        txvHint.text = it
                    }
                    var previousColor:ColorStatus?
                    previousColor = null
                    fileColor.observe(this@MainActivity){
                        val a = colPaletBinding
                        if(previousColor == null) makeAllColPaletUnselected(a)
                        else changeColPalletCol(previousColor!!,false,a)
                        changeColPalletCol(it,true,a)
                        previousColor = it
                    }
                    var start = true
                    lastInsetedFileId.observe(this@MainActivity){
                        if(start){
                            start = false
                            return@observe
                        } else createFileViewModel.setLastInsertedFileId(it)
                    }
                    createFileViewModel.txvFileTitleText.observe(this@MainActivity){
                        edtCreatefile.text = it
                    }
                }


//            ーーーーファイル作成・編集UIー－－－
            }
        }
//        ー－－－ー－－－
//        ー－－－LibraryViewModelの読み取りー－－－
        libraryViewModel.apply {
            action.observe(this@MainActivity){
                navCon.navigate(it)
                Toast.makeText(this@MainActivity, "action called ", Toast.LENGTH_SHORT).show()
            }
            parentFile.observe(this@MainActivity){
                createCardViewModel.setParentFlashCardCover(it)
                createFileViewModel.pAndGP(it?.fileId).observe(this@MainActivity){
                    createFileViewModel.setPAndG(it)
                }
            }
            childCardsFromDB.observe(this@MainActivity){
                createCardViewModel.setSisterCards(it)
            }
//            allAncestors.observe(this@MainActivity){
//                createFileViewModel.setPAndG(it)
//            }
            myFinalList.observe(this@MainActivity){
                createFileViewModel.setNewPosition(it.size+1)
            }
        }
//        ー－－－ー－－－

//        ー－－－CreateCardViewModelの読み取りー－－－

        createCardViewModel.apply{
            onCreateViewModel()
            var calledFirstTime = true
            lastInsertedCardAndTags.observe(this@MainActivity){
                if(!calledFirstTime){
                    createCardViewModel.setLastInsertedCardAndTags(it)
                }
                calledFirstTime = false
            }
            action.observe(this@MainActivity){
                if(it.fromSameFrag){
                    navCon.popBackStack()
                    navCon.navigate(it.action)
                } else navCon.navigate(it.action)
            }
        }

//        ー－－－ー－－－
 }

    override fun onClick(v: View?) {
        binding.apply {
            bnvBinding.apply {
                when(v){
                    layout1 -> mainActivityViewModel.onClickTab1()
                    layout3 -> mainActivityViewModel.onClickTab3()
                    layout2 -> createFileViewModel.onClickImvAddBnv()
                }
            }
            createFileBinding.apply {
                bindingAddMenu.apply {
                    when(v){
                        imvnewCard -> createCardViewModel.onClickAddNewCardBottomBar()
                        imvnewTangocho -> createFileViewModel.onCLickCreateFlashCardCover()
                        imvnewfolder -> createFileViewModel.onClickCreateFolder()
                    }
                }
                bindingCreateFile.apply {
                    when(v){
                        btnClose -> createFileViewModel.setAddFileActive(false)
                        btnCreateFile ->
                            if(edtCreatefile.text.isEmpty()) edtCreatefile.hint = "タイトルが必要です"
                            else createFileViewModel.onClickFinish(edtCreatefile.text!!.toString())
                    }
                    colPaletBinding.apply {
                        createFileViewModel.apply {
                            when(v){
                                imvColYellow -> setFileColor(ColorStatus.YELLOW)
                                imvColGray -> setFileColor(ColorStatus.GRAY)
                                imvColBlue -> setFileColor(ColorStatus.BLUE)
                                imvColRed -> setFileColor(ColorStatus.RED)
                                imvIconPalet -> lineLayColorPalet.children.iterator().forEachRemaining {
                                    it.visibility = if(it.visibility == VISIBLE) GONE else VISIBLE
                                }
                            }
                        }

                    }
                }
            }
        }

    }

//    Toast.makeText(this@MainActivity,"called",Toast.LENGTH_SHORT).show()


    fun changeColPalletCol(colorStatus: ColorStatus, selected:Boolean, colorPaletBinding: ColorPaletBinding){


        val circle = getDrawable(R.drawable.circle)!!
        val wrappedCircle = DrawableCompat.wrap(circle)
        val stroke = getDrawable(R.drawable.circle_stroke)
        val imageView:ImageView
        val col:Int
        colorPaletBinding.apply {
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

    fun makeAllColPaletUnselected(colorPaletBinding: ColorPaletBinding){
        changeColPalletCol(ColorStatus.RED,false,colorPaletBinding)
        changeColPalletCol(ColorStatus.YELLOW,false,colorPaletBinding)
        changeColPalletCol(ColorStatus.BLUE,false,colorPaletBinding)
        changeColPalletCol(ColorStatus.GRAY,false,colorPaletBinding)
    }


}