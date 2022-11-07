package com.koronnu.kina.ui.fragment.flipFragCon

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.koronnu.kina.actions.hideKeyBoard
import com.koronnu.kina.actions.makeToast
import com.koronnu.kina.actions.showKeyBoard
import com.koronnu.kina.databinding.AnkiFlipFragTypeAnswerStringFragBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.customClasses.FlipFragments
import com.koronnu.kina.customClasses.NeighbourCardSide
import com.koronnu.kina.ui.listener.KeyboardListener
import com.koronnu.kina.ui.viewmodel.AnkiFlipBaseViewModel
import com.koronnu.kina.ui.viewmodel.FlipTypeAndCheckViewModel
import com.koronnu.kina.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringTypeAnswerFrag  : Fragment() {

    private var _binding: AnkiFlipFragTypeAnswerStringFragBinding? = null
    private val args: FlipStringTypeAnswerFragArgs by navArgs()
    private val flipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val typeAndCheckViewModel: FlipTypeAndCheckViewModel by activityViewModels()
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
        _binding =  AnkiFlipFragTypeAnswerStringFragBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fun setUpViewStart(){
            fun addEdtFocusChangeListener(){
                binding.edtTypeAnswer.setOnFocusChangeListener { view, b ->
                    when(b){
                        true ->  showKeyBoard(view as EditText,requireActivity())
                        false -> return@setOnFocusChangeListener
                    }
                }
            }
            fun addKeyBoardListener(){
                val rootView = binding.root
                rootView.viewTreeObserver.addOnGlobalLayoutListener(object:KeyboardListener(rootView,){
                    override fun onGlobalLayout() {
                        super.onGlobalLayout()
                        keyLis = this
                    }
                    override fun onKeyBoardAppear() {
                        super.onKeyBoardAppear()
                        typeAndCheckViewModel.setKeyBoardVisible(true)
                        binding.btnCheckAnswer.visibility = View.VISIBLE
                    }

                    override fun onKeyBoardDisappear() {
                        super.onKeyBoardDisappear()
                        typeAndCheckViewModel.setKeyBoardVisible(false)
                        binding.btnCheckAnswer.visibility = View.GONE
                    }
                })
            }
            fun addCL(){
                binding.btnCheckAnswer.setOnClickListener {
                    val rootView = binding.root
                    rootView.viewTreeObserver.removeOnGlobalLayoutListener(keyLis)
                    flipBaseViewModel.flip(NeighbourCardSide.NEXT,ankiSettingPopUpViewModel.returnReverseCardSide(),true)
                }
            }
            addCL()
            addEdtFocusChangeListener()
            addKeyBoardListener()

            typeAndCheckViewModel.setKeyBoardVisible(true)
            binding.edtTypeAnswer.requestFocus()
        }
        val cardFromDBObserver = Observer<Card>{
            flipBaseViewModel.setParentCard(it)
            binding.txvFlipTitle.text = if(args.answerIsBack)it.stringData?.frontTitle ?:"表" else it.stringData?.backTitle ?:"裏"
            binding.txvContent.text = if(args.answerIsBack)it.stringData?.frontText else it.stringData?.backText
        }
        setUpViewStart()
        flipBaseViewModel.onChildFragmentsStart(
            FlipFragments.TypeAnswerString,
            ankiSettingPopUpViewModel.returnReverseCardSide(),
            ankiSettingPopUpViewModel.returnAutoFlip().active)
        flipBaseViewModel.getCardFromDB(args.cardId).observe(viewLifecycleOwner,cardFromDBObserver)






        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(keyLis)
        typeAndCheckViewModel.addAnswer(args.cardId, binding.edtTypeAnswer.editableText.toString())
        typeAndCheckViewModel.checkAnswer(flipBaseViewModel.returnParentCard()?:return,args.answerIsBack)
        _binding = null
    }
}