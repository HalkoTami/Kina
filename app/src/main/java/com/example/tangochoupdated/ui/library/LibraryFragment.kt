package com.example.tangochoupdated.ui.library

import android.content.Context
import android.content.Intent
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
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.planner.CreateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class HomeFragment : Fragment(),DataClickListener{
    lateinit var adapter:LibraryListAdapter

    private val viewModel: LibraryRVViewModel by lazy { ViewModelProvider(this, ViewModelFactory(
        (requireActivity().application as RoomApplication).repository
    )).get(LibraryRVViewModel::class.java) }
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



        val recyclerView = _binding?.vocabCardRV
        adapter = LibraryListAdapter(this)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)

        testCoroutine()

        val bnv :BottomNavigationView = requireActivity().findViewById(R.id.my_bnv)
        bnv.menu.getItem(0).setIcon(R.drawable.icon_library_active)


        return root


    }
    private fun testCoroutine() {
        viewModel.viewModelScope.launch {
            // ここはメインスレッドで呼び出される

            // asyncTask() は suspend 関数なので、呼び出してもメインスレッドをブロックしない
            val result = asyncTask()

            // asyncTask() が終了したら、メインスレッドでこのブロックが実行される
            adapter.submitList(result)
        }
    }
    private suspend fun asyncTask(): List<LibraryRV> =  withContext(Dispatchers.Default) {
        val list = RoomApplication().repository.getLibRVCover(null)
        // ここは DefaultDispatcher-worker-X というバックグラウンドスレッドで呼び出される
        return@withContext list
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