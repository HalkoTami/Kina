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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.*

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.ui.planner.CreateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.job

class HomeFragment : Fragment(),DataClickListener{

    val viewModel: LibraryRVViewModel by viewModels {
        ViewModelFactory((requireActivity().application as RoomApplication).repository)
    }
    private var _binding: FragmentLibraryHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        _binding = FragmentLibraryHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        viewModel.test.observe(requireActivity()){
            binding.edtLibrarySearch.text = it.title!!
        }


        val bnv :BottomNavigationView = requireActivity().findViewById(R.id.my_bnv)
        bnv.menu.getItem(0).setIcon(R.drawable.icon_library_active)


        return root


    }


    override fun onTouchWhole(id: Int, viewType: LibRVViewType) {
        TODO("Not yet implemented")
    }

    override fun onTouchDelete(id: Int, viewType: LibRVViewType) {
        TODO("Not yet implemented")
    }

    override fun onTouchMain() {
        TODO("Not yet implemented")
    }

    override fun onLongClickMain() {
        TODO("Not yet implemented")
    }

    override fun onClickEdit() {
        TODO("Not yet implemented")
    }

    override fun onClickTags() {
        TODO("Not yet implemented")
    }

    override fun oncClickSelect() {
        TODO("Not yet implemented")
    }

    override fun onClickAdd() {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}