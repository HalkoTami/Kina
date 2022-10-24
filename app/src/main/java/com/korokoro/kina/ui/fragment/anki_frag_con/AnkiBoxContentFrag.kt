package com.korokoro.kina.ui.fragment.anki_frag_con

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.korokoro.kina.R
import com.korokoro.kina.actions.changeViewVisibility
import com.korokoro.kina.databinding.FragAnkiContentRvBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.korokoro.kina.ui.viewmodel.*
import com.korokoro.kina.ui.customClasses.AnkiBoxFragments
import com.korokoro.kina.ui.customClasses.AnkiFragments


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
            return when(fragmentBefore){
                AnkiFragments.AnkiBox -> "暗記ボックスアイテム"
                AnkiFragments.Flip -> "暗記中のアイテム"
                else -> ""
            }
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
            val amountText = "${getParentTokenList(fragmentBefore).size}枚"
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
                val ankiNavCon = requireActivity().findViewById<FragmentContainerView>(R.id.anki_frag_container_view).findNavController()
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