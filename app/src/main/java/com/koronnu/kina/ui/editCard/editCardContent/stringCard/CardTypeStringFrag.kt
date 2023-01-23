package com.koronnu.kina.ui.editCard.editCardContent.stringCard

import android.content.Context
import android.os.*
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.koronnu.kina.R
import com.koronnu.kina.databinding.CreateCardFragStringFragBinding
import com.koronnu.kina.data.source.local.entity.StringData
import com.koronnu.kina.data.source.local.entity.enumclass.CardStatus
import com.koronnu.kina.data.model.enumClasses.StringFragFocusedOn
import com.koronnu.kina.ui.editCard.CreateCardViewModel

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
        createCardViewModel.setCreateCardStringBinding(binding)
        createCardViewModel.setCardStatus(CardStatus.STRING)
        binding.apply {
            binding.edtFrontTitle.visibility = View.VISIBLE
//            ヒント
            edtFrontTitle.hint = resources.getString(R.string.create_string_card_edt_front_title_hint)
            edtFrontContent.hint = resources.getString(R.string.create_string_card_edtFrontContent_hint)
            edtBackTitle.hint = resources.getString(R.string.create_string_card_edt_back_title_hint)
            edtBackContent.hint =  resources.getString(R.string.create_string_card_edtBackContent_hint)

            fun setTextAndSelection(editText: EditText,string: String){
                editText.text = SpannableStringBuilder(string)
                editText.setSelection(string.length)
            }
            stringCardViewModel.apply {
//                表示
                parentCard.observe(viewLifecycleOwner){
                    val stringData = it?.stringData
                    setTextAndSelection(edtFrontContent,stringData?.frontText ?: String())
                    setTextAndSelection(edtBackContent,stringData?.backText ?: String())
                    setTextAndSelection(edtFrontTitle,stringData?.frontTitle ?: resources.getString(R.string.edtFrontTitle_default))
                    setTextAndSelection(edtBackTitle,stringData?.backTitle ?: resources.getString(R.string.edtBackTitle_default))
                }


//                キーボードフォーカス
                focusedOn.observe(viewLifecycleOwner){
                    when(it){
                        StringFragFocusedOn.FrontContent-> showSoftKeyboard(edtFrontContent)
                        StringFragFocusedOn.BackContent -> showSoftKeyboard(edtBackContent)
                        StringFragFocusedOn.FrontTitle -> showSoftKeyboard(edtFrontTitle)
                        StringFragFocusedOn.BackTitle -> showSoftKeyboard(edtBackTitle)
                        else -> binding.root.requestFocus()
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
            frontTitle = if(frontTitle==resources.getString(R.string.edtFrontTitle_default)) null else frontTitle,
            frontText =  frontText,
            backText =   backText,
            backTitle =  if(backTitle==resources.getString(R.string.edtBackTitle_default)) null else backTitle
        )
        createCardViewModel.upDateCard(newStringData,stringCardViewModel.returnParentCard() ?:createCardViewModel.returnParentCard() ?:return)

        _binding = null
    }
}