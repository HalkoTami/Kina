package com.example.tangochoupdated.ui.fragment.createCard_frag_com

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.*
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.view_set_up.ColorPalletViewSetUp
import com.example.tangochoupdated.ui.view_set_up.GetCustomDrawables
import com.example.tangochoupdated.ui.viewmodel.StringCardViewModel
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel


class CreateCardFragment: Fragment(),View.OnClickListener {

    private var _binding: CreateCardFragBaseBinding? = null
    private val binding get() = _binding!!

    private val args : CreateCardFragmentArgs by navArgs()
    private val  createCardViewModel: CreateCardViewModel by activityViewModels()
    private val stringCardViewModel : StringCardViewModel by activityViewModels()

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
        fun setAlphaByClickable(clickable:Boolean,view: View){
            view.alpha=if(clickable) 1f else 0.5f
        }
        fun addClickListeners(){
            binding.apply {
                arrayOf(
                    createCardTopBarBinding.imvSaveAndBack,
                    createCardColPaletBinding.imvIconPalet,
                    createCardColPaletBinding.imvColYellow,
                    createCardColPaletBinding.imvColRed,
                    createCardColPaletBinding.imvColGray,
                    createCardColPaletBinding.imvColBlue,
                    btnInsertPrevious,
                    btnPrevious,
                    btnNext,
                    btnInsertNext
                    ).onEach { it.setOnClickListener(this@CreateCardFragment) }
            }
        }

        setLateInitVars()
        addClickListeners()
        val frag = childFragmentManager.findFragmentById(binding.fragConEachCardType.id) as NavHostFragment
        cardTypeNavCon = frag.navController
        val parentCardFromDBObserver = Observer<Card>{ card->
            createCardViewModel.apply {
                setParentCard(card)
                binding.createCardTopBarBinding.txvPosition.text =
                    "${card.libOrder}/${returnSisterCards().size}"
                setAlphaByClickable(
                    getNeighbourCardId(CreateCardViewModel.NeighbourCardSide.NEXT)!=null,
                    binding.btnNext)
                setAlphaByClickable(
                    getNeighbourCardId(CreateCardViewModel.NeighbourCardSide.PREVIOUS)!=null,
                    binding.btnPrevious)
                stringCardViewModel.setParentCard(card)
            }

        }
        val sisterCardObserver = Observer<List<Card>?> {
            createCardViewModel.apply {
                val sort = it.sortedBy { it.libOrder }
                setSisterCards(sort)
                if(returnParentCard()!=null){
                    binding.createCardTopBarBinding.txvPosition.text =
                        "${returnParentCard()?.libOrder?:0}/${it.size}"
                }

            }
        }
        val parentFlashCardCoverObserver = Observer<File?> {
            createCardViewModel.setParentFlashCardCover(it)
            binding.createCardTopBarBinding.apply {
                imvMode.setImageDrawable(
                    if(it!=null){
                        GetCustomDrawables(requireActivity()).getFlashCardIconByCol(it.colorStatus)
                    } else  GetCustomDrawables(requireActivity()).getDrawable(R.drawable.icon_inbox)

                )
                txvEditingFileTitle.text = it?.title ?:"InBox"
            }
        }
        val colorPalletVisibilityObserver = Observer<Boolean> {
            Animation().animateColPallet(binding.createCardColPaletBinding.root,booleanToVisibility(it))
        }
        val cardColorObserver = Observer<CreateCardViewModel.ColorPalletStatus> {
            ColorPalletViewSetUp().apply {
                changeColPalletCol(requireActivity(),it.colNow,true,binding.createCardColPaletBinding)
                changeColPalletCol(requireActivity(),it.before,false,binding.createCardColPaletBinding)
            }
        }
        val cardId = args.cardId
        val parentFlashCardId:Int? = args.parentFlashCardCoverId?.single()
        if(cardId!=null){
            createCardViewModel. getParentCard(cardId.single()).observe(viewLifecycleOwner,parentCardFromDBObserver)
            createCardViewModel.getSisterCards(args.parentFlashCardCoverId?.single()).observe(viewLifecycleOwner,sisterCardObserver)
            createCardViewModel.getParentFlashCardCover(parentFlashCardId).observe(viewLifecycleOwner,parentFlashCardCoverObserver)
        }

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
                createCardTopBarBinding.imvSaveAndBack  ->{
                    mainNavCon.popBackStack()
                }
                btnInsertPrevious                       -> createCardViewModel. onClickBtnInsert(CreateCardViewModel.NeighbourCardSide.PREVIOUS)
                btnInsertNext                           -> createCardViewModel. onClickBtnInsert(CreateCardViewModel.NeighbourCardSide.NEXT)
                btnNext                                 -> createCardViewModel.onClickBtnNavigate(cardNavCon,CreateCardViewModel.NeighbourCardSide.NEXT)
                btnPrevious                             -> createCardViewModel.onClickBtnNavigate(cardNavCon,CreateCardViewModel.NeighbourCardSide.PREVIOUS)
                createCardColPaletBinding.imvIconPalet  -> createCardViewModel.changeColPalletVisibility()
                createCardColPaletBinding.imvColBlue    ->    createCardViewModel.setCardColor(ColorStatus.BLUE)
                createCardColPaletBinding.imvColRed     ->     createCardViewModel.setCardColor(ColorStatus.RED)
                createCardColPaletBinding.imvColGray    ->    createCardViewModel.setCardColor(ColorStatus.GRAY)
                createCardColPaletBinding.imvColYellow  ->createCardViewModel. setCardColor(ColorStatus.YELLOW)

            }
        }

    }


}