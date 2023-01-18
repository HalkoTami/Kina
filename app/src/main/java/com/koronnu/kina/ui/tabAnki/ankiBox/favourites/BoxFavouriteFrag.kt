package com.koronnu.kina.ui.tabAnki.ankiBox.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.koronnu.kina.databinding.FullRvBinding
import com.koronnu.kina.customClasses.enumClasses.AnkiBoxFragments
import com.koronnu.kina.ui.view_set_up.AnkiBoxFragViewSetUp
import com.koronnu.kina.ui.viewmodel.AnkiBoxViewModel


class BoxFavouriteFrag  : Fragment() {

    private val args: com.koronnu.kina.ui.tabAnki.ankiBox.favourites.BoxFavouriteFragArgs by navArgs()
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