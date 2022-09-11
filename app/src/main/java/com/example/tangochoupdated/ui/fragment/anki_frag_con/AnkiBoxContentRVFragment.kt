package com.example.tangochoupdated.ui.fragment.anki_frag_con

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.activity.MainActivity
import com.example.tangochoupdated.databinding.AnkiFlipFragBaseBinding
import com.example.tangochoupdated.databinding.FragAnkiContentRvBinding
import com.example.tangochoupdated.databinding.FullRvBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.enumclass.*
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringFragmentDirections
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringTypeAnswerFragment
import com.example.tangochoupdated.ui.fragment.flipFragCon.FlipStringTypeAnswerFragmentDirections
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.view_set_up.AnkiFlipFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.*
import java.time.Duration
import java.time.LocalDateTime


class AnkiBoxContentRVFragment  : Fragment() {

    private var _binding: FragAnkiContentRvBinding? = null
    private val boxViewModel: AnkiBoxFragViewModel by activityViewModels()
    private val ankiBaseViewModel: AnkiFragBaseViewModel by activityViewModels()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()
    private val typeAndCheckViewModel: AnkiFlipTypeAndCheckViewModel by activityViewModels()
    private val createFileViewModel: CreateFileViewModel by activityViewModels()
    private val  createCardViewModel: CreateCardViewModel by activityViewModels()
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
        val viewSetUp = AnkiBoxFragViewSetUp()
        val topBarText:String
        val amountText:String
        val topBarDraw:Int
        val list :List<Card>
        when(ankiBaseViewModel.returnActiveFragment()){
            AnkiFragments.AnkiBox -> {
                topBarText = "暗記ボックスアイテム"
                topBarDraw = R.drawable.icon_inbox
                list = boxViewModel.returnAnkiBoxItems()
            }
            AnkiFragments.Flip ->{
                topBarText = "暗記中のアイテム"
                topBarDraw = R.drawable.icon_card
                list = flipBaseViewModel.returnFlipItems()

            }
        }
        amountText = "${list.size}枚"
        binding.txvTopBarTitle.text = topBarText
        binding.txvTopBarCardAmount.text = amountText
        binding.imvFlipOrAnkiBox.setImageDrawable(ContextCompat.getDrawable(requireActivity(),topBarDraw))

        val adapter =
            viewSetUp.setUpAnkiBoxRVListAdapter(binding.rvBinding.recyclerView,requireActivity(),boxViewModel,AnkiBoxFragments.Library,viewLifecycleOwner)
        adapter.submitList(list)

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