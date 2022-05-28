package com.example.tangochoupdated.ui.library

import android.graphics.Color
import android.graphics.Color.rgb
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tangochoupdated.R

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private var _binding: FragmentLibraryHomeBinding? = null
    val bnv :BottomNavigationView = requireActivity().findViewById(R.id.my_bnv)
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentLibraryHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: EditText = binding.edtLibrarySearch
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.hint = it
        }
        bnv.menu.getItem(0).setIcon(R.drawable.icon_library_active)
        return root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        bnv.menu.getItem(0).setIcon(R.drawable.icon_library_plane)
        _binding = null
    }
}