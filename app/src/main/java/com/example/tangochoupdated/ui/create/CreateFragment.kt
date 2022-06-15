package com.example.tangochoupdated.ui.planner

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.FragmentCreateBinding
import com.example.tangochoupdated.databinding.FragmentPlannerHomeBinding
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.ui.library.BaseViewModel

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    lateinit var appearAnimator:AnimatorSet
    val bottomAnimators = mutableListOf<Animator>()
    private val sharedViewModel: BaseViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val plannerViewModel =
            ViewModelProvider(this)[CreateViewModel::class.java]
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.popupAddFile.visibility = View.INVISIBLE
        val a =ObjectAnimator.ofFloat(binding.frameBottomMenu, View.TRANSLATION_Y, 300f,0f)

        val b = ObjectAnimator.ofFloat(binding.bindingAddMenu.root, View.ALPHA,0f,1f)
        bottomAnimators.add(a)
        bottomAnimators.add(b)


        appearAnimator =AnimatorSet().apply{

            playTogether(a,b)
            duration = 500

        }
        appearAnimator.start()
        binding.bindingAddMenu.root.children.iterator().forEachRemaining {
            it.setOnClickListener {
                onClickAddMenu(it)
            }
        }

        return root
    }
    fun onClickAddMenu(v:View){
        appearAnimator.reverse()
        when (v.id){
            binding.bindingAddMenu.imvnewfolder.id -> {
                binding.popupAddFile.visibility= View.VISIBLE
                ObjectAnimator.ofFloat(binding.popupAddFile, View.ALPHA, 0f,1f ).apply {
                    duration= 500
                    start()
                }
                binding.bindingCreateFile.root.children.iterator().forEachRemaining {
                    it.setOnClickListener {
                        onClickCreateFile(it)
                    }
                }
            }
        }

        return
    }

    fun onClickCreateFile(v:View){
        when(v){
            binding.bindingCreateFile.btnCreateFile -> {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}