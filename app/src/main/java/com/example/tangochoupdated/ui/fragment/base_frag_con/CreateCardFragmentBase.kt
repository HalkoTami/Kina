package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.databinding.CreateCardFragMainBinding
import com.example.tangochoupdated.ui.viewmodel.*


class CreateCardFragmentBase  : Fragment() {

    private var _binding: CreateCardFragMainBinding? = null
    private val baseViewModel: BaseViewModel by activityViewModels()
    private val ankiSettingVM: AnkiSettingPopUpViewModel by activityViewModels()
    private val ankiFragViewModel : AnkiFragBaseViewModel by activityViewModels()
    private val ankiBoxViewModel: AnkiBoxFragViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding =  CreateCardFragMainBinding.inflate(inflater, container, false)

        val a = childFragmentManager.findFragmentById(binding.createCardFragCon.id) as NavHostFragment
        val cardNavCon = a.navController


        val root: View = binding.root




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}