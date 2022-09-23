package com.korokoro.kina.ui.fragment.ankibox_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.korokoro.kina.databinding.FullRvBinding
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.customClasses.AnkiBoxFragments
import com.korokoro.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.korokoro.kina.ui.viewmodel.AnkiBoxViewModel
import com.korokoro.kina.ui.viewmodel.MainViewModel


class BoxFavouriteFrag  : Fragment() {

    private var _binding: FullRvBinding? = null
    private val ankiBoxViewModel: AnkiBoxViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FullRvBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val viewSetUp = AnkiBoxFragViewSetUp()
        val adapter = viewSetUp
            .setUpAnkiBoxRVListAdapter(binding.recyclerView,requireActivity(),ankiBoxViewModel,
                AnkiBoxFragments.Favourites,viewLifecycleOwner)

        fun makeEmptyVisibleByListSize(list: List<Any>){
            binding.frameLayFullRvEmpty.visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
        }
        val allFavouriteAnkiBoxFromDBObserver = Observer<List<File>>{
            adapter.submitList(it)
            makeEmptyVisibleByListSize(it)
        }
        ankiBoxViewModel.setCurrentChildFragment(AnkiBoxFragments.Favourites)
        ankiBoxViewModel.allFavouriteAnkiBoxFromDB.observe(viewLifecycleOwner,allFavouriteAnkiBoxFromDBObserver)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}