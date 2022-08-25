package com.example.tangochoupdated.activity

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.application.RoomApplication
import com.example.tangochoupdated.databinding.ItemColorPaletBinding
import com.example.tangochoupdated.databinding.MainActivityBinding
import com.example.tangochoupdated.databinding.MainActivityBottomNavigationBarBinding
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.Tab
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.view_set_up.SearchViewModel
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.viewmodel.*

class MainActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var factory: ViewModelFactory
    private lateinit var  navHostFragment : NavHostFragment
    lateinit var mainActivityViewModel: BaseViewModel
    lateinit var createFileViewModel: CreateFileViewModel
    lateinit var createCardViewModel: CreateCardViewModel
    lateinit var stringCardViewModel: StringCardViewModel
    lateinit var libraryViewModel: LibraryViewModel
    lateinit var searchViewModel:SearchViewModel

    private lateinit var binding: MainActivityBinding

    private val  mainActivityClickableItem = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


//      全部のデータを消したいとき
//        applicationContext.deleteDatabase("my_database")


//        ー－－－mainActivityのviewー－－－

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        navController の宣言
        navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragContainerView.id) as NavHostFragment
        val navCon = navHostFragment.navController
        binding.apply {
//        Focus request
            mainTopConstrainLayout.requestFocus()
//            初期view設定
            binding.apply {
                popupAddFile.visibility = GONE
                frameBottomMenu.visibility = GONE
                makeAllColPaletUnselected(bindingCreateFile.colPaletBinding)
            }

//       　　 viewをclickListenerに追加
            mainActivityClickableItem.apply {
                addAll(arrayOf(fragConViewCover, mainTopConstrainLayout))
                bnvBinding.bnvRoot.children.iterator().forEach {
                    add(it)
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
        val ankiInBoxViewModel = ViewModelProvider(this, factory
        )[AnkiBoxFragViewModel::class.java]
        ankiInBoxViewModel.onCreate()
        val ankiBaseViewModel = ViewModelProvider(this,factory)[AnkiFragBaseViewModel::class.java]
        val ankiFlipFragViewModel = ViewModelProvider(this,factory)[AnkiFlipFragViewModel::class.java]
        val ankiSettingViewModel = ViewModelProvider(this)[AnkiSettingPopUpViewModel::class.java]
        val typedAnswerViewModel = ViewModelProvider(this)[AnkiFlipTypeAndCheckViewModel::class.java]
        stringCardViewModel = ViewModelProvider(this)[StringCardViewModel::class.java]
        searchViewModel = ViewModelProvider(this,factory)[SearchViewModel::class.java]
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
            binding.apply {
                var previousTab:Tab? = null
                activeTab.observe(this@MainActivity){
                    changeTabView(previousTab,it,bnvBinding)
                    previousTab = it
                }
//                bnvLayout3View.observe(this@MainActivity){
//                    imv3.setImageDrawable(getDrawable(it.drawableId))
//                    imv3.setPadding(it.padding)
//                    txv3.text = it.tabName
//                    txv3.visibility = when(it.tabNameVisibility){
//                        true -> VISIBLE
//                        false -> GONE
//                    }
//                }
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
            onCreate()
//            action.observe(this@MainActivity){
//                navCon.navigate(it)
//                Toast.makeText(this@MainActivity, "action called ", Toast.LENGTH_SHORT).show()
//            }
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
            finalRVList.observe(this@MainActivity){
                createFileViewModel.setPosition(it.size+1)
            }
        }
//        ー－－－ー－－－

//        ー－－－CreateCardViewModelの読み取りー－－－

        createCardViewModel.apply{
            onCreateViewModel()
            var calledFirstTime = true
            lastInsertedCard.observe(this@MainActivity){

                if(!calledFirstTime){
                    createCardViewModel.setLastInsertedCard(it)
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
                        imvnewCard ->  {
                            createCardViewModel.onClickAddNewCardBottomBar()
                            fragConViewCover.visibility = GONE
                        }
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
                                imvIconPalet -> arrayOf(imvColYellow,
                                    imvColGray,
                                    imvColBlue,
                                    imvColRed,).onEach { it.visibility = View.GONE }
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
                        bnvImvTabLibrary,bnvTxvTabLibrary -> mainActivityViewModel.onClickTab1()
                        bnvImvTabAnki,bnvTxvTabAnki       -> mainActivityViewModel.onClickTab3()
                        bnvImvAdd                         -> {
                            animateVisibility(frameBottomMenu, VISIBLE)
                            fragConViewCover.visibility = VISIBLE
                            createFileViewModel.onClickImvAddBnv()
                        }
                    }
                }
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
                    R.id.linLay_col_pallet ->animateColPallet(view as LinearLayoutCompat,visibility)
                }
            }
        }

    }


    fun changeColPalletCol(colorStatus: ColorStatus?, selected:Boolean?, colorPaletBinding: ItemColorPaletBinding){
        val imageView:ImageView
        val col:Int
        colorPaletBinding.apply {
            when(colorStatus) {
                ColorStatus.GRAY -> {
                    col = getColor(this@MainActivity, R.color.gray)
                    imageView = this.imvColGray
                }
                ColorStatus.BLUE -> {
                    col = getColor(this@MainActivity, R.color.blue)
                    imageView = this.imvColBlue
                }
                ColorStatus.YELLOW -> {
                    col = getColor(this@MainActivity, R.color.yellow)
                    imageView = this.imvColYellow
                }
                ColorStatus.RED -> {
                    col = getColor(this@MainActivity, R.color.red)
                    imageView = this.imvColRed
                }
                else -> return
            }
        }
        val a = imageView.drawable as GradientDrawable
        a.mutate()

        when(selected){
            true -> {
                a.setStroke(5,getColor(this@MainActivity, R.color.black))
                a.setColor(col)
                imageView.alpha = 1f
                imageView.background = a
                imageView.elevation = 10f
            }
            false -> {
                a.setStroke(5,getColor(this@MainActivity, R.color.ofwhite))
                a.setColor(col)
                imageView.alpha = 0.4f
                imageView.elevation = 0f
            }
            else -> return
        }
        imageView.setImageDrawable(a)

    }

    fun makeAllColPaletUnselected(colorPaletBinding: ItemColorPaletBinding){
        changeColPalletCol(ColorStatus.RED,false,colorPaletBinding)
        changeColPalletCol(ColorStatus.YELLOW,false,colorPaletBinding)
        changeColPalletCol(ColorStatus.BLUE,false,colorPaletBinding)
        changeColPalletCol(ColorStatus.GRAY,false,colorPaletBinding)
    }
    fun changeTabView(previous:Tab?,tab: Tab,bnv: MainActivityBottomNavigationBarBinding){
        bnv.apply {
            val nowImv:ImageView
            val nowTxv:TextView
            val selectedDraw:Drawable
            when(tab){
                Tab.TabAnki -> {
                    nowImv = bnvImvTabAnki
                    nowTxv = bnvTxvTabAnki
                    selectedDraw = getDrawable(R.drawable.icon_tab_anki)!!
                }
                Tab.TabLibrary -> {
                    nowImv = bnvImvTabLibrary
                    nowTxv = bnvTxvTabLibrary
                    selectedDraw = getDrawable(R.drawable.icon_flashcard_with_col)!!
                }
                else -> return
            }
            nowTxv.visibility = GONE
            nowImv.setImageDrawable(selectedDraw)

            val preImv:ImageView
            val preTxv:TextView
            val selectableDraw:Drawable
            when(previous){
                Tab.TabAnki -> {
                    preImv = bnvImvTabAnki
                    preTxv = bnvTxvTabAnki
                    selectableDraw = getDrawable(R.drawable.icon_tab_anki)!!
                }
                Tab.TabLibrary -> {
                    preImv = bnvImvTabLibrary
                    preTxv = bnvTxvTabLibrary
                    selectableDraw = getDrawable(R.drawable.icon_flashcard)!!
                }
                else -> return
            }
            preTxv.visibility = VISIBLE
            preImv.setImageDrawable(selectableDraw)


        }

    }

}