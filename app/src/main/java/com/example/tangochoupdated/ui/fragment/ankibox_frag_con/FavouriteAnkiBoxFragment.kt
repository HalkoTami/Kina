package com.example.tangochoupdated.ui.fragment.ankibox_frag_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.tangochoupdated.databinding.FullRvBinding
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiBoxFragments
import com.example.tangochoupdated.ui.view_set_up.AnkiBoxFragViewSetUp
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.BaseViewModel


class FavouriteAnkiBoxFragment  : Fragment() {

    private var _binding: FullRvBinding? = null
    private val sharedViewModel: BaseViewModel by activityViewModels()
    private val ankiBoxViewModel: AnkiBoxFragViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
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
        ankiBoxViewModel.apply {
            setCurrentChildFragment(AnkiBoxFragments.Favourites)
            allFavouriteAnkiBoxFromDB.observe(viewLifecycleOwner){
                adapter.submitList(it)
                makeEmptyVisibleByListSize(it)
                Toast.makeText(requireActivity(),it.size.toString(),Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}