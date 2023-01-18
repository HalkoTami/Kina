package com.koronnu.kina.ui.fragment.base_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.R
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.customClasses.enumClasses.NeighbourCardSide
import com.koronnu.kina.databinding.CreateCardFragMainBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.ui.tabLibrary.LibraryBaseViewModel
import com.koronnu.kina.ui.view_set_up.GetCustomDrawables
import com.koronnu.kina.ui.viewmodel.*


class EditCardBaseFrag  : Fragment() {
//    private val args : EditCardBaseFragArgs by navArgs()
    private var _binding: CreateCardFragMainBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiBaseViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val createCardViewModel:CreateCardViewModel by activityViewModels()
    private val stringCardViewModel : CardTypeStringViewModel by activityViewModels()
    private val libraryViewModel: LibraryBaseViewModel by activityViewModels()
    private val editFileViewModel: EditFileViewModel by activityViewModels()


    private lateinit var cardNavCon:NavController

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        fun setLateInitVars() {
            _binding = CreateCardFragMainBinding.inflate(inflater, container, false)
            binding.apply {
                viewModel = createCardViewModel
                lifecycleOwner = viewLifecycleOwner
            }
            val a =
                childFragmentManager.findFragmentById(binding.createCardFragCon.id) as NavHostFragment
            cardNavCon = a.navController
        }


        fun setAlphaByClickable(clickable: Boolean, view: View) {
            view.alpha = if (clickable) 1f else 0.5f
        }

        var calledFirst = true

        val parentCardObserver = Observer<Card?> { card ->
            val previousCard = createCardViewModel.getNeighbourCardId(NeighbourCardSide.PREVIOUS)
            val parentCard = createCardViewModel.returnParentCard()
            val nextCard = createCardViewModel.getNeighbourCardId(NeighbourCardSide.NEXT)
            val sisterCards = createCardViewModel.returnSisterCards()
            binding.apply {
                val text = resources.getString(R.string.editCardProgress,(sisterCards.indexOf(parentCard) + 1),sisterCards.size)
                binding.createCardTopBarBinding.txvPosition.text = text
                setAlphaByClickable(nextCard != null, binding.btnNext)
                setAlphaByClickable(previousCard != null, binding.btnPrevious)
                stringCardViewModel.setParentCard(card)
            }
        }
        val sisterCardObserver = Observer<List<Card>?> {
            val parentCard = createCardViewModel.returnParentCard()
            if (parentCard != null) {
                val text = resources.getString(R.string.editCardProgress,(it.indexOf(parentCard) + 1),it.size)
                binding.createCardTopBarBinding.txvPosition.text =
                    text

            }
        }
        val sisterCardFromDBObserver = Observer<List<Card>> { cardList ->
            val startingCardId = createCardViewModel.returnStartingCardId()
            if (calledFirst) {
                createCardViewModel.onClickEditCard(cardList.find { it.id == startingCardId }
                    ?: return@Observer, cardNavCon)
                calledFirst = false
            }
        }
        val parentFlashCardCoverObserver = Observer<File?> {
            binding.createCardTopBarBinding.apply {
                imvMode.setImageDrawable(
                    if (it != null) {
                        GetCustomDrawables(requireActivity()).getFlashCardIconByCol(it.colorStatus)
                    } else GetCustomDrawables(requireActivity()).getDrawable(R.drawable.icon_inbox)

                )
            }
        }
        val parentFlashCardCoverId = when (mainViewModel.getFragmentStatus.now) {
            MainFragment.Anki -> when (ankiBaseViewModel.returnActiveFragment()) {
                AnkiFragments.AnkiBox -> null
                AnkiFragments.Flip -> flipBaseViewModel.getParentCard.belongingFlashCardCoverId
                else -> null
            }
            MainFragment.Library -> libraryViewModel.returnParentFile()?.fileId
            else -> null
        }

        setLateInitVars()
        binding.createCardTopBarBinding.imvSaveAndBack.setOnClickListener {
            requireActivity().findViewById<FragmentContainerView>(R.id.fcv_activityMain)
                .findNavController().popBackStack()
        }

        mainViewModel.setChildFragmentStatus(MainFragment.EditCard)
        mainViewModel.setBnvVisibility(false)
        editFileViewModel.setBottomMenuVisible(false)

        createCardViewModel.apply {
            setEditCardBaseFragNavDirection(null)
            parentCard.observe(viewLifecycleOwner, parentCardObserver)
            sisterCards.observe(viewLifecycleOwner, sisterCardObserver)
            parentFlashCardCover.observe(viewLifecycleOwner, parentFlashCardCoverObserver)
            getSisterCards(parentFlashCardCoverId).observe(viewLifecycleOwner,
                sisterCardFromDBObserver)
            editCardBaseFragNavDirection.observe(viewLifecycleOwner){
                if(it==null) return@observe
                cardNavCon.popBackStack()
                cardNavCon.navigate(it)
            }
        }



        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}