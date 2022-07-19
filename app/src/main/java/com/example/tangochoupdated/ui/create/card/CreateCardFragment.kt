package com.example.tangochoupdated.ui.create.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.ui.create.Mode
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel

class CreateCardFragment: Fragment(),View.OnClickListener {

    private var _binding: CreateCardBaseBinding? = null
    private val binding get() = _binding!!
    private val args: CreateCardFragmentArgs by navArgs()

    private val clickableViews = mutableListOf<View>()

    private val  createCardViewModel: CreateCardViewModel by activityViewModels()
//    private lateinit var stringCardViewModel: StringCardViewModel
    private val mainViewModel : BaseViewModel by activityViewModels()
    private val stringCardViewModel :StringCardViewModel by viewModels()

    lateinit var mainNavCon:NavController



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = CreateCardBaseBinding.inflate(inflater, container, false)
        mainNavCon = requireActivity().findNavController(requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).id)

        createCardViewModel.apply{
//            初期設定
            onStart()

            setParentCardId(args.cardId?.single())
            getParentFlashCardCover(args.parentFlashCardCoverId?.single()).observe(viewLifecycleOwner){
                setParentFlashCardCover(it)
            }
//            DBからデータとってくる
            getSisterCards(args.parentFlashCardCoverId?.single()).observe(viewLifecycleOwner){
                setSisterCards(it)
            }
            filterAndSetParentCard(args.cardId?.single())
            getParentCard(args.cardId?.single()).observe(viewLifecycleOwner){
                setParentCard(it?.card)
            }

            val edit = requireActivity().getDrawable(R.drawable.icon_edit)
            val new = requireActivity().getDrawable(R.drawable.icon_eye_opened)
            mode.observe(viewLifecycleOwner){
                binding.imvMode.setImageDrawable(when(it){
                    Mode.Create -> new
                    Mode.Edit -> edit
                })
            }


//            top Bar
//            parentFlashCardCover.observe(viewLifecycleOwner){
//                binding.txvTitle.text = it?.title ?:"単語帳に入ってないよ"
//            }
            txvPositionText.observe(viewLifecycleOwner){
                binding.txvPosition.text = it
            }
            parentCard.observe(viewLifecycleOwner){
                binding.txvTitle.text = it?.id.toString()
            }






//            Color Pallet attribute 1. pallet visibility
            val palletBinding = binding.createCardColPaletBinding
            colPalletVisibility.observe(viewLifecycleOwner){
                val time:Long = 500
                when(it){
                    true -> palletBinding.lineLayColorPalet.visibility = View.VISIBLE
                    false ->palletBinding.lineLayColorPalet.visibility = View.GONE
                }
            }

//            Color Pallet attribute 2. change  each color
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

            binding.apply {
                clickableViews.add(imvSaveAndBack)
                clickableViews.addAll(arrayOf(btnNext,btnPrevious))

                createCardColPaletBinding.apply {
                    clickableViews.addAll(arrayOf(imvColBlue,imvColGray,imvColRed,imvColYellow,imvIconPalet))
                }
            }

//            移動ボタン




        }
//        stringCardViewModel.stringData.observe(viewLifecycleOwner){
//            createCardViewModel.setStringData(it)
//            stringCardViewModel.setSendStringData(false)
//        }

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
                        createCardViewModel.onClickSaveAndBack()
                        Toast.makeText(context, "onsave clicked", Toast.LENGTH_SHORT).show()
                    }
                    //  移動操作
                    btnNext ->  onClickBtnNext()
                    btnPrevious -> return

                }



            }
        }

    }
}