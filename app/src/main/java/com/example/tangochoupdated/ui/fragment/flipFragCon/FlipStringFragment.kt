package com.example.tangochoupdated.ui.fragment.flipFragCon

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
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFlipFragLookStringFragBinding
import com.example.tangochoupdated.db.enumclass.Count
import com.example.tangochoupdated.db.enumclass.CountFlip
import com.example.tangochoupdated.db.enumclass.FlipFragments
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringFragment  : Fragment() {

    private var _binding: AnkiFlipFragLookStringFragBinding? = null
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
    private val args: FlipStringFragmentArgs by navArgs()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()

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


        binding.apply {
            flipBaseViewModel.apply {
                settingVM.apply {
                    onChildFragmentsStart(when(front){
                        true -> FlipFragments.LookStringFront
                        false -> FlipFragments.LookStringBack
                    },returnReverseCardSide(),returnAutoFlip().active)
                }

                if(cardId!=null){
                    getCardFromDB(args.cardId!!.single()).observe(viewLifecycleOwner){
                        setParentCard(it)
                        val data = it.stringData
                        Toast.makeText(requireActivity(),"${it.timesFlipped}",Toast.LENGTH_SHORT).show()
                        when(front) {
                            true ->{
                                txvTitle.text =  "表 めくった回数 " + it.timesFlipped.toString()
//                                data?.frontTitle ?:"表"
                                txvContent.text = data?.frontText
                                setCountFlip(CountFlip(count = Count.Start, countingCard = it))
                            }
                            false  -> {
                                txvTitle.text ="裏  めくった回数 " +it.timesFlipped.toString()
//                                data?.backTitle ?:"裏"
                                txvContent.text = data?.backText
                            }
                        }

                    }
                }

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
        flipBaseViewModel.apply {

            val a = returnCountFlip()
            if(a!=null){
                if(returnFlipFragment()==FlipFragments.LookStringBack){
                    a.count = Count.End
                    setCountFlip(a)
                }
                Toast.makeText(requireActivity(),"${a.count}",Toast.LENGTH_SHORT).show()
            }

        }
        _binding = null
    }
}