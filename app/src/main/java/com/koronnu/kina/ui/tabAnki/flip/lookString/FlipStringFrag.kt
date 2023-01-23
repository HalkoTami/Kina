package com.koronnu.kina.ui.tabAnki.flip.lookString

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.koronnu.kina.R
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.model.enumClasses.Count
import com.koronnu.kina.data.model.enumClasses.FlipFragments
import com.koronnu.kina.databinding.FragmentFlipStringBinding
import com.koronnu.kina.ui.tabAnki.flip.AnkiFlipBaseViewModel
import com.koronnu.kina.ui.tabAnki.AnkiSettingPopUpViewModel


class FlipStringFrag  : Fragment() {

    private var _binding: FragmentFlipStringBinding? = null
    private val flipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val args: com.koronnu.kina.ui.tabAnki.flip.lookString.FlipStringFragArgs by navArgs()
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  FragmentFlipStringBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val cardId = args.cardId
        val front = args.front
        val cardFromDBObserver = Observer<Card>{
            flipBaseViewModel.setParentCard(it)
            val data = it.stringData
            binding.apply {
                when(front) {
                    true ->{
                        txvFlipStringCardTitle.text = data?.frontTitle ?:resources.getString(R.string.edtFrontTitle_default)
                        txvFlipStringCardContent.text = data?.frontText
                    }
                    false  -> {
                        txvFlipStringCardTitle.text =data?.backTitle ?:resources.getString(R.string.edtBackTitle_default)
                        txvFlipStringCardContent.text = data?.backText
                    }
                }
            }

        }

        flipBaseViewModel.onChildFragmentsStart(
            when(front){
                true -> FlipFragments.LookStringFront
                false -> FlipFragments.LookStringBack
            },
            ankiSettingPopUpViewModel.getAutoFlip.active
        )
        flipBaseViewModel.getCardFromDB(cardId?.single() ?:return binding.root).observe(viewLifecycleOwner,cardFromDBObserver)




        return root
    }
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
//            true // default to enabled
//        ) {
//            override fun handleOnBackPressed() {
//                val navCon = requireActivity().findViewById<FragmentContainerView>(R.id.anki_frag_container_view).findNavController()
//                navCon.popBackStack()
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(
//            this,  // LifecycleOwner
//            callback
//        )
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        flipBaseViewModel.apply {

            val a = returnCountFlip()
            if(a!=null){
                if(returnFlipFragment()== FlipFragments.LookStringBack){
                    a.count = Count.End
                    setCountFlip(a)
                }
            }

        }
        _binding = null
    }
}