package com.example.tangochoupdated.ui.create.card.string

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.tangochoupdated.databinding.FragmentCreateBinding
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.BaseViewModel
import com.example.tangochoupdated.RoomApplication
import com.example.tangochoupdated.ViewModelFactory
import com.example.tangochoupdated.databinding.CreateCardBaseBinding
import com.example.tangochoupdated.databinding.CreateCardStringBinding
import com.example.tangochoupdated.room.dataclass.StringData
import com.example.tangochoupdated.room.enumclass.CardStatus
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
            stringCardViewModel.stringData.observe(viewLifecycleOwner){
                edtFrontTitle.text = SpannableStringBuilder(it?. frontTitle ?:"表" )
                edtFrontContent.text = SpannableStringBuilder(it?.frontText ?:"" )
                edtBackTitle.text = SpannableStringBuilder(it?.backTitle ?:"裏")
                edtBackContent.text =  SpannableStringBuilder(it?. backText ?:"")

            }
//

        }






        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()


        _binding = null
    }
}