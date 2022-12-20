package com.koronnu.kina.ui.fragment.base_frag_con

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.databinding.AnkiFragBaseBinding
import com.koronnu.kina.ui.viewmodel.*


class AnkiBaseFrag  : Fragment() {

    private var _binding: AnkiFragBaseBinding? = null
    private val ankiBaseViewModel : AnkiBaseViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  AnkiFragBaseBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(binding.ankiFragContainerView.id) as NavHostFragment
        val myNavCon = navHostFragment.navController
        val getSharedPref:(int:Int)->SharedPreferences = {int ->
            requireActivity().getSharedPreferences(getString(int),Context.MODE_PRIVATE)
        }
        ankiBaseViewModel.onFragmentCreated(binding, viewLifecycleOwner,getSharedPref,myNavCon)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ankiBaseViewModel.changeBnvBtnAddStatus(true)
        _binding = null
    }


}