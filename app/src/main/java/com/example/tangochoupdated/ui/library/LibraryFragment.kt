package com.example.tangochoupdated.ui.library

import android.content.Context
import android.graphics.Color
import android.graphics.Color.rgb
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.LibraryListAdapter
import com.example.tangochoupdated.R
import com.example.tangochoupdated.RoomApplication
import com.example.tangochoupdated.UserListAdapter

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.job

class HomeFragment : Fragment(){

    private var _binding: FragmentLibraryHomeBinding? = null

    private val rvViewModel: LibraryViewModel by viewModels {
        LibraryViewModelFactory((requireActivity().application as RoomApplication).repository)
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this)[LibraryViewModel::class.java]

        _binding = FragmentLibraryHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.vocabCardRV
        val adapter = LibraryListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        rvViewModel.cardsWithoutParent.observe(requireActivity()){
            item -> item.let { adapter.submitList(it) }
        }


        val textView: EditText = binding.edtLibrarySearch
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.hint = it
        }
        val bnv :BottomNavigationView = requireActivity().findViewById(R.id.my_bnv)
        bnv.menu.getItem(0).setIcon(R.drawable.icon_library_active)
        val recyclerview= binding.vocabCardRV

        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(context)

        return root


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}