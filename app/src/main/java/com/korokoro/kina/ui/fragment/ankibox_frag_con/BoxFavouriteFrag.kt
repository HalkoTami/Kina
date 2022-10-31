package com.korokoro.kina.ui.fragment.ankibox_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.korokoro.kina.databinding.FullRvBinding
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.customClasses.AnkiBoxFragments
import com.korokoro.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.korokoro.kina.ui.viewmodel.AnkiBoxViewModel
import com.korokoro.kina.ui.viewmodel.MainViewModel


class BoxFavouriteFrag  : Fragment() {

    private val args: BoxFavouriteFragArgs by navArgs()
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
        val fileId = args.fileId?.single()
        val fileIdIsNull = fileId == null
        val allFavouriteAnkiBoxFromDBObserver = Observer<List<Any>>{
            adapter.submitList(it)
            makeEmptyVisibleByListSize(it)
        }
        ankiBoxViewModel.setCurrentChildFragment(AnkiBoxFragments.Favourites)
        if(fileIdIsNull){
            ankiBoxViewModel.allFavouriteAnkiBoxFromDB.observe(viewLifecycleOwner,allFavouriteAnkiBoxFromDBObserver)
        } else {
            ankiBoxViewModel.getAnkiBoxRVCards(fileId!!).observe(viewLifecycleOwner,allFavouriteAnkiBoxFromDBObserver)
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}