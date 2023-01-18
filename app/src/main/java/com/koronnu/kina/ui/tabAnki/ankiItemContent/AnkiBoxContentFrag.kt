package com.koronnu.kina.ui.tabAnki.ankiItemContent

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.koronnu.kina.R
import com.koronnu.kina.databinding.FragAnkiContentRvBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.koronnu.kina.ui.viewmodel.*
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments


class AnkiBoxContentFrag  : Fragment() {

    private var _binding: FragAnkiContentRvBinding? = null
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiBaseViewModel by activityViewModels()
    private val ankiFlipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  FragAnkiContentRvBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val fragmentBefore = ankiBaseViewModel.returnActiveFragment()
        val viewSetUp = AnkiBoxFragViewSetUp()
        fun getTopBarTitle(fragmentBefore: AnkiFragments):String{
            return requireActivity().resources.getString(when(fragmentBefore){
                AnkiFragments.AnkiBox ->R.string.ankiBoxContentFrag_topBarTitle_ankiBoxContent
                AnkiFragments.Flip ->R.string.ankiBoxContentFrag_topBarTitle_parentFlipItem
                else -> R.string.item
            })
        }
        fun getTopBarDrawable(fragmentBefore: AnkiFragments):Drawable{
            val id =   when(fragmentBefore){
                AnkiFragments.AnkiBox -> R.drawable.icon_inbox
                AnkiFragments.Flip -> R.drawable.icon_card
                else -> 0
            }
            return ContextCompat.getDrawable(requireActivity(),id)!!
        }
        fun getParentTokenList(fragmentBefore: AnkiFragments):List<Card>{
            return when(fragmentBefore){
                AnkiFragments.AnkiBox -> ankiBoxViewModel.returnAnkiBoxItems()
                AnkiFragments.Flip -> ankiFlipBaseViewModel.returnFlipItems()
                else    -> mutableListOf()
            }
        }
        fun setUpTopBar(){
            val amountText = requireActivity().resources.getString(R.string.cardAmountText,getParentTokenList(fragmentBefore).size)
            binding.txvTopBarTitle.text = getTopBarTitle(fragmentBefore)
            binding.txvTopBarCardAmount.text = amountText
            binding.imvFlipOrAnkiBox.setImageDrawable(getTopBarDrawable(fragmentBefore))
        }
        fun setUpRV(){
            val adapter =
                viewSetUp.setUpAnkiBoxRVListAdapter(
                    binding.rvBinding.recyclerView,
                    requireActivity(),
                    ankiBoxViewModel,
                    null,viewLifecycleOwner)
            adapter.submitList(getParentTokenList(fragmentBefore))

        }
        setUpTopBar()
        setUpRV()


        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                val ankiNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.fcv_fragmentAnkiBase).findNavController()
                ankiNavCon.popBackStack()
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