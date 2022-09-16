package com.example.tangochoupdated.ui.fragment.anki_frag_con

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
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.FragAnkiContentRvBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.*
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiBoxFragments
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiFragments


class AnkiBoxContentRVFragment  : Fragment() {

    private var _binding: FragAnkiContentRvBinding? = null
    private val boxViewModel: AnkiBoxFragViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiFragBaseViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
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
        fun getTopBarTitle(fragmentBefore:AnkiFragments):String{
            return when(fragmentBefore){
                AnkiFragments.AnkiBox -> "暗記ボックスアイテム"
                AnkiFragments.Flip -> "暗記中のアイテム"
            }
        }
        fun getTopBarDrawable(fragmentBefore:AnkiFragments):Drawable{
            val id =   when(fragmentBefore){
                AnkiFragments.AnkiBox -> R.drawable.icon_inbox
                AnkiFragments.Flip -> R.drawable.icon_card
            }
            return ContextCompat.getDrawable(requireActivity(),id)!!
        }
        fun getParentTokenList(fragmentBefore:AnkiFragments):List<Card>{
            return when(fragmentBefore){
                AnkiFragments.AnkiBox -> boxViewModel.returnAnkiBoxItems()
                AnkiFragments.Flip -> flipBaseViewModel.returnFlipItems()
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
                    boxViewModel,
                    AnkiBoxFragments.Library,viewLifecycleOwner)
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