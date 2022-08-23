package com.example.tangochoupdated.ui.fragment.flipFragCon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFlipFragBaseBinding
import com.example.tangochoupdated.databinding.AnkiFlipFragTypeAnswerStringFragBinding
import com.example.tangochoupdated.db.enumclass.FlipAction
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringTypeAnswerFragment  : Fragment() {

    private var _binding: AnkiFlipFragTypeAnswerStringFragBinding? = null

    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()

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
        flipBaseViewModel.setFlipAction(FlipAction.TypeAnswerString)
        flipBaseViewModel.parentCard.observe(viewLifecycleOwner){
            binding.txvFlipTitle.text = it.id.toString()
        }



        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}