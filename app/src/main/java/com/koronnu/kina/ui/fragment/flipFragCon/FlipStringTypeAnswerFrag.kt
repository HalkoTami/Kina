package com.koronnu.kina.ui.fragment.flipFragCon

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.compose.runtime.key
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.koronnu.kina.R
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.showKeyBoard
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.customClasses.enumClasses.FlipFragments
import com.koronnu.kina.customClasses.enumClasses.NeighbourCardSide
import com.koronnu.kina.databinding.FragmentFlipStringTypeAnswerBinding
import com.koronnu.kina.ui.listener.KeyboardListener
import com.koronnu.kina.ui.viewmodel.AnkiFlipBaseViewModel
import com.koronnu.kina.ui.viewmodel.FlipTypeAndCheckViewModel
import com.koronnu.kina.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringTypeAnswerFrag  : Fragment() {

    private var _binding: FragmentFlipStringTypeAnswerBinding? = null
    private val args: FlipStringTypeAnswerFragArgs by navArgs()
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
                onKeyBoardDisappear={typeAndCheckViewModel.setKeyBoardVisible(false)
                    binding.imvCheckAnswer.visibility = View.GONE
                    changeViewVisibility(bottom,true)
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