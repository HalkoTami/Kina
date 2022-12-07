package com.koronnu.kina.ui.fragment.base_frag_con

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.R
import com.koronnu.kina.databinding.CreateCardFragMainBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.customClasses.enumClasses.NeighbourCardSide
import com.koronnu.kina.ui.view_set_up.GetCustomDrawables
import com.koronnu.kina.ui.viewmodel.*


class EditCardBaseFrag  : Fragment(),View.OnClickListener {
//    private val args : EditCardBaseFragArgs by navArgs()
    private var _binding: CreateCardFragMainBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiBaseViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val createCardViewModel:CreateCardViewModel by activityViewModels()
    private val stringCardViewModel : CardTypeStringViewModel by activityViewModels()
    private val libraryViewModel: LibraryBaseViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()

    private lateinit var mainNavCon:NavController
    private lateinit var cardNavCon:NavController

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        fun setLateInitVars(){
            _binding =  CreateCardFragMainBinding.inflate(inflater, container, false)
            val a = childFragmentManager.findFragmentById(binding.createCardFragCon.id) as NavHostFragment
            cardNavCon = a.navController
            createCardViewModel.setCreateCardBaseBinding(binding)
            mainNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.frag_container_view).findNavController()
        }
        fun addClickListeners(){
            binding.apply {
                arrayOf(
                    createCardTopBarBinding.imvSaveAndBack,
                    btnInsertPrevious,
                    btnPrevious,
                    btnNext,
                    btnInsertNext,
                ).onEach { it.setOnClickListener(this@EditCardBaseFrag) }
            }
        }
        fun setAlphaByClickable(clickable:Boolean,view: View){
            view.alpha=if(clickable) 1f else 0.5f
        }
        var calledFirst = true

        val parentCardObserver = Observer<Card?>{ card->
            val previousCard = createCardViewModel.getNeighbourCardId(NeighbourCardSide.PREVIOUS)
            val parentCard = createCardViewModel.returnParentCard()
            val nextCard = createCardViewModel.getNeighbourCardId(NeighbourCardSide.NEXT)
            val sisterCards = createCardViewModel.returnSisterCards()
            binding.apply {
                binding.createCardTopBarBinding.txvPosition.text =
                    "${sisterCards.indexOf(parentCard)+1}/${sisterCards.size}"
                setAlphaByClickable(nextCard!=null, binding.btnNext)
                setAlphaByClickable(previousCard!=null, binding.btnPrevious)
                stringCardViewModel.setParentCard(card)
            }
        }
        val sisterCardObserver = Observer<List<Card>?> {
            val parentCard = createCardViewModel.returnParentCard()
            if(parentCard!=null){
                binding.createCardTopBarBinding.txvPosition.text =
                    "${it.indexOf(parentCard)+1}/${it.size}"

            }
        }
        val sisterCardFromDBObserver = Observer<List<Card>> {
            val startingCardId = createCardViewModel.returnStartingCardId()
            if(calledFirst){
                createCardViewModel.onClickEditCard(it.find { it.id == startingCardId } ?:return@Observer,cardNavCon)
                calledFirst = false
            }
        }
        val parentFlashCardCoverObserver = Observer<File?> {
            binding.createCardTopBarBinding.apply {
                imvMode.setImageDrawable(
                    if(it!=null){
                        GetCustomDrawables(requireActivity()).getFlashCardIconByCol(it.colorStatus)
                    } else  GetCustomDrawables(requireActivity()).getDrawable(R.drawable.icon_inbox)

                )
//                txvEditingFileTitle.text = it?.title ?:"InBox"
            }
        }
        val parentFlashCardCoverId =  when(mainViewModel.returnFragmentStatus()?.now){
            MainFragment.Anki -> when(ankiBaseViewModel.returnActiveFragment()) {
                AnkiFragments.AnkiBox -> null
                AnkiFragments.Flip -> flipBaseViewModel.returnParentCard()?.belongingFlashCardCoverId
                else    -> null
            }
            MainFragment.Library -> libraryViewModel.returnParentFile()?.fileId
            else -> null
        }

        setLateInitVars()
        addClickListeners()

        mainViewModel.setChildFragmentStatus(MainFragment.EditCard)
        mainViewModel.setBnvVisibility(false)
        editFileViewModel.setBottomMenuVisible(false)

        createCardViewModel.apply {
            parentCard.observe(viewLifecycleOwner,parentCardObserver)
            sisterCards.observe(viewLifecycleOwner,sisterCardObserver)
            parentFlashCardCover.observe(viewLifecycleOwner,parentFlashCardCoverObserver)
            getSisterCards(parentFlashCardCoverId).observe(viewLifecycleOwner,sisterCardFromDBObserver)
        }

//        val startingCardId =args.startEditCardId
//        var started = true
//        if(started){
//            createCardViewModel.getParentCard(startingCardId).observe(viewLifecycleOwner){
//                val action = EditCardFragDirections.flipCreateCard()
//                action.cardId = intArrayOf(it.id)
//                val flashCardCoverId = it.belongingFlashCardCoverId
//                action.parentFlashCardCoverId =if(flashCardCoverId!=null) intArrayOf(flashCardCoverId) else null
//                cardNavCon.navigate(action)
//                started = false
//            }
//
//        }


        val root: View = binding.root
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {


                val a = mainNavCon.backQueue.size
                if(a>0){
                    while( mainNavCon.backQueue[mainNavCon.backQueue.size-1].destination.label.toString()==getString(R.string.nav_label_main_create_card)){
                        mainNavCon.popBackStack()
                    }
                }

            }
        }
        if(mainViewModel.returnGuideVisibility().not()){
            requireActivity().onBackPressedDispatcher.addCallback(
                this,  // LifecycleOwner
                callback
            )
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View?) {
        binding.apply {
            when(p0){
                createCardTopBarBinding.imvSaveAndBack  ->{
                    mainNavCon.popBackStack()
                }

                btnInsertNext,btnInsertPrevious         -> {
                    when(p0){
                        btnInsertPrevious                    -> createCardViewModel. onClickBtnInsert(
                            NeighbourCardSide.PREVIOUS)
                        btnInsertNext                           -> createCardViewModel. onClickBtnInsert(
                            NeighbourCardSide.NEXT)
                    }
                    createCardViewModel.checkMakePopUpVisible(mainViewModel.returnFragmentStatus() ?:return,ankiBaseViewModel.returnActiveFragment())
                }
                btnNext                                 -> createCardViewModel.onClickBtnNavigate(cardNavCon,
                    NeighbourCardSide.NEXT)
                btnPrevious                             -> createCardViewModel.onClickBtnNavigate(cardNavCon,
                    NeighbourCardSide.PREVIOUS)

            }
        }

    }
}