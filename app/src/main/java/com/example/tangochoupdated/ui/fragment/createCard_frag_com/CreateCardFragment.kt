package com.example.tangochoupdated.ui.fragment.createCard_frag_com

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.*
import com.example.tangochoupdated.activity.MainActivity
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FragmentTree
import com.example.tangochoupdated.db.enumclass.Mode
import com.example.tangochoupdated.db.enumclass.MainFragment
import com.example.tangochoupdated.ui.fragment.base_frag_con.LibraryFragmentBase
import com.example.tangochoupdated.ui.viewmodel.StringCardViewModel
import com.example.tangochoupdated.ui.viewmodel.BaseViewModel
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel


class CreateCardFragment: Fragment(),View.OnClickListener {

    private var _binding: CreateCardFragBaseBinding? = null
    private val binding get() = _binding!!

    private val args : CreateCardFragmentArgs by navArgs()

    private val clickableViews = mutableListOf<View>()
    private val baseViewModel: BaseViewModel by activityViewModels()
    private val  createCardViewModel: CreateCardViewModel by activityViewModels()
//    private lateinit var stringCardViewModel: StringCardViewModel
    private val mainViewModel : BaseViewModel by activityViewModels()
    private val stringCardViewModel : StringCardViewModel by activityViewModels()

    lateinit var cardNavCon:NavController

    var finish = false



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = CreateCardFragBaseBinding.inflate(inflater, container, false)
        cardNavCon =requireActivity().findViewById<FragmentContainerView>(R.id.create_card_frag_con).findNavController()
        val mainNavCon =  requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).id)
        baseViewModel.apply {
//            val activeFragment = returnActiveFragment() ?: FragmentTree()
//            activeFragment.startFragment = MainFragment.EditCard
            setActiveFragment(MainFragment.EditCard)
        }

        val vlo = viewLifecycleOwner
        binding.apply {
            binding.root.requestFocus()

            createCardViewModel.apply{
//            初期設定
                onStartFrag()

//            DBからデータ下ろす
                getParentCard(args.cardId?.single()).observe(viewLifecycleOwner){ cardAndTags->
                    if(cardAndTags!= null){
                        setParentCard(cardAndTags)
                        createCardTopBarBinding.txvEditingFileTitle.text = cardAndTags.id.toString()
                    }
                    stringCardViewModel.setStringData(cardAndTags?.stringData)
                }
                getSisterCards(args.parentFlashCardCoverId?.single()).observe(viewLifecycleOwner){ sisters ->
                    setSisterCards(sisters!!)
                }
                getParentFlashCardCover(args.parentFlashCardCoverId?.single()).observe(viewLifecycleOwner){ file ->
                    setParentFlashCardCover(file)
                }


//                Top Frame
                createCardTopBarBinding.apply {

//                現在地 text view
                    txvPositionText.observe(viewLifecycleOwner){
                        txvPosition.text = it
                    }

//                編集モード/新しいカード
                    mode.observe(viewLifecycleOwner){
                        requireActivity().apply {
                            imvMode.setImageDrawable(when(it){
                                Mode.New -> getDrawable(R.drawable.icon_edit)
                                Mode.Edit -> getDrawable(R.drawable.icon_eye_opened)
                                else -> return@observe
                            })
                        }
                    }
//                    add to click listener
                    clickableViews.addAll(arrayOf(
                        imvSaveAndBack
                    )
                    )


                }
//            Color Pallet attribute
               createCardColPaletBinding.apply {
                   val palletBinding = this


//                   Color Pallet attribute 2. change  each color
                   var previousColor: ColorStatus?
                   previousColor = null
                   val mainActivity = LibraryFragmentBase()
                   cardColor.observe(viewLifecycleOwner){
                       mainActivity.apply {
                           if(previousColor == null) makeAllColPaletUnselected(palletBinding)
                           else changeColPalletCol(previousColor!!,false,palletBinding)
                           changeColPalletCol(it,true,palletBinding)
                           previousColor = it
                       }
                   }

                   //                   addToClickListener
                   clickableViews.addAll(arrayOf(
                       imvIconPalet,imvColYellow,imvColRed,imvColGray,imvColBlue
                   ))

               }
//                移動ボタン
                val gray = ContextCompat.getColor(requireActivity(), R.color.light_gray)
                layNavigateButtons.apply {
                    btnPrevious.apply {
                        previousCardExists.observe(viewLifecycleOwner){
                            when(it){
                                true ->  alpha = 1f
                                false -> alpha = 0.5f
                            }
                        }
                    }
                    btnNext.apply {
                        nextCardExists.observe(viewLifecycleOwner){
                            when(it){
                                true ->  alpha = 1f
                                false -> alpha = 0.5f
                            }
                        }
                    }

//                    add to click listener
                    clickableViews.addAll( arrayOf(
                        btnInsertPrevious,btnPrevious,btnNext,btnInsertNext
                    )
                    )


                }






            }
        }

        clickableViews.onEach {
            it.setOnClickListener(this)
        }

        return binding.root
    }

    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                createCardTopBarBinding.imvSaveAndBack  -> {
                    createCardViewModel.onClickBack()
                    cardNavCon.popBackStack()
                }
                //  移動操作
                btnInsertPrevious                       -> createCardViewModel. onClickBtnInsertPrevious()
                btnInsertNext                           -> createCardViewModel. onClickBtnInsertNext()
                btnNext                                 ->  createCardViewModel. onClickBtnNext(cardNavCon)
                btnPrevious                             -> createCardViewModel. onClickBtnPrevious(cardNavCon)
                createCardColPaletBinding.imvIconPalet  -> {
                    MainActivity().animateVisibility(createCardColPaletBinding.linLayColPallet,
                        if(v.tag == View.VISIBLE) View.GONE else View.VISIBLE)
                }
                createCardColPaletBinding.imvColBlue    ->    createCardViewModel.onClickEachColor(ColorStatus.BLUE)
                createCardColPaletBinding.imvColRed     ->     createCardViewModel.onClickEachColor(ColorStatus.RED)
                createCardColPaletBinding.imvColGray    ->    createCardViewModel.onClickEachColor(ColorStatus.GRAY)
                createCardColPaletBinding.imvColYellow  ->createCardViewModel. onClickEachColor(ColorStatus.YELLOW)

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        createCardViewModel.onEndFrag()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {

                createCardViewModel.onClickBack()
                cardNavCon.popBackStack()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }
}