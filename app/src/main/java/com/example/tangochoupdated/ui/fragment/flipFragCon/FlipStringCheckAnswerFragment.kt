package com.example.tangochoupdated.ui.fragment.flipFragCon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.geometry.Rect
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFlipFragCheckAnswerStringFragBinding
import com.example.tangochoupdated.db.enumclass.FlipFragments
import com.example.tangochoupdated.db.enumclass.ImvChangeAlphaOnDown
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipTypeAndCheckViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringCheckAnswerFragment  : Fragment() {

    private var _binding: AnkiFlipFragCheckAnswerStringFragBinding? = null
    private val args: FlipStringCheckAnswerFragmentArgs by navArgs()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()
    private val typeAndCheckViewModel: AnkiFlipTypeAndCheckViewModel by activityViewModels()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  AnkiFlipFragCheckAnswerStringFragBinding.inflate(inflater, container, false)
        val root: View = binding.root


        flipBaseViewModel.apply {
            Toast.makeText(requireActivity(),"${binding.lineLayCorrectAnswer.layoutParams.height}",Toast.LENGTH_SHORT).show()
            settingVM.apply {
                onChildFragmentsStart(FlipFragments.CheckAnswerString,returnReverseCardSide(),returnAutoFlip().active)
            }


            flipBaseViewModel.getCardFromDB(args.cardId).observe(viewLifecycleOwner){
                setParentCard(it)
                binding.txvTitle.text = if(args.answerIsBack)it.stringData?.backTitle ?:"裏" else it.stringData?.frontTitle ?:"表"
                binding.txvYourAnswer.text = typeAndCheckViewModel.getAnswer(args.cardId)

            }
        }





        return root
    }
    fun dpTopx(dp: Int, context: Context): Float {
        val metrics = context.getResources().getDisplayMetrics()
        return dp * metrics.density
    }

    fun pxToDp(px: Int, context: Context): Float {
        val metrics = context.resources.displayMetrics
        return px / metrics.density
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}