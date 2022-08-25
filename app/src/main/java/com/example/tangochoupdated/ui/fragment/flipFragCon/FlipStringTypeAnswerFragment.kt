package com.example.tangochoupdated.ui.fragment.flipFragCon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.databinding.AnkiFlipFragTypeAnswerStringFragBinding
import com.example.tangochoupdated.db.enumclass.FlipFragments
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringTypeAnswerFragment  : Fragment() {

    private var _binding: AnkiFlipFragTypeAnswerStringFragBinding? = null
    private val args: FlipStringTypeAnswerFragmentArgs by navArgs()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
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
            flipBaseViewModel.getCardFromDB(args.cardId).observe(viewLifecycleOwner){
                setParentCard(it)
                binding.txvFlipTitle.text = it.id.toString()
            }
        }




        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}