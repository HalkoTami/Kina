package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFragBaseBinding
import com.example.tangochoupdated.db.enumclass.AnkiFilter
import com.example.tangochoupdated.ui.view_set_up.AnkiBaseFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.BaseViewModel


class AnkiFragmentBase  : Fragment() {

    private var _binding: AnkiFragBaseBinding? = null
    private val sharedViewModel: BaseViewModel by activityViewModels()
    private val ankiSettingVM: AnkiSettingPopUpViewModel by activityViewModels()
    private val ankiFragViewModel : AnkiFragBaseViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding =  AnkiFragBaseBinding.inflate(inflater, container, false)
        val a = childFragmentManager.findFragmentById(binding.ankiFragContainerView.id) as NavHostFragment
        val myNavCon = a.navController

        val viewSetUp = AnkiBaseFragViewSetUp(ankiFragViewModel, binding,requireActivity())
        viewSetUp.ankiSettingPopUpAddCL(ankiSettingVM)
        binding.bindingSetting.apply {
            ankiSettingVM.apply {
                start()
                setActive(true)
                active.observe(viewLifecycleOwner){
                    if(it){
                        viewSetUp.setUpSettingContent(returnAnkiFilter()!!)
                    }
                    binding.frameLayAnkiSetting.visibility = if(it) View.VISIBLE else View.GONE
                }

            }

        }
        ankiFragViewModel.tabChangeAction.observe(viewLifecycleOwner){
            myNavCon.navigate(it)
        }



        val root: View = binding.root




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}