package com.example.tangochoupdated.ui.fragment.flipFragCon

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.databinding.AnkiFlipFragTypeAnswerStringFragBinding
import com.example.tangochoupdated.ui.viewmodel.customClasses.FlipFragments
import com.example.tangochoupdated.ui.listener.KeyboardListener
import com.example.tangochoupdated.ui.view_set_up.AnkiTypeAnswerFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipTypeAndCheckViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringTypeAnswerFragment  : Fragment() {

    private var _binding: AnkiFlipFragTypeAnswerStringFragBinding? = null
    private val args: FlipStringTypeAnswerFragmentArgs by navArgs()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
    private val typeAndCheckViewModel: AnkiFlipTypeAndCheckViewModel by activityViewModels()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()

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

        flipBaseViewModel.apply {
            settingVM.apply {
                onChildFragmentsStart(FlipFragments.TypeAnswerString,returnReverseCardSide(),returnAutoFlip().active)
            }




            binding.edtTypeAnswer.setOnFocusChangeListener { view, b ->
                when(b){
                    true -> {
                        showSoftKeyboard(requireActivity())
                    }
                    false -> return@setOnFocusChangeListener
                }
            }
            typeAndCheckViewModel.setKeyBoardVisible(true)
            binding.edtTypeAnswer.requestFocus()


            flipBaseViewModel.getCardFromDB(args.cardId).observe(viewLifecycleOwner){
                setParentCard(it)
                binding.txvFlipTitle.text = if(args.answerIsBack)it.stringData?.frontTitle ?:"表" else it.stringData?.backTitle ?:"裏"
                binding.txvContent.text = if(args.answerIsBack)it.stringData?.frontText else it.stringData?.backText
            }
        }
        var up :Boolean = false


        val viewSetUp = AnkiTypeAnswerFragViewSetUp(typeAndCheckViewModel)
        val rootView = binding.root
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object:KeyboardListener(rootView,){
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


        return root
    }


    fun showSoftKeyboard(activity: Activity) {


        val a = activity.window.currentFocus
        if (a!=null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtTypeAnswer, 0 )
        }
    }
    fun hideSoftKeyboard(activity: Activity) {
        val a = activity.window.currentFocus
        if (a!=null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edtTypeAnswer.windowToken, 0 )
            typeAndCheckViewModel.setKeyBoardVisible(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        typeAndCheckViewModel.addAnswer(args.cardId, binding.edtTypeAnswer.editableText.toString())
        typeAndCheckViewModel.checkAnswer(flipBaseViewModel.returnParentCard()?:return,binding.edtTypeAnswer.text.toString(),args.answerIsBack)
        _binding = null
    }
}