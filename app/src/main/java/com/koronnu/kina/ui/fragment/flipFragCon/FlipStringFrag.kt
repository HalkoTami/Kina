package com.koronnu.kina.ui.fragment.flipFragCon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.koronnu.kina.R
import com.koronnu.kina.databinding.AnkiFlipFragLookStringFragBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.customClasses.enumClasses.Count
import com.koronnu.kina.customClasses.enumClasses.FlipFragments
import com.koronnu.kina.ui.viewmodel.AnkiFlipBaseViewModel
import com.koronnu.kina.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringFrag  : Fragment() {

    private var _binding: AnkiFlipFragLookStringFragBinding? = null
    private val flipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()
    private val args: FlipStringFragArgs by navArgs()
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  AnkiFlipFragLookStringFragBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val cardId = args.cardId
        val front = args.front
        val cardFromDBObserver = Observer<Card>{
            flipBaseViewModel.setParentCard(it)
            val data = it.stringData
            binding.apply {
                when(front) {
                    true ->{
                        txvTitle.text = data?.frontTitle ?:resources.getString(R.string.edtFrontTitle_default)
                        txvContent.text = data?.frontText
                    }
                    false  -> {
                        txvTitle.text =data?.backTitle ?:resources.getString(R.string.edtBackTitle_default)
                        txvContent.text = data?.backText
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