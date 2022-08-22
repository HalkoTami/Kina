package com.example.tangochoupdated.ui.fragment.flipFragCon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFlipFragBaseBinding
import com.example.tangochoupdated.databinding.AnkiFlipFragLookStringFragBinding
import com.example.tangochoupdated.ui.view_set_up.AnkiFlipFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringFragment  : Fragment() {

    private var _binding: AnkiFlipFragLookStringFragBinding? = null
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  AnkiFlipFragLookStringFragBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            flipBaseViewModel.apply {
                parentCard.observe(viewLifecycleOwner){
                    val data = it.stringData
                    txvTitle.text = if(returnFront()) data?.frontTitle else data?.backTitle
                    txvContent.text = if(returnFront()) data?.frontText else data?.backText
                    Toast.makeText(requireActivity(),"called",Toast.LENGTH_SHORT).show()

                }
            }

        }




        return root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                val navCon = requireActivity().findViewById<FragmentContainerView>(R.id.anki_frag_container_view).findNavController()
                navCon.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}