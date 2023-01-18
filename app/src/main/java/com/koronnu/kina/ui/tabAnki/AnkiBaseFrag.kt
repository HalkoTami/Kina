package com.koronnu.kina.ui.tabAnki

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.databinding.FragmentAnkiBaseBinding
import com.koronnu.kina.ui.viewmodel.*


class AnkiBaseFrag  : Fragment() {

    private var _binding: FragmentAnkiBaseBinding? = null
    private val ankiBaseViewModel : AnkiBaseViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentAnkiBaseBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(binding.fcvFragmentAnkiBase.id) as NavHostFragment
        val myNavCon = navHostFragment.navController
        val getSharedPref:(int:Int)->SharedPreferences = {int ->
            requireActivity().getSharedPreferences(getString(int),Context.MODE_PRIVATE)
        }
        ankiBaseViewModel.onFragmentCreated(binding, viewLifecycleOwner,getSharedPref,myNavCon)
        binding.apply {
            viewModel=ankiBaseViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ankiBaseViewModel.changeBnvBtnAddStatus(true)
        _binding = null
    }


}