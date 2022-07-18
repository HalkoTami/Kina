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
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val  stringViewModel: StringCardViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateCardStringBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.apply {
            edtFrontTitle.hint = "表のタイトル"
            edtFrontContent.hint = "表"
            edtBackTitle.hint = "裏のタイトル"
            edtBackContent.hint = "裏"
            stringViewModel.sendStringData.observe(viewLifecycleOwner){
                if(it==true){

                    stringViewModel.setStringData(
                        StringData(
                            frontTitle =  if(edtFrontTitle.text.toString() == "表") null else edtFrontTitle.text.toString(),
                            frontText = edtFrontContent.text.toString(),
                            backTitle = if(edtBackTitle.text.toString() == "裏") null else edtBackTitle.text.toString(),
                            backText =  edtBackContent.text.toString()
                        )
                    )
                }
            }


        }



        stringViewModel.parentCard.observe(viewLifecycleOwner){
            binding.apply {
                edtFrontTitle.text = SpannableStringBuilder(it?.stringData?.frontTitle ?:"表")
                edtFrontContent.text = SpannableStringBuilder(it?.stringData?.frontText ?:"")
                edtBackTitle.text = SpannableStringBuilder(it?.stringData?.backTitle ?:"裏")
                edtBackContent.text =  SpannableStringBuilder(it?.stringData?.backText ?:"")
            }

        }






        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()


        _binding = null
    }
}