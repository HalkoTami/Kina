package com.example.tangochoupdated.ui.mainactivity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.doOnEnd
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
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
    private lateinit var  navHostFragment : NavHostFragment
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
        navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragContainerView.id) as NavHostFragment
        val navCon = navHostFragment.navController
        binding.apply {
//        Focus request
            mainTopConstrainLayout.requestFocus()
//            初期view設定
            bnvBinding.imvEditFile.setImageDrawable(getDrawable(R.drawable.icon_add_middlesize))
            binding.apply {
                popupAddFile.visibility = GONE
                frameBottomMenu.visibility = GONE
                makeAllColPaletUnselected(bindingCreateFile.colPaletBinding)
            }

//       　　 viewをclickListenerに追加
            mainActivityClickableItem.apply {
                addAll(arrayOf(fragConViewCover, mainTopConstrainLayout))
                bnvBinding.apply {
                    addAll(arrayOf(
                        layout1,layout2,layout3
                    ))
                }
                bindingAddMenu.apply {
                    addAll(arrayOf(imvnewCard,imvnewTangocho,imvnewfolder,root))
                }
                bindingCreateFile.apply {
                    addAll(arrayOf(btnCreateFile,btnClose,root))
                    colPaletBinding.apply {
                        addAll(arrayOf(
                            imvColBlue,imvColGray,imvColRed,imvColYellow,imvIconPalet
                        ))
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
                        true -> VISIBLE
                        false -> GONE
                    }
                }
                bnvLayout3View.observe(this@MainActivity){
                    imv3.setImageDrawable(getDrawable(it.drawableId))
                    imv3.setPadding(it.padding)
                    txv3.text = it.tabName
                    txv3.visibility = when(it.tabNameVisibility){
                        true -> VISIBLE
                        false -> GONE
                    }
                }
            }
        }

//        ー－－－ー－－－

//        ー－－－CreateFileViewModelの読み取りー－－－

        binding.apply {
            createFileViewModel.apply{
//            初期設定
                onCreate()
//            ー－－－追加メニューUIー－－－

                bottomMenuClickable.observe(this@MainActivity){
                    binding.bindingAddMenu.apply {
                        this.imvnewfolder.visibility = if(!it.createFile)  GONE else VISIBLE
                        this.imvnewTangocho.visibility =if (!it.createFlashCardCover)  GONE else VISIBLE
                        this.imvnewCard.visibility= if(!it.createCard) GONE else VISIBLE
                    }
                }
//


//            ー－－－追加メニューUIー－－－

//            ーーーーファイル作成・編集UIー－－－

//                visibility
                popupAddFile.apply {
                    var previousColor:ColorStatus? =null
                    filePopUpUIData.observe(this@MainActivity){
                        if(visibility!= it.visibility)
                            animateVisibility(popupAddFile,
                                it.visibility
                            )
                        bindingCreateFile.apply {
                            if(txvFileTitle.text != it.txvLeftTopText) txvFileTitle.text = it.txvLeftTopText
                            if(txvHint.text!=it.txvHintText) txvHint.text=it.txvHintText

                            if(previousColor!=it.colorStatus){
                                changeColPalletCol(previousColor,false,colPaletBinding)
                                changeColPalletCol(it.colorStatus,true,colPaletBinding)
                                previousColor = it.colorStatus
                            }
                            if(imvFileType.tag!= it.drawableId){
                                imvFileType.setImageDrawable(getDrawable(it.drawableId))
                                imvFileType.tag = it.drawableId
                            }
                            edtCreatefile.apply {
                                if(text.toString()!=it.edtTitleText) text= SpannableStringBuilder(it.edtTitleText)
                                if(hint!= it.edtTitleHint) hint = it.edtTitleHint
                            }

                        }

                    }
                }
//                UIへデータ反映
                bindingCreateFile.apply {
                    var start = true
                    lastInsetedFileId.observe(this@MainActivity){
                        if(start){
                            start = false
                            return@observe
                        } else createFileViewModel.setLastInsertedFileId(it)
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
                createCardViewModel.setParentFlashCardCoverId(it?.fileId)
                createFileViewModel.setParentFile(it)
                createFileViewModel.parentFileParent(it?.parentFileId).observe(this@MainActivity){
                    createFileViewModel.setParentFileParent(it)
                }
                createFileViewModel.allAncestors(it?.fileId).observe(this@MainActivity){
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
                createFileViewModel.setPosition(it.size+1)
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
            if(frameBottomMenu.visibility == VISIBLE){
                bindingAddMenu.apply {
                    when(v){
                        imvnewCard ->  createCardViewModel.onClickAddNewCardBottomBar()
                        imvnewTangocho -> createFileViewModel.onCLickCreateFlashCardCover()
                        imvnewfolder -> createFileViewModel.onClickCreateFolder()
                        else -> {
                            fragConViewCover.visibility = GONE
                        }
                    }
                    animateVisibility(frameBottomMenu, GONE)
                }

            } else if(popupAddFile.visibility == VISIBLE){
                createFileViewModel.apply {
                    bindingCreateFile. apply {
                        when(v){
                            root -> return
                            btnClose -> {
                                animateVisibility(popupAddFile, GONE)
                                fragConViewCover.visibility = GONE
                            }
                            btnCreateFile ->{
                                if(edtCreatefile.text.toString() == "") {
                                    edtCreatefile.hint = "タイトルが必要です"
                                    return
                                } else {
                                    animateVisibility(popupAddFile, GONE)
                                    fragConViewCover.visibility = GONE
                                    createFileViewModel.onClickFinish(edtCreatefile.text!!.toString())
                                }
                            }
                        }
                        colPaletBinding.apply {
                            when(v){
                                imvColYellow -> onClickColorPalet(ColorStatus.YELLOW)
                                imvColGray -> onClickColorPalet(ColorStatus.GRAY)
                                imvColBlue -> onClickColorPalet(ColorStatus.BLUE)
                                imvColRed -> onClickColorPalet(ColorStatus.RED)
                                imvIconPalet -> animateVisibility(lineLayColorPalet,if(lineLayColorPalet.visibility == VISIBLE) GONE else VISIBLE)
                                else -> {
                                    animateVisibility(popupAddFile, GONE)
                                    fragConViewCover.visibility = GONE
                                }
                            }

                        }
                    }
                }

            }
            else{
                bnvBinding.apply {
                    when(v){
                        layout1 -> mainActivityViewModel.onClickTab1()
                        layout3 -> mainActivityViewModel.onClickTab3()
                        layout2 -> {
                            animateVisibility(frameBottomMenu, VISIBLE)
                            fragConViewCover.visibility = VISIBLE
                            createFileViewModel.onClickImvAddBnv()
                        }
                    }
                }
//                bindingAddMenu.apply {
//                    when(v){
//                        imvnewCard -> createCardViewModel.onClickAddNewCardBottomBar()
//                        imvnewTangocho -> createFileViewModel.onCLickCreateFlashCardCover()
//                        imvnewfolder -> createFileViewModel.onClickCreateFolder()
//                        else -> frameBottomMenu.visibility == GONE
//                    }
//                }

//                bindingCreateFile.apply {
//                    when(v){
//                        btnClose -> createFileViewModel.setAddFileActive(false)
//                        btnCreateFile ->
//                            if(edtCreatefile.text.isEmpty()) edtCreatefile.hint = "タイトルが必要です"
//                            else createFileViewModel.onClickFinish(edtCreatefile.text!!.toString())
//                    }
//                    colPaletBinding.apply {
//                        createFileViewModel.apply {
//                            when(v){
//                                imvColYellow -> onClickColorPalet(ColorStatus.YELLOW)
//                                imvColGray -> onClickColorPalet(ColorStatus.GRAY)
//                                imvColBlue -> onClickColorPalet(ColorStatus.BLUE)
//                                imvColRed -> onClickColorPalet(ColorStatus.RED)
//                                imvIconPalet -> lineLayColorPalet.children.iterator().forEachRemaining {
//                                    it.visibility = if(it.visibility == VISIBLE) GONE else VISIBLE
//                                }
//                            }
//                        }
//
//                    }
//                }
            }



        }

    }

//    Toast.makeText(this@MainActivity,"called",Toast.LENGTH_SHORT).show()


    fun animateVisibility(view:View,visibility:Int){
        Animation().apply {
            if(view.visibility==visibility) return
            else{
                when(view.id){
                    R.id.popup_add_file -> animatePopUpAddFile(view as FrameLayout,visibility)
                    R.id.frame_bottomMenu->animateFrameBottomMenu(view as FrameLayout,visibility)
                    R.id.line_lay_color_palet ->animateColPallet(view as LinearLayoutCompat,visibility)
                }
            }
        }

    }


    fun changeColPalletCol(colorStatus: ColorStatus?, selected:Boolean, colorPaletBinding: ColorPaletBinding){


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
                else -> return
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