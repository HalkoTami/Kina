package com.example.tangochoupdated.ui.create.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.StringFragFocusedOn
import com.example.tangochoupdated.ui.create.Mode
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel


class CreateCardFragment: Fragment(),View.OnClickListener {

    private var _binding: CreateCardBaseBinding? = null
    private val binding get() = _binding!!

    private val args :CreateCardFragmentArgs by navArgs()

    private val clickableViews = mutableListOf<View>()

    private val  createCardViewModel: CreateCardViewModel by activityViewModels()
//    private lateinit var stringCardViewModel: StringCardViewModel
    private val mainViewModel : BaseViewModel by activityViewModels()
    private val stringCardViewModel :StringCardViewModel by activityViewModels()

    lateinit var mainNavCon:NavController

    var finish = false



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = CreateCardBaseBinding.inflate(inflater, container, false)
        mainNavCon = requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).id)

        binding.apply {
            createCardViewModel.apply{
//            初期設定
                onStart()

//            DBからデータ下ろす
                getParentCard(args.cardId?.single()).observe(viewLifecycleOwner){ cardAndTags->
                    if(cardAndTags!= null){
                        setParentCard(cardAndTags)
                        binding.txvTitle.text = cardAndTags.card.id.toString()
                    }
                    stringCardViewModel.setStringData(cardAndTags?.card?.stringData)
                }
                getSisterCards(args.parentFlashCardCoverId?.single()).observe(viewLifecycleOwner){ sisters ->
                    setSisterCards(sisters!!)
                }
                getParentFlashCardCover(args.parentFlashCardCoverId?.single()).observe(viewLifecycleOwner){ file ->
                    setParentFlashCardCover(file)
                }

//              編集モード/新しいカード
                mode.observe(viewLifecycleOwner){
                    requireActivity().apply {
                        binding.imvMode.setImageDrawable(when(it){
                            Mode.New -> getDrawable(R.drawable.icon_edit)
                            Mode.Edit -> getDrawable(R.drawable.icon_eye_opened)
                        })
                    }

                }
//                現在地 text view
                txvPositionText.observe(viewLifecycleOwner){
                    binding.txvPosition.text = it
                }
//                新しいカードが挿入完了したら
                var calledFirstTime = true
                createCardViewModel.lastInsertedCardAndTags.observe(viewLifecycleOwner){
                    if(!calledFirstTime){
                        createCardViewModel.setLastInsertedCardAndTags(it)
                    }
                    calledFirstTime = false
                }

//            Color Pallet attribute
               createCardColPaletBinding.apply {
//                   addToClickListener
                   root.children.iterator().forEachRemaining {
                       clickableViews.add(it)
                   }

                   val palletBinding = this
//                    pallet visibility
                   lineLayColorPalet.visibility.apply {
                       colPalletVisibility.observe(viewLifecycleOwner){
                           when(it){
                               true -> View.VISIBLE
                               false ->View.GONE
                           }
                       }
                   }
//                   Color Pallet attribute 2. change  each color
                   var previousColor: ColorStatus?
                   previousColor = null
                   val mainActivity = activity as MainActivity
                   cardColor.observe(viewLifecycleOwner){
                       mainActivity.apply {
                           if(previousColor == null) makeAllColPaletUnselected(palletBinding)
                           else changeColPalletCol(previousColor!!,false,palletBinding)
                           changeColPalletCol(it,true,palletBinding)
                           previousColor = it
                       }
                   }
               }




                binding.apply {
                    clickableViews.add(imvSaveAndBack)
                    clickableViews.add(imvMode)
                    clickableViews.addAll(arrayOf(btnNext,btnPrevious))

                }

//            移動ボタン





            }
        }

        clickableViews.onEach {
            it.setOnClickListener(this)
        }

        return binding.root
    }

    override fun onClick(v: View?) {
        createCardViewModel.apply {
            binding.apply {
                createCardColPaletBinding.apply {
                        when(v){
                            imvIconPalet -> onClickColPaletIcon()
                            imvColBlue -> onClickEachColor(ColorStatus.BLUE)
                            imvColRed -> onClickEachColor(ColorStatus.RED)
                            imvColGray -> onClickEachColor(ColorStatus.GRAY)
                            imvColYellow -> onClickEachColor(ColorStatus.YELLOW)
                        }
                }
                when(v){
                    imvSaveAndBack -> {
//                        createCardViewModel.onClickSaveAndBack()
                        stringCardViewModel.setFocusedOn(StringFragFocusedOn.FrontContent)

                    }
                    imvMode -> stringCardViewModel.setFocusedOn(StringFragFocusedOn.BackContent)
                    //  移動操作
                    btnNext ->  onClickBtnNext()
                    btnPrevious -> onClickBtnPrevious()

                }



            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {

                createCardViewModel.onClickBack()
                mainNavCon.popBackStack()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }
}