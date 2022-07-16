package com.example.tangochoupdated.ui.create.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.tangochoupdated.RoomApplication
import com.example.tangochoupdated.ViewModelFactory
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.ui.library.HomeFragmentArgs

class CreateCardFragment: Fragment() {
    private var _binding: CreateCardBaseBinding? = null
    private val binding get() = _binding!!
    private val args: CreateCardFragmentArgs by navArgs()

    private lateinit var createCardViewModel: CreateCardViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repository = (requireActivity().application as RoomApplication).repository
        val viewModelFactory = ViewModelFactory(repository)
        createCardViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[CreateCardViewModel::class.java]

        _binding = CreateCardBaseBinding.inflate(inflater, container, false)

        createCardViewModel.apply{
            getParentCard(args.cardId.single()).observe(requireActivity()){
                setParentCard(it)
            }
            getParentFlashCardCover(args.parentFlashCardCoverId.single()).observe(requireActivity()){
                setParentFlashCardCover(it)
            }






            return binding.root
        }
    }


}