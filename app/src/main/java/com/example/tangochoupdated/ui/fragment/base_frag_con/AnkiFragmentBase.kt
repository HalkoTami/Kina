package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.databinding.AnkiFragBaseBinding
import com.example.tangochoupdated.ui.viewmodel.customClasses.MainFragment
import com.example.tangochoupdated.ui.view_set_up.AnkiBaseFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.*


class AnkiFragmentBase  : Fragment() {

    private var _binding: AnkiFragBaseBinding? = null
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


        _binding =  AnkiFragBaseBinding.inflate(inflater, container, false)
        val a = childFragmentManager.findFragmentById(binding.ankiFragContainerView.id) as NavHostFragment
        val myNavCon = a.navController

        baseViewModel.setChildFragmentStatus(MainFragment.Anki)

        val viewSetUp = AnkiBaseFragViewSetUp()
        viewSetUp.ankiSettingPopUpAddCL(
            binding.bindingSetting,
            ankiSettingVM,
            ankiFragViewModel,
            requireActivity(),
            myNavCon
            )
        viewSetUp.ankiBackGroundCoverAddCL(binding,ankiFragViewModel)
        ankiSettingVM.start()
        ankiFragViewModel.apply {
            setAnkiBoxNavCon(myNavCon)
            settingVisible.observe(viewLifecycleOwner){ settingVisible ->
                if(settingVisible){
                    viewSetUp.setUpSettingContent(
                        ankiSettingVM.returnAnkiFilter(),
                        binding,
                        requireActivity(),)
                }
                arrayOf(binding.frameLayAnkiSetting,binding.viewAnkiFragConCover).onEach {
                   it.visibility = if(settingVisible) View.VISIBLE else View.GONE
                }
            }


        }




        val root: View = binding.root




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}