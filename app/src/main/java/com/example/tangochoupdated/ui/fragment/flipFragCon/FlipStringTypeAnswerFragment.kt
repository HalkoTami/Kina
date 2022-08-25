package com.example.tangochoupdated.ui.fragment.flipFragCon

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFlipFragTypeAnswerStringFragBinding
import com.example.tangochoupdated.db.enumclass.FlipFragments
import com.example.tangochoupdated.ui.listener.KeyboardListener
import com.example.tangochoupdated.ui.view_set_up.AnkiTypeAnswerFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipTypeAndCheckViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringTypeAnswerFragment  : Fragment() {

    private var _binding: AnkiFlipFragTypeAnswerStringFragBinding? = null
    private val args: FlipStringTypeAnswerFragmentArgs by navArgs()
    private val flipBaseViewModel: AnkiFlipFragViewModel by activityViewModels()
    private val typeAndCheckViewModel: AnkiFlipTypeAndCheckViewModel by activityViewModels()
    private val settingVM: AnkiSettingPopUpViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  AnkiFlipFragTypeAnswerStringFragBinding.inflate(inflater, container, false)
        val root: View = binding.root

        flipBaseViewModel.apply {
            settingVM.apply {
                onChildFragmentsStart(FlipFragments.TypeAnswerString,returnReverseCardSide(),returnAutoFlip().active)
            }
//
            val bottomMenu = requireActivity().findViewById<LinearLayoutCompat>(R.id.linLay_flip_bottom)



            binding.edtTypeAnswer.setOnFocusChangeListener { view, b ->
                when(b){
                    true -> {
                        showSoftKeyboard(requireActivity())
                    }
                    false -> return@setOnFocusChangeListener
                }
            }
            typeAndCheckViewModel.setKeyBoardVisible(true)
            binding.edtTypeAnswer.requestFocus()

            binding.edtTypeAnswer.addTextChangedListener {
                flipBaseViewModel.setTypedAnswer(it.toString())
            }
            flipBaseViewModel.getCardFromDB(args.cardId).observe(viewLifecycleOwner){
                setParentCard(it)
                binding.txvFlipTitle.text = it.id.toString()
            }
        }
        var up :Boolean = false

        val viewSetUp = AnkiTypeAnswerFragViewSetUp(typeAndCheckViewModel)
        val rootView = binding.root
//        rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
//            ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//
//                val r = Rect()
//                rootView.getWindowVisibleDisplayFrame(r)
//                val heightDiff: Int = rootView.rootView.height - (r.bottom - r.top)
//                if(up){
//                    binding.root.requestFocus()
//                    up = false
//                }
//                if (heightDiff>500) {
//                    Toast.makeText(requireActivity(),"up", Toast.LENGTH_SHORT).show()
//                   up = true
//                    return
//                }
//
//
//
//            }
//        })

        rootView.viewTreeObserver.addOnGlobalLayoutListener(object:KeyboardListener(rootView,){
            override fun onKeyBoardAppear() {
                super.onKeyBoardAppear()
                typeAndCheckViewModel.setKeyBoardVisible(true)
                binding.btnCheckAnswer.visibility = View.VISIBLE
            }

            override fun onKeyBoardDisappear() {
                super.onKeyBoardDisappear()
                typeAndCheckViewModel.setKeyBoardVisible(false)
                binding.btnCheckAnswer.visibility = View.GONE
            }
        })


        return root
    }

    fun hideKeyboard(context: Context) {
        try {
            (context as Activity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            if (context.currentFocus != null && context.currentFocus!!
                    .windowToken != null
            ) {
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    context.currentFocus!!.windowToken, 0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showSoftKeyboard(activity: Activity) {


        val a = activity.window.currentFocus
        if (a!=null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtTypeAnswer, 0 )
        }
    }
    fun hideSoftKeyboard(activity: Activity) {

        val a = activity.window.currentFocus
        if (a!=null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edtTypeAnswer.windowToken, 0 )
            typeAndCheckViewModel.setKeyBoardVisible(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}