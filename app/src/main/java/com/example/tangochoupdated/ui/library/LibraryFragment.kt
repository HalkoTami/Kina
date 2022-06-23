package com.example.tangochoupdated.ui.library

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangochoupdated.*

import com.example.tangochoupdated.databinding.FragmentLibraryHomeBinding
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
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
            sharedViewModel.libOrder = adapter.itemCount
        }



        recyclerView?.layoutManager = LinearLayoutManager(context)





        return root


    }



    override fun onLongClickMain() {
        sharedViewModel.finalList().observe(requireActivity()){
         it.onEach { it.selectable = true }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onClickEdit(item: LibraryRV) {
        Toast.makeText(context, "onclick edit", Toast.LENGTH_SHORT).show()

    }

    override fun onClickAdd(item: LibraryRV) {
        Toast.makeText(context, "onClickAdd", Toast.LENGTH_SHORT).show()
    }

    override fun onClickDelete(item: LibraryRV) {
        Toast.makeText(context, "onClickDelete", Toast.LENGTH_SHORT).show()
    }

    override fun onClickMain(item: LibraryRV) {
        Toast.makeText(context, "onClickMain", Toast.LENGTH_SHORT).show()
    }

    override fun onSelect(item: LibraryRV) {
        Toast.makeText(context, "onSelect", Toast.LENGTH_SHORT).show()
        sharedViewModel.finalList().observe(requireActivity()) {
            it[item.position].selected = true
            adapter.notifyItemChanged(item.position)
        }

    }

    override fun onUnselect(item: LibraryRV) {
        Toast.makeText(context, "onUnselect", Toast.LENGTH_SHORT).show()
        sharedViewModel.finalList().observe(requireActivity()) {
            it[item.position].selected = false
            adapter.notifyItemChanged(item.position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
        }
