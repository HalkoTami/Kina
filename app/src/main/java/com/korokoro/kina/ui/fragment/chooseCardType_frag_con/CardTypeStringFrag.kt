package com.korokoro.kina.ui.fragment.chooseCardType_frag_con

import android.content.Context
import android.os.*
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.korokoro.kina.R
import com.korokoro.kina.databinding.CreateCardFragStringFragBinding
import com.korokoro.kina.db.dataclass.StringData
import com.korokoro.kina.db.enumclass.CardStatus
import com.korokoro.kina.customClasses.enumClasses.StringFragFocusedOn
import com.korokoro.kina.ui.viewmodel.CreateCardViewModel
import com.korokoro.kina.ui.viewmodel.CardTypeStringViewModel

class CardTypeStringFrag : Fragment() {

    private var _binding: CreateCardFragStringFragBinding? = null
    private val  createCardViewModel: CreateCardViewModel by activityViewModels()
    private val  stringCardViewModel : CardTypeStringViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateCardFragStringFragBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createCardViewModel.setCardStatus(CardStatus.STRING)
        binding.apply {
            binding.edtFrontTitle.visibility = View.VISIBLE
//            ヒント
            edtFrontTitle.hint = "表のタイトル"
            edtFrontContent.hint = "表"
            edtBackTitle.hint = "裏のタイトル"
            edtBackContent.hint = "裏"

            fun setTextAndSelection(editText: EditText,string: String){
                editText.text = SpannableStringBuilder(string)
                editText.setSelection(string.length)
            }
            stringCardViewModel.apply {
//                表示
                parentCard.observe(viewLifecycleOwner){
                    val stringData = it?.stringData
                    setTextAndSelection(edtFrontContent,stringData?.frontText ?: "")
                    setTextAndSelection(edtBackContent,stringData?.backText ?: "")
                    setTextAndSelection(edtFrontTitle,stringData?.frontTitle ?: "表")
                    setTextAndSelection(edtBackTitle,stringData?.backTitle ?: "裏")
                }


//                キーボードフォーカス
                focusedOn.observe(viewLifecycleOwner){
                    when(it){
                        StringFragFocusedOn.FrontContent-> showSoftKeyboard(edtFrontContent)
                        StringFragFocusedOn.BackContent -> showSoftKeyboard(edtBackContent)
                        StringFragFocusedOn.FrontTitle -> showSoftKeyboard(edtFrontTitle)
                        StringFragFocusedOn.BackTitle -> showSoftKeyboard(edtBackTitle)
                        else -> requireActivity().findViewById<ConstraintLayout>(R.id.main_top_constrainLayout).requestFocus()
                    }
                }

            }




        }








        return root
    }

    fun showSoftKeyboard(view: EditText) {
        if (view.requestFocus()) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stringCardViewModel.setFocusedOn(null)
        val frontTitle = binding.edtFrontTitle.text.toString()
        val frontText =  binding.edtFrontContent.text.toString()
        val backText =   binding.edtBackContent.text.toString()
        val backTitle =  binding.edtBackTitle.text.toString()
        val newStringData = StringData(
            frontTitle = if(frontTitle=="表") null else frontTitle,
            frontText =  frontText,
            backText =   backText,
            backTitle =  if(backTitle=="裏") null else backTitle
        )
        createCardViewModel.upDateCard(newStringData,stringCardViewModel.returnParentCard() ?:createCardViewModel.returnParentCard() ?:return)

        _binding = null
    }
}