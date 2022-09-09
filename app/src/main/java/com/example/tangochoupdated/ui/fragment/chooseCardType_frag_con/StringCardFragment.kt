package com.example.tangochoupdated.ui.fragment.chooseCardType_frag_con

import android.content.Context
import android.os.*
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.CreateCardFragStringFragBinding
import com.example.tangochoupdated.db.dataclass.StringData
import com.example.tangochoupdated.db.enumclass.CardStatus
import com.example.tangochoupdated.db.enumclass.StringFragFocusedOn
import com.example.tangochoupdated.ui.viewmodel.CreateCardViewModel
import com.example.tangochoupdated.ui.viewmodel.StringCardViewModel

class StringCardFragment : Fragment() {

    private var _binding: CreateCardFragStringFragBinding? = null
    private val  createCardViewModel: CreateCardViewModel by activityViewModels()
    private val stringCardViewModel : StringCardViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


//   private val  stringViewModel: StringCardViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateCardFragStringFragBinding.inflate(inflater, container, false)
        val root: View = binding.root


        Toast.makeText(requireActivity(),"string called",Toast.LENGTH_SHORT).show()

        createCardViewModel.setCardStatus(CardStatus.STRING)
        binding.apply {
            binding.edtFrontTitle.visibility = View.VISIBLE
//            ヒント
            edtFrontTitle.hint = "表のタイトル"
            edtFrontContent.hint = "表"
            edtBackTitle.hint = "裏のタイトル"
            edtBackContent.hint = "裏"

            stringCardViewModel.apply {
//                表示
                parentCard.observe(viewLifecycleOwner){
                    val stringData = it?.stringData
                    edtFrontTitle.text = SpannableStringBuilder(stringData?.frontTitle ?: "表")
                    edtFrontContent.text = SpannableStringBuilder(stringData?.frontText ?: "")
                    edtBackTitle.text = SpannableStringBuilder(stringData?.backTitle ?: "裏")
                    edtBackContent.text = SpannableStringBuilder(stringData?.backText ?: "")
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

    fun showSoftKeyboard(view: View) {
        view.requestFocus()
        if (view.requestFocus()) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<ConstraintLayout>(R.id.main_top_constrainLayout).requestFocus()
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
        createCardViewModel.upDateCard(newStringData,stringCardViewModel.returnParentCard() ?:return)

        _binding = null
    }
}