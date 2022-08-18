package com.example.tangochoupdated.ui.fragment.base_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFragBaseBinding
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.BaseViewModel


class AnkiFragmentBase  : Fragment() {

    private var _binding: AnkiFragBaseBinding? = null
    private val sharedViewModel: BaseViewModel by activityViewModels()
    private val ankiSettingVM: AnkiSettingPopUpViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding =  AnkiFragBaseBinding.inflate(inflater, container, false)
//        val a = childFragmentManager.findFragmentById(binding.ankiFragContainerView.id) as NavHostFragment
//        val myNavCon = a.navController
        binding.bindingSetting.apply {
            ankiSettingVM.apply {
                start()
                active.observe(viewLifecycleOwner){
                    binding.frameLayAnkiSetting.visibility = if(it) View.VISIBLE else View.GONE
                }
                bindingSettingContent.apply {
                    val white = ContextCompat.getColor(requireActivity(),R.color.white)
                    val green = ContextCompat.getColor(requireActivity(),R.color.most_dark_green)
                    root.setOnClickListener(null)
                    txvFilterWithFlag.setOnClickListener { txvFilterSetting }
                    ankiFilter.observe(viewLifecycleOwner){
                        linLayFilterTypedAnswer.apply {
                            alpha = if(it.correctAnswerTyped == null) 0.5f else 1f
                            isClickable = it.correctAnswerTyped!=null
                        }
                        lineLayFilterRememberStatus.apply {
                            alpha = if(it.remembered == null) 0.5f else 1f
                            isClickable = it.remembered!=null
                        }
                        lineLayFilterFlag.apply {
                            alpha = if(it.flag == null) 0.5f else 1f
                            isClickable = it.flag!=null
                        }
                        txvFilterCardRemembered.isSelected = (it.remembered == false)
                        txvFilterCardNotRemembered .isSelected = (it.remembered != false)
                        txvFilterTypedAnswerCorrect.isSelected = (it.correctAnswerTyped == false)
                        txvFilterTypedAnswerMissed.isSelected = (it.correctAnswerTyped != false)
                        txvFilterWithFlag.isSelected = (it.flag != false)
                        txvFilterWithoutFlag.isSelected = (it.flag == false)
                        arrayOf(txvFilterCardRemembered,
                            txvFilterCardNotRemembered,
                            txvFilterTypedAnswerMissed,
                            txvFilterTypedAnswerCorrect,
                            txvFilterWithoutFlag,
                            txvFilterWithFlag,).onEach { it.setTextColor(if(it.isSelected) white else green) }
                        Toast.makeText(requireActivity(),"called",Toast.LENGTH_SHORT).show()


                    }
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