package com.example.tangochoupdated.ui.create

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
import com.example.tangochoupdated.databinding.CreateCardStringBinding

class StringCardFragment : Fragment() {

    private var _binding: CreateCardStringBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = CreateCardStringBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}