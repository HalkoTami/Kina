package com.example.tangochoupdated.ui.create.card.string

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
import com.example.tangochoupdated.databinding.CreateCardStringBinding
import com.example.tangochoupdated.room.dataclass.StringData
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.StringFragFocusedOn
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel

class StringCardFragment : Fragment() {

    private var _binding: CreateCardStringBinding? = null
    private val  createCardViewModel: CreateCardViewModel by activityViewModels()
    private val stringCardViewModel :StringCardViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

//   private val  stringViewModel: StringCardViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateCardStringBinding.inflate(inflater, container, false)
        val root: View = binding.root



        createCardViewModel.setCardStatus(CardStatus.STRING)


        binding.apply {
            edtFrontTitle.hint = "表のタイトル"
            edtFrontContent.hint = "表"
            edtBackTitle.hint = "裏のタイトル"
            edtBackContent.hint = "裏"

            createCardViewModel.getStringData.observe(viewLifecycleOwner){
                if(it==true){
                    Toast.makeText(context, "sending stringdata ", Toast.LENGTH_SHORT).show()
//
                    createCardViewModel.setStringData(
                        StringData(
                            frontTitle =  if(edtFrontTitle.text.toString() == "表") null else edtFrontTitle.text.toString(),
                            frontText =     edtFrontContent.text.toString(),
                            backTitle = if(edtBackTitle.text.toString() == "裏") null else edtBackTitle.text.toString(),
                            backText =  edtBackContent.text.toString()
                        )
                    )
                } else return@observe
            }


        }




        binding.apply {
            stringCardViewModel.apply {
                stringData.observe(viewLifecycleOwner) {
                    edtFrontTitle.text = SpannableStringBuilder(it?.frontTitle ?: "表")
                    edtFrontContent.text = SpannableStringBuilder(it?.frontText ?: "")
                    edtBackTitle.text = SpannableStringBuilder(it?.backTitle ?: "裏")
                    edtBackContent.text = SpannableStringBuilder(it?.backText ?: "")

                }
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

//


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

        _binding = null
    }
}