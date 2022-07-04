package com.example.tangochoupdated.ui.planner

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.tangochoupdated.databinding.FragmentCreateBinding
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.BaseViewModel
import com.example.tangochoupdated.RoomApplication
import com.example.tangochoupdated.ViewModelFactory

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    lateinit var appearAnimator:AnimatorSet

    private val sharedViewModel: BaseViewModel by activityViewModels()
    private lateinit var createViewModel:CreateViewModel

    private var bottomMenuVisible:Boolean = false

    private var addFileMenuVisible:Boolean = false
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository = (requireActivity().application as RoomApplication).repository
        val viewModelFactory = ViewModelFactory(repository)
        createViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[CreateViewModel::class.java]

        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.popupAddFile.visibility = View.INVISIBLE





        appearAnimator =AnimatorSet().apply{
            val a =ObjectAnimator.ofFloat(binding.frameBottomMenu, View.TRANSLATION_Y, 300f,0f)
            val b = ObjectAnimator.ofFloat(binding.bindingAddMenu.root, View.ALPHA,0f,1f)
            playTogether(a,b)
            duration = 500

        }
        appearAnimator.start()
        bottomMenuVisible = true
        binding.bindingAddMenu.root.children.iterator().forEachRemaining {
            it.setOnClickListener {
                onClickAddMenu(it)
            }
        }
        binding.bindingCreateFile.btnCreateFile.setOnClickListener {
            onClickCreateFile(it)
        }
        binding.root.setOnClickListener {
            finishWithAnimation()
//
        }
        return root
    }
    fun onClickAddMenu(v:View){
        appearAnimator.reverse()
        bottomMenuVisible = false
        when (v.id){
            binding.bindingAddMenu.imvnewfolder.id -> {
                sharedViewModel.fileStatus = FileStatus.FOLDER
                binding.popupAddFile.visibility= View.VISIBLE
                appearAnimator = AnimatorSet().apply {
                    duration= 500
                    play(ObjectAnimator.ofFloat(binding.popupAddFile, View.ALPHA, 0f,1f ))
                }
                addFileMenuVisible= true

            }
        }

        return
    }

    fun onClickCreateFile(v:View){
        when(v.id){
            binding.bindingCreateFile.btnCreateFile.id -> {
                sharedViewModel.title = binding.bindingCreateFile.edtCreatefile.text.toString()
                sharedViewModel.libOrder = 0
                sharedViewModel.insertFile()
                finishWithAnimation()

            }
        }
    }
    fun finishWithAnimation(){

        appearAnimator.addListener(object:AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
                requireActivity().supportFragmentManager.commit {
                    remove(this@CreateFragment)
                }
            }
        })
        appearAnimator.reverse()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}