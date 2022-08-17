package com.example.tangochoupdated.ui.fragment.anki_frag_con

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
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import java.lang.Math.abs


class AnkiFragmentAnkiBox  : Fragment() {

    private var _binding: AnkiHomeFragBaseBinding? = null
    private val viewModel: AnkiBoxFragViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  AnkiHomeFragBaseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            linLayTabChange.tag = AnkiBoxTab.AllFlashCardCovers
            tabAllFlashCardCoverToAnkiBox.isSelected = true
            arrayOf(tabFavouritesToAnkiBox,tabLibraryToAnkiBox,tabAllFlashCardCoverToAnkiBox).onEach {
                it.setOnClickListener(AnkiBoxTabChangeCL(binding,viewModel))
            }
        }
        viewModel.apply{
            tabChangeAction.observe(viewLifecycleOwner){
                val a = childFragmentManager.findFragmentById(binding.ankiBoxFragConView.id) as NavHostFragment
                a.navController.navigate(it)
                Toast.makeText(requireActivity(), "navigated ", Toast.LENGTH_SHORT).show()
            }
            ankiBoxFileIds.observe(viewLifecycleOwner){
                getDescendantsCardIds(it).observe(viewLifecycleOwner){
                    setAnkiBoxCardIds(it)
                }
            }
            ankiBoxCardIds.observe(viewLifecycleOwner){
                getCardsFromDBByMultipleCardIds(it).observe(viewLifecycleOwner){
                    setAnkiBoxItems(it)
                }
            }
            setAnkiBoxItems(emptyList())
            ankiBoxItems.observe(viewLifecycleOwner) {
                setUpAnkiBoxRing(it)
                setUpGraphs(it)
               if (it.isEmpty()){
                    binding.linLayAnkiBox.alpha = 0.3f
                   binding.imvRememberedEndIcon.visibility = View.GONE
                    binding.linLayAnkiBox.isClickable = false
                } else {
                   binding.linLayAnkiBox.alpha = 1f
                   binding.imvRememberedEndIcon.visibility = View.VISIBLE
                   binding.linLayAnkiBox.isClickable = true
               }
            }



            return root
        }
    }
    fun setUpAnkiBoxRing(list:MutableList<Card>){
        val radious:Double
        val rememberedCards = list.filter { it.remembered == true }.size
        if(list.isNotEmpty()){

            val rememberedPercentage =
            if(rememberedCards!=0){
                (list.filter { it.remembered == true }.size/list.size).toDouble()

            } else {
                1.0
            }
            binding.ringAnkiBox.progress = rememberedPercentage.toInt()
            360*rememberedPercentage
            radious = Math.toRadians(360*rememberedPercentage-90.0)





        } else{
            radious = Math.toRadians(-90.0)
        }
        val dx: Float = (binding.ringAnkiBox.width-binding.imvRememberedEndIcon.width)/2 * Math.cos(radious).toFloat()
        val dy: Float = (binding.ringAnkiBox.height-binding.imvRememberedEndIcon.width)/2  * Math.sin(radious).toFloat()
        binding.imvRememberedEndIcon.translationX = dx
        binding.imvRememberedEndIcon.translationY =dy
        binding.txvInsideRing.text = "$rememberedCards/${list.size}"


    }
    fun setUpGraphs(list: MutableList<Card>){
        val rememberedCards = list.filter { it.remembered == true }.size
        val rememberedPercentage =
            if(rememberedCards!=0){
                (list.filter { it.remembered == true }.size/list.size).toDouble()

            } else {
                0.0
            }
        binding.ankiBoxGraphBinding.guideRemembered.setGuidelinePercent(rememberedPercentage.toFloat())

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                val navCon = requireActivity().findViewById<FragmentContainerView>(R.id.anki_box_frag_con_view).findNavController()
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