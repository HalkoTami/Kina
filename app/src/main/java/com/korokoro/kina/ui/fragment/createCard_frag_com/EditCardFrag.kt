package com.korokoro.kina.ui.fragment.createCard_frag_com

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.korokoro.kina.R
import com.korokoro.kina.databinding.CreateCardFragBaseBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.ColorStatus
import com.korokoro.kina.ui.animation.Animation
import com.korokoro.kina.ui.view_set_up.ColorPalletViewSetUp
import com.korokoro.kina.ui.viewmodel.AnkiBoxViewModel
import com.korokoro.kina.ui.viewmodel.CreateCardViewModel
import com.korokoro.kina.ui.customClasses.ColorPalletStatus


class EditCardFrag: Fragment(),View.OnClickListener {

    private var _binding: CreateCardFragBaseBinding? = null
    private val binding get() = _binding!!
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()
    private val args : EditCardFragArgs by navArgs()
    private val  createCardViewModel: CreateCardViewModel by activityViewModels()

    lateinit var cardNavCon:NavController
    lateinit var cardTypeNavCon:NavController
    lateinit var mainNavCon:NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fun getNavConFromFragCon(fragConId:Int):NavController{
            return requireActivity().findViewById<FragmentContainerView>(fragConId).findNavController()
        }
        fun setLateInitVars(){
            _binding = CreateCardFragBaseBinding.inflate(inflater, container, false)
            cardNavCon =getNavConFromFragCon(R.id.create_card_frag_con)
            mainNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).findNavController()
        }
        fun booleanToVisibility(visibility: Boolean):Int{
            return if(visibility) View.VISIBLE else View.GONE
        }
        fun addClickListeners(){
            binding.apply {
                arrayOf(
                    createCardColPalletBinding.imvIconPalet,
                    createCardColPalletBinding.imvColYellow,
                    createCardColPalletBinding.imvColRed,
                    createCardColPalletBinding.imvColGray,
                    createCardColPalletBinding.imvColBlue,
                    confirmAddToFlipItemBinding.btnCloseConfirm,
                    confirmAddToFlipItemBinding.btnCommitAdd,
                    confirmAddToFlipItemBinding.btnNotAdd,
                    frameLayPopupConfirmAddToFlipItem
                    ).onEach { it.setOnClickListener(this@EditCardFrag) }
            }
        }

        setLateInitVars()
        addClickListeners()
        val frag = childFragmentManager.findFragmentById(binding.fragConEachCardType.id) as NavHostFragment
        cardTypeNavCon = frag.navController
        val parentCardFromDBObserver = Observer<Card>{ card->
            createCardViewModel.setParentCard(card)

        }
        val sisterCardObserver = Observer<List<Card>?> {
            createCardViewModel.apply {
                val sort = it.sortedBy { it.libOrder }
                setSisterCards(sort)

            }
        }
        val parentFlashCardCoverObserver = Observer<File?> {
            createCardViewModel.setParentFlashCardCover(it)
        }
        val colorPalletVisibilityObserver = Observer<Boolean> {
            Animation().animateColPallet(binding.createCardColPalletBinding.root,booleanToVisibility(it))
        }
        val cardColorObserver = Observer<ColorPalletStatus> {
            ColorPalletViewSetUp().apply {
                changeColPalletCol(requireActivity(),it.colNow,true,binding.createCardColPalletBinding)
                changeColPalletCol(requireActivity(),it.before,false,binding.createCardColPalletBinding)
            }
        }
        val confirmPopUpVisibilityObserver = Observer<Boolean>{
            binding.frameLayPopupConfirmAddToFlipItem.visibility = booleanToVisibility(it)
        }
        val cardId = args.cardId
        val parentFlashCardId:Int? = args.parentFlashCardCoverId?.single()
        if(cardId!=null){
            createCardViewModel. getParentCard(cardId.single()).observe(viewLifecycleOwner,parentCardFromDBObserver)


        }
        var lastCardOld: Card? = null
        createCardViewModel.lastInsertedCard.observe(viewLifecycleOwner) {
            if(lastCardOld!=null&&lastCardOld!=it&&lastCardOld!!.id<it.id){
                createCardViewModel.onClickEditCard(it,cardNavCon)
            }
            lastCardOld = it
        }
        createCardViewModel.getParentFlashCardCover(parentFlashCardId).observe(viewLifecycleOwner,parentFlashCardCoverObserver)
        createCardViewModel.getSisterCards(args.parentFlashCardCoverId?.single()).observe(viewLifecycleOwner,sisterCardObserver)
        createCardViewModel.confirmFlipItemPopUpVisible.observe(viewLifecycleOwner,confirmPopUpVisibilityObserver)
        createCardViewModel.colPalletVisibility.observe(viewLifecycleOwner,colorPalletVisibilityObserver)
        createCardViewModel.cardColor.observe(viewLifecycleOwner,cardColorObserver)




        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                confirmAddToFlipItemBinding.btnCloseConfirm,confirmAddToFlipItemBinding.btnNotAdd -> createCardViewModel.setConfirmFlipItemPopUpVisible(false)
                confirmAddToFlipItemBinding.btnCommitAdd -> {
                    ankiBoxViewModel.addToAnkiBoxCardIds(listOf(args.cardId?.single() ?:return))
                    createCardViewModel.setConfirmFlipItemPopUpVisible(false)
                }
                createCardColPalletBinding.imvIconPalet  -> createCardViewModel.changeColPalletVisibility()
                createCardColPalletBinding.imvColBlue    ->    createCardViewModel.setCardColor(ColorStatus.BLUE)
                createCardColPalletBinding.imvColRed     ->     createCardViewModel.setCardColor(ColorStatus.RED)
                createCardColPalletBinding.imvColGray    ->    createCardViewModel.setCardColor(ColorStatus.GRAY)
                createCardColPalletBinding.imvColYellow  ->createCardViewModel. setCardColor(ColorStatus.YELLOW)

            }
        }

    }


}