package com.example.tangochoupdated.ui.create

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.example.tangochoupdated.databinding.*

class CreateCardFragment: Fragment() {
    private var _binding: CreateCardBaseBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CreateCardBaseBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return  root
    }


}