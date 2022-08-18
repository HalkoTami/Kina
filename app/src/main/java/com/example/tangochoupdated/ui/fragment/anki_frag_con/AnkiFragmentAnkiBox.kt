package com.example.tangochoupdated.ui.fragment.anki_frag_con

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.LayerDrawable
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
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxViewSetUp
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
        val viewSetUp = AnkiBoxViewSetUp(
            ankiBoxVM = viewModel,
            context =  requireActivity(),
            bindingAnkiBoxFrag = binding,
        )
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


            viewSetUp.apply {
                ankiBoxItems.observe(viewLifecycleOwner) {
                    setUpAnkiBoxRing(it)
                    setUpFlipProgressBar(it)
                    binding.btnStartAnki.text = if(it.isEmpty()) "カードを選ばず暗記" else "暗記開始"

                }
            }




            return root
        }
    }
    fun getRememberedPercentage(list: MutableList<Card>):Double{
        return if(list.size!=0) (list.filter { it.remembered == true }.size/list.size).toDouble()
        else 0.0

    }
    fun getFlippedPercentage(list: MutableList<Card>,timesFlipped:Int):Double{
        return if(list.size!=0) (list.filter { it.timesFlipped == timesFlipped }.size/list.size).toDouble()
        else 0.0

    }
    fun moveViewInCircle(percentage:Double,
                         view:ImageView,
                         circleWidth: Int){
        val radious = Math.toRadians(360*percentage-90.0)
        val dx: Float = (circleWidth-view.layoutParams.width)/2 * Math.cos(radious).toFloat()
        val dy: Float = (circleWidth-view.layoutParams.height)/2 * Math.sin(radious).toFloat()
        view.translationX = dx
        view.translationY =dy
    }
    fun setUpAnkiBoxRing(list: MutableList<Card>){
        val rememberedPercentage = 0.50
//            getRememberedPercentage(list)
        val rememberedCardsSize = list.filter { it.remembered == true }.size
        binding.ringBinding.apply {
            ringProgressBar.progress = rememberedPercentage.times(100).toInt()
            moveViewInCircle(rememberedPercentage,imvRememberedEndIcon,ringProgressBar.layoutParams.width)
            txvInsideRing.text = "$rememberedCardsSize/${list.size}"
        }



    }
    fun setUpFlipProgressBar(list: MutableList<Card>){
        val per1 = getFlippedPercentage(list,1)
        val per2 = getFlippedPercentage(list,2)
        val per3 = getFlippedPercentage(list,3)
        val per4 = getFlippedPercentage(list,4)
        fun getPercentage(per:Double):Int{
            return per.times(100).toInt()
        }

        binding.flipGraphBinding.apply {
            progressBarFlipped1.progress =      getPercentage(per1)
            progressBarFlipped2.progress =      getPercentage(per2)
            progressBarFlipped3.progress =      getPercentage(per3)
            progressBarFlippedAbove4.progress = getPercentage(per4)
            txv1.translationX =  root.width*per1.toFloat()
            txv2.translationX = root.width*per2.toFloat()
            txv3.translationX = root.width*per3.toFloat()
            txv4Above.translationX = root.width*per4.toFloat()

        }

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