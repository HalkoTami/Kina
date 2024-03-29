package com.koronnu.kina.ui.editCard.editCardContent

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
import com.koronnu.kina.R
import com.koronnu.kina.databinding.CreateCardFragBaseBinding
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxViewModel
import com.koronnu.kina.ui.editCard.CreateCardViewModel


class EditCardFrag: Fragment() {

    private var _binding: CreateCardFragBaseBinding? = null
    private val binding get() = _binding!!
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()
    private val args : com.koronnu.kina.ui.editCard.editCardContent.EditCardFragArgs by navArgs()
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
            mainNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.fcv_activityMain).findNavController()
        }
        fun booleanToVisibility(visibility: Boolean):Int{
            return if(visibility) View.VISIBLE else View.GONE
        }

        setLateInitVars()
        val frag = childFragmentManager.findFragmentById(binding.fragConEachCardType.id) as NavHostFragment
        cardTypeNavCon = frag.navController
        val parentCardFromDBObserver = Observer<Card>{ card->
//            createCardViewModel.setParentCard(card)

        }
        val flashCardAndChildrenCardsObserver = Observer<Map<File,List<Card>>>{
            val file = it.keys
            val cards = it.values
            if(it.keys.size == 1 && it.values.size == 1)
            { createCardViewModel.setParentFlashCardCover(it.keys.single())
                val parentCard = it.values.single().find { it.id == args.cardId?.single() }
                createCardViewModel.setParentCard(parentCard)
                createCardViewModel.setSisterCards(it.values.single())

            }

        }



        val cardId = args.cardId
        val parentFlashCardId:Int? = args.parentFlashCardCoverId?.single()
        if(cardId!=null){
            createCardViewModel. getParentCard(cardId.single()).observe(viewLifecycleOwner,parentCardFromDBObserver)


        }
        var lastCardOld: Card? = null
        createCardViewModel.lastInsertedCard.observe(viewLifecycleOwner) {
            if(it!=null&& lastCardOld!=null&&lastCardOld!=it&&lastCardOld!!.id<it.id){
                createCardViewModel.onClickEditCard(it,cardNavCon)
            }
            lastCardOld = it
        }



        if(parentFlashCardId==null){
            createCardViewModel.cardsWithoutCardsFlashCard.observe(viewLifecycleOwner){
                createCardViewModel.setParentFlashCardCover(null)
                val parentCard = it?.find { it.id == args.cardId?.single() } ?:return@observe
                createCardViewModel.setParentCard(parentCard)
                createCardViewModel.setSisterCards(it)
            }
        } else {
            createCardViewModel.getFileAndChildrenCards(parentFlashCardId)
                .observe(viewLifecycleOwner,flashCardAndChildrenCardsObserver)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }





}