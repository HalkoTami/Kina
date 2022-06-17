package com.example.tangochoupdated.ui.library

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.*

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.google.android.material.snackbar.Snackbar
import kotlin.math.abs

class HomeFragment : Fragment(),DataClickListener{



    lateinit var adapter:LibraryListAdapter


    lateinit var sharedViewModel: BaseViewModel
    private var _binding: FragmentLibraryHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel =
        ViewModelProvider(requireActivity(),
            ViewModelFactory((requireActivity().application as RoomApplication).repository)
        ).get(BaseViewModel::class.java)

    }
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



        sharedViewModel.finalList().observe(requireActivity()){
            adapter.submitList(it)
        }


        recyclerView?.layoutManager = LinearLayoutManager(context)





        return root


    }







    override fun onLongClickMain(type: LibRVViewType, id: Int) {
        Snackbar.make(binding.root,"hello",Snackbar.LENGTH_SHORT)
    }

    override fun onClickEdit(id: Int, viewId: Int) {
        Snackbar.LENGTH_SHORT
    }

    override fun onClickAdd(type: LibRVViewType, id: Int) {
        Snackbar.LENGTH_SHORT
    }

    override fun onClickDelete(type: LibRVViewType, id: Int) {
        Snackbar.LENGTH_SHORT
    }

    override fun onClickMain(type: LibRVViewType, id: Int) {
       binding.topMenuBarFrame.txvTitle.text = "hello"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}