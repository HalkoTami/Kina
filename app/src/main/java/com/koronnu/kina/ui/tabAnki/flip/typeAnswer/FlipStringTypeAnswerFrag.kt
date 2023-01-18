package com.koronnu.kina.ui.tabAnki.flip.typeAnswer

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.koronnu.kina.R
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.showKeyBoard
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.model.enumClasses.FlipFragments
import com.koronnu.kina.data.model.enumClasses.NeighbourCardSide
import com.koronnu.kina.databinding.FragmentFlipStringTypeAnswerBinding
import com.koronnu.kina.ui.listener.KeyboardListener
import com.koronnu.kina.ui.tabAnki.flip.AnkiFlipBaseViewModel
import com.koronnu.kina.ui.tabAnki.flip.FlipTypeAndCheckViewModel
import com.koronnu.kina.ui.tabAnki.AnkiSettingPopUpViewModel


class FlipStringTypeAnswerFrag  : Fragment() {

    private var _binding: FragmentFlipStringTypeAnswerBinding? = null
    private val args: com.koronnu.kina.ui.tabAnki.flip.typeAnswer.FlipStringTypeAnswerFragArgs by navArgs()
    private val flipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val typeAndCheckViewModel: FlipTypeAndCheckViewModel by viewModels{ FlipTypeAndCheckViewModel.Factory}
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()
    var keyLis:KeyboardListener? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentFlipStringTypeAnswerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val bottom = requireActivity().findViewById<LinearLayoutCompat>(R.id.linLay_flip_bottom)
        fun setUpViewStart(){
            fun addEdtFocusChangeListener(){
                binding.edtTypeAnswerString.setOnFocusChangeListener { view, b ->
                    when(b){
                        true ->  showKeyBoard(view as EditText,requireActivity())
                        false -> return@setOnFocusChangeListener
                    }
                }
            }
            fun addKeyBoardListener(){
                val rootView = binding.root
                val keyboardListener = object:KeyboardListener(rootView,){
                }.apply { onKeyBoardAppear = {typeAndCheckViewModel.setKeyBoardVisible(true)
                    binding.imvCheckAnswer.visibility = View.VISIBLE
                   changeViewVisibility(bottom,false)
                }
                onKeyBoardDisappear={
                    typeAndCheckViewModel.setKeyBoardVisible(false)
                    if(this@FlipStringTypeAnswerFrag.isVisible){
                        binding.imvCheckAnswer.visibility = View.GONE
                        changeViewVisibility(bottom,true)
                    }
                }
                }
                keyLis = keyboardListener
                rootView.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
            }
            fun addCL(){
                binding.imvCheckAnswer.setOnClickListener {
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(keyLis)
                    changeViewVisibility(bottom,true)
                    flipBaseViewModel.flip(NeighbourCardSide.NEXT)
                }
            }
            addCL()
            addEdtFocusChangeListener()
            addKeyBoardListener()

            typeAndCheckViewModel.setKeyBoardVisible(true)
            binding.edtTypeAnswerString.requestFocus()
        }
        val cardFromDBObserver = Observer<Card>{
            flipBaseViewModel.setParentCard(it)
            binding.txvTypeAnswerSideOppositeTitle.text = if(args.answerIsBack)it.stringData?.frontTitle ?:resources.getString(
                R.string.edtFrontTitle_default) else it.stringData?.backTitle ?:resources.getString(R.string.edtBackTitle_default)
            binding.txvTypeAnswerSideOppositeContent.text = if(args.answerIsBack)it.stringData?.frontText else it.stringData?.backText
        }
        setUpViewStart()
        flipBaseViewModel.onChildFragmentsStart(
            FlipFragments.TypeAnswerString,
            ankiSettingPopUpViewModel.getAutoFlip.active)
        flipBaseViewModel.getCardFromDB(args.cardId).observe(viewLifecycleOwner,cardFromDBObserver)






        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(keyLis)
        typeAndCheckViewModel.addAnswer(args.cardId, binding.edtTypeAnswerString.editableText.toString())
        typeAndCheckViewModel.checkAnswer(flipBaseViewModel.getParentCard,args.answerIsBack)
        _binding = null
    }
}