package com.example.tangochoupdated.ui.fragment.anki_frag_con

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFlipFragBaseBinding
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringFragmentDirections
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.view_set_up.AnkiFlipFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.*


class AnkiFragFlipBaseFragment  : Fragment() {

    private var _binding: AnkiFlipFragBaseBinding? = null
    private val boxViewModel: AnkiBoxFragViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiFragBaseViewModel by activityViewModels()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  AnkiFlipFragBaseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewSetUp = AnkiFlipFragViewSetUp(binding,flipBaseViewModel,requireActivity(),ankiBaseViewModel,settingVM)
        val frag = childFragmentManager.findFragmentById(binding.fragConViewFlip.id) as NavHostFragment
        val navCon = frag.navController

        viewSetUp.setUpViewStart()
        baseViewModel.setBnvVisibility(false)

        flipBaseViewModel.apply {
            setParentPosition(0)
            setFront(
                !settingVM.returnReverseCardSide()
            )
            if(boxViewModel.returnAnkiBoxItems().isNullOrEmpty()){
                getAllCardsFromDB.observe(viewLifecycleOwner){
                    setAnkiFlipItems(it)
                }
            }
            parentPosition.observe(viewLifecycleOwner){ parentPosition ->
                val newCardId = if(returnFlipItems().size > parentPosition) returnFlipItems()[parentPosition].id else return@observe
                getCardFromDB(newCardId).observe(viewLifecycleOwner){
                    setParentCard(it)
                    navCon.navigate(FlipStringFragmentDirections.toFlipString())
                }
            }
            parentCard.observe(viewLifecycleOwner){
                binding.btnRemembered.isSelected =  it.remembered ?:false
            }
            front.observe(viewLifecycleOwner){
                val side = if(it)"表" else "裏"
                viewSetUp.applyProgress(returnParentPosition(),returnFlipItems().size,it,settingVM.returnReverseCardSide())
                binding.topBinding.txvCardPosition.text = side + "  ${returnParentPosition()}/${returnFlipItems().size}"
                navCon.navigate(FlipStringFragmentDirections.toFlipString())
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
        baseViewModel.setBnvVisibility(true)
        _binding = null
    }
}