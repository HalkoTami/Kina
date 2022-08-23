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
import com.example.tangochoupdated.db.enumclass.FlipAction
import com.example.tangochoupdated.db.enumclass.FragmentTree
import com.example.tangochoupdated.db.enumclass.StartFragment
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

        baseViewModel.apply {
            val activeFragment = returnActiveFragment() ?: FragmentTree()
            activeFragment.startFragment = StartFragment.Anki
            setActiveFragment(activeFragment)
        }

        val viewSetUp = AnkiBaseFragViewSetUp(ankiFragViewModel, binding,requireActivity(),ankiSettingVM,myNavCon)
        viewSetUp.addCL()
        ankiSettingVM.start()
        ankiFragViewModel.apply {
            tabChangeAction.observe(viewLifecycleOwner){
                myNavCon.navigate(it)
            }
            settingVisible.observe(viewLifecycleOwner){ settingVisible ->
                if(settingVisible){
                    viewSetUp.setUpSettingContent(ankiSettingVM.returnAnkiFilter()!!)
                }
                arrayOf(binding.frameLayAnkiSetting,binding.viewAnkiSettingBG).onEach {
                   it.visibility = if(settingVisible) View.VISIBLE else View.GONE
                }
            }
            ankiSettingVM.typeAnswer.observe(viewLifecycleOwner){
                if(it){
                    if(flipBaseViewModel.checkFront())
                        flipBaseViewModel.setFlipAction(FlipAction.TypeAnswerString)
                    else if (flipBaseViewModel.checkBack())
                        flipBaseViewModel.setFlipAction(FlipAction.CheckAnswerString)
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