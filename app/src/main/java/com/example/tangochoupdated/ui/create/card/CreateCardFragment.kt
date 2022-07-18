package com.example.tangochoupdated.ui.create.card

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel

class CreateCardFragment: Fragment(),View.OnClickListener {

    private var _binding: CreateCardBaseBinding? = null
    private val binding get() = _binding!!
    private val args: CreateCardFragmentArgs by navArgs()

    private val clickableViews = mutableListOf<View>()

    private lateinit var createCardViewModel: CreateCardViewModel
    private lateinit var stringCardViewModel: StringCardViewModel
    private val mainViewModel : BaseViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository = (requireActivity().application as RoomApplication).repository
        val viewModelFactory = ViewModelFactory(repository)
        createCardViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[CreateCardViewModel::class.java]
        stringCardViewModel = ViewModelProvider(this@CreateCardFragment).get(StringCardViewModel::class.java)


        _binding = CreateCardBaseBinding.inflate(inflater, container, false)

        createCardViewModel.apply{
//            初期設定
            onStart()

//            DBからデータとってくる
            getParentCard(args.cardId?.single()).observe(viewLifecycleOwner){
                setParentCard(it)
                stringCardViewModel.setParentCard(it)

            }
            getParentFlashCardCover(args.parentFlashCardCoverId?.single()).observe(viewLifecycleOwner){
                setParentFlashCardCover(it)
                Toast.makeText(context, "${it?.title} ", Toast.LENGTH_SHORT).show()
            }

//            Color Pallet attribute 1 pallet visibility
            val palletBinding = binding.createCardColPaletBinding
            colPalletVisibility.observe(viewLifecycleOwner){
                val time:Long = 500
                when(it){
                    true -> palletBinding.lineLayColorPalet.visibility = View.VISIBLE
                    false ->palletBinding.lineLayColorPalet.visibility = View.GONE
                }
            }

//            Color Pallet attribute 2 each color
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
            binding.createCardColPaletBinding.apply {
                clickableViews.addAll(arrayOf(imvColBlue,imvColGray,imvColRed,imvColYellow,imvIconPalet))
            }







            clickableViews.onEach {
                it.setOnClickListener(this@CreateCardFragment)
            }

        }
        stringCardViewModel.stringData.observe(viewLifecycleOwner){
            createCardViewModel.setStringData(it)
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
                        onClickSaveAndBack()
                        stringCardViewModel.setSendStringData(true)
//                        activity!!.finish()
                    }
                }

            }
        }

    }
}