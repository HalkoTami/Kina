package com.example.tangochoupdated.ui.anki

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.tangochoupdated.ui.mainactivity.BaseViewModel
import com.example.tangochoupdated.databinding.FragmentAnkiHomeBinding


class AnkiFragment  : Fragment() {

    private var _binding: FragmentAnkiHomeBinding? = null
    private val sharedViewModel: BaseViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ankiViewModel =
            ViewModelProvider(this)[AnkiViewModel::class.java]

        _binding = FragmentAnkiHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        ankiViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}